package com.server.Tools;

import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class RedisLock {
    private static Logger logger=LogManager.getLogger(RedisLock.class.getName());
    /** Ĭ�ϳ�ʱʱ�䣨���룩 */  
    public static final long DEFAULT_TIME_OUT = 10000;  
    /** ���ĳ�ʱʱ�䣨�룩������ɾ�� */  
    public static final int EXPIRE = 60;  
    /*����ǰ׺*/
    private static String lockpre="lock:";  
    
    public static String acquire_lock(Jedis conn, String lockName){
    	return acquire_lock(conn,lockName,DEFAULT_TIME_OUT);
    }
    public static String acquire_lock(Jedis conn, String lockName, long acquire_MilliTimeout){
        String identifier = UUID.randomUUID().toString();
        String lockKey=RedisLock.lockpre+lockName; 
        long end = System.currentTimeMillis() + acquire_MilliTimeout;
        while (System.currentTimeMillis() < end){
            if (conn.setnx(lockKey, identifier) == 1){
                return identifier;
            }

            try {
                Thread.sleep(500);
            }catch(InterruptedException ie){
                Thread.currentThread().interrupt();
            }
        }

        return null;
    }
    public static String acquire_lock_with_timeout(Jedis conn, String lockName){
    	return acquire_lock_with_timeout(conn, lockName, DEFAULT_TIME_OUT, EXPIRE);
    }
    
    public static String acquire_lock_with_timeout(Jedis conn, String lockName,long acquire_MilliTimeout, int lock_expire) {  
    	String identifier = UUID.randomUUID().toString();
    	 String lockKey=RedisLock.lockpre+lockName; 
    	long end=System.currentTimeMillis()+acquire_MilliTimeout;
        
            while (System.currentTimeMillis() < end) {  
                if (conn.setnx(lockKey, identifier) == 1) {  
                    conn.expire(lockKey, lock_expire);  
                    return  identifier;  
                }else if (conn.ttl(lockKey)<0) {
                	conn.expire(lockKey, lock_expire);  
				}  
                try {  
                /** �������ߣ�������ֻ���  */
                Thread.sleep(500);  
                } catch (Exception e) {  
                	logger.error("Locking error");
                    throw new RuntimeException("Locking error", e);  
                } 
            }  
        
        return null;  
    } 
    
    public static boolean release_lock(Jedis conn, String lockName, String Lockedidentifier) {
        String lockKey = RedisLock.lockpre+lockName;
        while (true){
            conn.watch(lockKey);
            if (Lockedidentifier.equals(conn.get(lockKey))){
                Transaction trans = conn.multi();
                trans.del(lockKey);
                List<Object> results = trans.exec();
                if (results == null){
                    continue;
                }
                return true;
            }

            conn.unwatch();
            break;
        }
        return false;
    }
    
}
