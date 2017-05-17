package com.server.Tools;

import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.server.DelayQueueBean;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

public class PollQueueRun implements Runnable{
	 private Jedis conn;
     private volatile boolean quit;
     public PollQueueRun() {
     	 conn=RedisUtils.getPoolConnection();
     }
     public void quit() {
         quit = true;
     }
	@Override
	public void run() {
		while(!quit){
			 Set<Tuple> items=conn.zrangeWithScores("delayed:",0,0);
			 Tuple item=items.size()>0?items.iterator().next():null;
			 if(item==null||item.getScore()>System.currentTimeMillis()){
				try {
					Thread.sleep(500);
				} catch(InterruptedException ie){
                   Thread.interrupted();
               }
               continue;
			}
			 String dqBeanJSON=item.getElement();
			DelayQueueBean delayQueueBean=JSON.parseObject(dqBeanJSON,DelayQueueBean.class);
			String identifier=delayQueueBean.getIdentifier();
			String Locked=RedisLock.acquire_lock(conn,identifier);
			if(Locked==null)
				continue;
			if(conn.zrem("delayed:",dqBeanJSON)==1)
				conn.rpush("queue:"+delayQueueBean.getQueueName(),dqBeanJSON);
			RedisLock.release_lock(conn, identifier,Locked);
		}
	         RedisUtils.ReleaseConn(conn);
	}
     
}
