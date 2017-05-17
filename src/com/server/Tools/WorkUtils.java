package com.server.Tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.common.ChatMessages;
import com.common.MsgType;
import com.common.work.ChatWork;
import com.common.work.CreateChatWork;
import com.common.work.EmailWork;
import com.common.work.MessageWork;
import com.server.DelayQueueBean;
import com.server.HeartBeatServerHandler;
import com.server.QueueBean;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

public class WorkUtils {
 private  final static  Logger logger = LogManager.getLogger(WorkUtils.class.getName());
	public static String callBackFun(MsgType mType){
		return "deal"+mType;
	}
	public static void execute_later(String queueName,String dealFunName,String data,Class dataClass,int delay){
		Jedis conn=RedisUtils.getPoolConnection();
		
		String identifier=UUID.randomUUID().toString();
		DelayQueueBean dqBean=new DelayQueueBean();
		dqBean.setIdentifier(identifier);
		dqBean.setCallBackFun(dealFunName);
		dqBean.setDataClass(dataClass);
		dqBean.setData(data);
		dqBean.setQueueName(queueName);
		String dqBeanJSON=JSON.toJSONString(dqBean);
		if(delay>0){
			conn.zadd("delayed:",System.currentTimeMillis()+delay*1000,dqBeanJSON);
		}else{
//			QueueBean qBean=new QueueBean();
//			qBean.setCallBackFun(dealFunName);
//			qBean.setDataClass(dataClass);
//			qBean.setData(data);
//			String qBeanJSON=JSON.toJSONString(qBean);
			conn.rpush("queue:"+queueName,dqBeanJSON);
		}
		RedisUtils.ReleaseConn(conn);
	}
	
    public static String dealcreateChat(CreateChatWork createCWork) { 
        String chatId =null;
        return createChat(createCWork, chatId);
    }

    private static  String createChat(CreateChatWork createCWork, String chatId){
    	Jedis conn=RedisUtils.getPoolConnection();
        if(null==chatId){
        	  chatId = String.valueOf(conn.incr("ids:chat:"));
        }
        Set<String>  recipients= createCWork.getReceivers();
    	recipients.add(createCWork.getSender());
        Transaction trans = conn.multi();
        for (String recipient : recipients){
            trans.zadd("chat:" + chatId, 0, recipient);
            trans.zadd("seen:" + recipient, 0, chatId);
        }
        trans.exec();
    	RedisUtils.ReleaseConn(conn);
    	if(createCWork.getMsgData()!=null||!createCWork.getMsgData().equals("")){
    		ChatWork Cwork=new ChatWork();
    		Cwork.setChatId(Integer.parseInt(chatId));
    		Cwork.setMsgData(createCWork.getMsgData());
    		Cwork.setSender(createCWork.getSender());
    		Cwork.setSend_time(createCWork.getSend_time());
    		return dealsendChat(Cwork);
    	}else {
			return chatId;
		}
    }

    public static String dealsendChat(ChatWork Cwork) {
    	String chatId=null;
    		chatId=String.valueOf(Cwork.getChatId());
    	Jedis conn=RedisUtils.getPoolConnection();
        String Lockedidentifier = RedisLock.acquire_lock(conn, "chat:" + chatId);
        if (Lockedidentifier == null){
        	logger.error("dealsendChat : Couldn't get the lock");
            throw new RuntimeException("Couldn't get the lock");
        }
        try {
            long messageId = conn.incr("ids:" + chatId);
//            HashMap<String,Object> values = new HashMap<String,Object>();
//           values.put("id", messageId);
//            values.put("ts", System.currentTimeMillis());
//            values.put("sender", sender);
//            values.put("message", message);
//            String packed = new Gson().toJson(values);
            MessageWork Mwork=new MessageWork();
            Mwork.setMessageId((int)messageId);
            Mwork.setMsgData(Cwork.getMsgData());
            Mwork.setSender(Cwork.getSender());
            Mwork.setSend_time(Cwork.getSend_time());
            String packed=JSON.toJSONString(Mwork);
            conn.zadd("msgs:" + chatId, messageId, packed);
        }finally{
            RedisLock.release_lock(conn, "chat:" + chatId, Lockedidentifier);
        	RedisUtils.ReleaseConn(conn);
        }
        return chatId;
    }
	
	public static void dealsendEmail(EmailWork Ework){
		System.out.println(Ework);
		fun();
	}
	
