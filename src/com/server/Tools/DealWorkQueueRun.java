package com.server.Tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.common.Tools.configUtils;
import com.server.QueueBean;

import redis.clients.jedis.Jedis;

public class DealWorkQueueRun implements Runnable{
	 private Jedis conn;
	 private String[] queues;
     private volatile boolean quit;
     
     public DealWorkQueueRun(){
    	 conn=RedisUtils.getPoolConnection();
 		queues=configUtils.getWorkQueues();
     }
     public void quit() {
         quit = true;
         if(conn!=null)
         RedisUtils.ReleaseConn(conn);
     }
     
	@Override
	public void run() {
		while(!quit){
			List<String> work=conn.blpop(60,queues);
			if(work==null)
				continue;
			String qBeanJSON=work.get(1);
			QueueBean qBean=JSON.parseObject(qBeanJSON,QueueBean.class);
			String dealFunName=qBean.getCallBackFun();
			
			try {
				Class<?> clazz = Class.forName(WorkUtils.class.getName());
				Method method = clazz.getMethod(dealFunName,qBean.getDataClass());
				Object object=JSON.parseObject(qBean.getData(),qBean.getDataClass());
				method.invoke(clazz.newInstance(),object);
			} catch (Exception e) {
				if(conn!=null)
				RedisUtils.ReleaseConn(conn);	
				e.printStackTrace();
			} 
		}
	}

}
