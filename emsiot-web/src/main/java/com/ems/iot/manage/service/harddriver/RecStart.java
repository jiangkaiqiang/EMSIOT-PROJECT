package com.ems.iot.manage.service.harddriver;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;


/**
 * 秀派设备接收器(桂林路)
 * @author Administrator
 *
 */
public class RecStart{
	
	static int count = 0;
	//@PostConstruct
	public void executeRfid() throws Exception {
//		StartActivity act = new StartActivity();
        //监听秀派读卡器 触发器判断进出
        NioSocketAcceptor acceptorXPNew = new NioSocketAcceptor();  
        ServerXPDataHandlerThird handle = new ServerXPDataHandlerThird();
        acceptorXPNew.setHandler(handle);
        try {		
			System.out.println("haolidong:");
			//桂林路卡口秀派（根据触发器判断）
			acceptorXPNew.bind(new InetSocketAddress(13100));
		} catch (IOException e) {
	
			e.printStackTrace();
		}     

	}
	
	
	
	
	
	
	
}


