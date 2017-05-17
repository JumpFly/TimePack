package com.common.Tools;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader; 

public  class configUtils {
 private configUtils(){}
 private static HashMap<String, String> configTagsValue=new HashMap<>();
 static {
	 getAllMsgArgs();
 }
 private static void getAllMsgArgs(){
	 try {
		 File file=new File("config.xml");
	 		SAXReader reader = new SAXReader();
			Document doc=reader.read(file);
			Element root = doc.getRootElement();
			Element foo ,foo2;
			foo=root.element("MsgArgs");
			List<Element> nodes=foo.elements();
			for(int j=0;j<nodes.size();j++){
				foo2=nodes.get(j);
				configTagsValue.put(foo2.getName(), foo2.getText());
			}
			foo=root.element("ServerAddress"); 
			configTagsValue.put(foo.getName(), foo.getText());
			foo=root.element("ServerPort"); 
			configTagsValue.put(foo.getName(), foo.getText());
			
			foo=root.element("RedisAddress"); 
			configTagsValue.put(foo.getName(), foo.getText());
			foo=root.element("RedisPort"); 
			configTagsValue.put(foo.getName(), foo.getText());
			foo=root.element("WorkQueues"); 
			configTagsValue.put(foo.getName(), foo.getText());
			
			
	 	} catch (Exception e) {
	 		e.printStackTrace();
	 	}
	
 }
 public static String[] getWorkQueues(){
	 if(configTagsValue.containsKey("WorkQueues"))
		 return configTagsValue.get("WorkQueues").split(",");
	 else  
		 return null;
 }
 public static String getServerAddr(){
	 if(configTagsValue.containsKey("ServerAddress"))
		 return configTagsValue.get("ServerAddress");
	 else  
		 return null;
 }
 public static String getServerPort(){
	 if(configTagsValue.containsKey("ServerPort"))
		 return configTagsValue.get("ServerPort");
	 else  
		 return null;
 }
 public static String getRedisAddr(){
	 if(configTagsValue.containsKey("RedisAddress"))
		 return configTagsValue.get("RedisAddress");
	 else  
		 return null;
 }
 public static String getRedisPort(){
	 if(configTagsValue.containsKey("RedisPort"))
		 return configTagsValue.get("RedisPort");
	 else  
		 return null;
 }
 public static String getHeartPongMsgArgs(){
	 if(configTagsValue.containsKey("heartPongMsgArgs"))
		 return configTagsValue.get("heartPongMsgArgs");
	 else  
		 return null;
 }
 public static String getConnectMsgArgs(){
	 if(configTagsValue.containsKey("connectMsgArgs"))
		 return configTagsValue.get("connectMsgArgs");
	 else  
		 return null;
 }
 public static String getHeartBeatMsgArgs(){
	 if(configTagsValue.containsKey("heartBeatMsgArgs"))
		 return configTagsValue.get("heartBeatMsgArgs");
	 else  
		 return null;
 }
 
}
