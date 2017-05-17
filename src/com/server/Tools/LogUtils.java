package com.server.Tools;

import java.net.SocketAddress;
import java.util.Date;

import org.apache.logging.log4j.Logger;

import com.common.MsgType;

public class LogUtils {
	public static void QuitDealLog(Logger logger,SocketAddress clientAddr,MsgType quitType){
		   switch (quitType) {
			case Quit:
				logger.info(clientAddr+"Quit");
				break;
			case heartDeadQuit:
				logger.info(clientAddr+" Quit With HeartDead(长时无响应)");
				break;
			default:
				break;
			}
	}
}