	public static void fun(){
		  List<ChatMessages> r1 = fetchPendingMessages("Barry");
	       List<ChatMessages> r2 = fetchPendingMessages("John");
	       for(ChatMessages cm:r1){
	    	   List<MessageWork> list1= cm.getMessages();
	    	   for(MessageWork mw:list1){
	    		   System.out.println(mw.getSender()+" --> "+mw.getMsgData());
	    	   }
	       }
	       for(ChatMessages cm:r2){
	    	   List<MessageWork> list1= cm.getMessages();
	    	   for(MessageWork mw:list1){
	    		   System.out.println(mw.getSender()+" --> "+mw.getMsgData());
	    	   }
	       }
	}
	
	
	 public static List<ChatMessages> fetchPendingMessages(String recipient) {
		 Jedis conn=RedisUtils.getPoolConnection();
	        Set<Tuple> seenSet = conn.zrangeWithScores("seen:" + recipient, 0, -1);
	        List<Tuple> seenList = new ArrayList<Tuple>(seenSet);

	        Transaction trans = conn.multi();
	        for (Tuple tuple : seenList){
	            String chatId = tuple.getElement();
	            int seenId = (int)tuple.getScore();
	            trans.zrangeByScore("msgs:" + chatId, String.valueOf(seenId + 1), "inf");
	        }
	        List<Object> results = trans.exec();

	        Iterator<Tuple> seenIterator = seenList.iterator();
	        Iterator<Object> resultsIterator = results.iterator();

	        List<ChatMessages> chatMessages = new ArrayList<ChatMessages>();
	        List<Object[]> seenUpdates = new ArrayList<Object[]>();
	        List<Object[]> msgRemoves = new ArrayList<Object[]>();
	        while (seenIterator.hasNext()){
	            Tuple seen = seenIterator.next();
	            Set<String> messageStrings = (Set<String>)resultsIterator.next();
	            if (messageStrings.size() == 0){
	                continue;
	            }

	            int seenId = 0;
	            String chatId = seen.getElement();
	            List<MessageWork> messages = new ArrayList<MessageWork>();
	            for (String messageJson : messageStrings){
	            	MessageWork message = JSON.parseObject(messageJson, MessageWork.class);
	                int messageId = message.getMessageId();
	                if (messageId > seenId){
	                    seenId = messageId;
	                }
	            //    message.put("id", messageId);
	                messages.add(message);
	            }

	            conn.zadd("chat:" + chatId, seenId, recipient);
	            seenUpdates.add(new Object[]{"seen:" + recipient, seenId, chatId});

	            Set<Tuple> minIdSet = conn.zrangeWithScores("chat:" + chatId, 0, 0);
	            if (minIdSet.size() > 0){
	                msgRemoves.add(new Object[]{
	                    "msgs:" + chatId, minIdSet.iterator().next().getScore()});
	            }
	            chatMessages.add(new ChatMessages(chatId, messages));
	        }
	        trans = conn.multi();
	        for (Object[] seenUpdate : seenUpdates){
	            trans.zadd(
	                (String)seenUpdate[0],
	                (Integer)seenUpdate[1],
	                (String)seenUpdate[2]);
	        }
	        for (Object[] msgRemove : msgRemoves){
	            trans.zremrangeByScore(
	                (String)msgRemove[0], 0, ((Double)msgRemove[1]).intValue());
	        }
	        trans.exec();
	        
	      	RedisUtils.ReleaseConn(conn);
	        return chatMessages;
	    }
	 public static boolean dealjoinChat(String chatId,String user){
		 boolean Done=false;
		 Jedis conn=RedisUtils.getPoolConnection();
		 if(!conn.exists("ids:"+chatId)){
			 return false;
		 }
		 int messageId=Integer.parseInt(conn.get("ids:"+chatId));
		 Transaction trans = conn.multi();
		  trans.zadd("chat:" + chatId, messageId, user);
          trans.zadd("seen:" + user, messageId, chatId);
		 trans.exec();
		 Done=true;
		 RedisUtils.ReleaseConn(conn);
		 return Done;
	 }
	 public static boolean dealleaveChat(String chatId,String user){
		 boolean Done=false;
		 Jedis conn=RedisUtils.getPoolConnection();
		 if(!conn.exists("chat:"+chatId)){
			 return false;
		 }
		 Transaction trans = conn.multi();
		 trans.zrem("chat:"+chatId, user);
		 trans.zrem("seen:"+user, chatId);
		 trans.zcard("chat:"+chatId);
		 List<Object> res=trans.exec();
		 if((int)res.get(res.size()-1)==0){
			 trans= conn.multi();
			 trans.del("msgs:"+chatId,"ids:"+chatId);
			 trans.exec();
		 }else {
			Set<Tuple> oldest=conn.zrangeWithScores("chat:"+chatId, 0, 0);
			conn.zremrangeByScore("chat:"+chatId, 0, oldest.iterator().next().getScore());
		 }
		 Done=true;
		 return Done;
	 }
}
