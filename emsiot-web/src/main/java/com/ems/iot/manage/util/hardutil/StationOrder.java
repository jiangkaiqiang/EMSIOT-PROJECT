package com.ems.iot.manage.util.hardutil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.prefixedstring.PrefixedStringCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;



import com.ems.iot.manage.dao.ElectrombileStationMapper;
import com.ems.iot.manage.entity.ElectrombileStation;
import com.ems.iot.manage.util.BeanUtil;


public class StationOrder {
	private static final int PORT = 10921;
	
	public static void main(String[] args) {
		StationOrder a=new StationOrder();
		a.stationNormallyStatus(13493);
		
	}

	
	public void minaConnector() {
		// 创建客户端连接
        IoConnector connector = new NioSocketConnector();
        // 增加日志过滤器
        connector.getFilterChain().addLast("logger", new LoggingFilter());
        // 增加编码过滤器，统一编码UTF-8
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter((ProtocolCodecFactory) new PrefixedStringCodecFactory(Charset.forName("UTF-8"))));
        // 设置客户端逻辑处理器
        connector.setHandler(new ClientHandler());
        // 连接
        ConnectFuture connectFuture = connector.connect(new InetSocketAddress("47.100.242.28", PORT));
        // 等待建立连接
        connectFuture.awaitUninterruptibly();
        // 获取连接会话
        IoSession session = connectFuture.getSession();
        
        
	}
	
	/*
	 * 阅读器正常工作
	 * 
	 * 
	 * */
	public void stationNormallyStatus(Integer stationNum) {
		
		byte[] bt=new byte[18];
	    bt[0] = (byte) 0xFF;
	    bt[1] = (byte) 0xFF;
	    bt[2] = (byte) 0xFF;
	    bt[3] = (byte) 0xFF;
	    
	    bt[4] = (byte) 0x2A;
	    bt[5] = (byte) 0x78;
	    
	    bt[6] = (byte) 0xC1;
	    bt[7] = (byte) 0x02;
	    
	    bt[8] = (byte) 0x00;
	    bt[9] = (byte) 0x08;
		
	    String equNum = Integer.toHexString(stationNum).toString();
	    StringBuffer sbfEquNum = new StringBuffer();
	    if(equNum.length()!=8){
			
			for (int i = 0; i < 8-equNum.length(); i++) {
				sbfEquNum.append("0");
			}
		}
	    sbfEquNum.append(equNum);
		//计算校验码      
		byte[] checkbt =  getCheckNum("4081"+sbfEquNum.toString());
		bt[10] = checkbt[0];
	    bt[11] = checkbt[1];
	    
	    bt[12] = checkbt[2];
	    
	    bt[13] = checkbt[3];
	    
	    bt[14] = checkbt[4];
	    bt[15] = checkbt[5];
	    bt[16] = checkbt[6];
	    bt[17] = checkbt[7];
	    
	    System.err.println(ByteAndStr16.Bytes2HexString(bt));
	   
	}
	
	
	/**
	 * 计算校验码(2个字节)
	 * 帧选项 0x40 命令代码0x81 设备地址0x00 0x00 0x34 00xB5
	 * @param 设备地址 例如：13493
	 * @return
	 */
	public byte[] getCheckNum(String dataCheck){
		
		
		//sbfEquNum.append(equNum);
		//String dataCheck = "4081"+sbfEquNum.toString();
		//将需要校验的16进制字符串转化为字节数组
		byte[] dataBy = ByteAndStr16.HexString2Bytes(dataCheck);
		//计算校验码
		int checkNumInt = CRC16B.getCRC1021(dataBy);
		String checkStr = Integer.toHexString(checkNumInt);
		StringBuffer sbf = new StringBuffer();
		//校验码为2个字节，16进制4位，不足四位前面补零
		if(checkStr.length()!=4){
			
			for (int i = 0; i < 4-checkStr.length(); i++) {
				sbf.append("0");
			}
			
		}
		sbf.append(checkStr);
		
		//将校验码转化为字节数组 
		byte[] checkbt = ByteAndStr16.HexString2Bytes(sbf.toString());
		byte[] resBt = new byte[8];
		resBt[0]=checkbt[0];
		resBt[1]=checkbt[1];
		for (int i = 0; i < dataBy.length; i++) {
			resBt[i+2]=dataBy[i];
		}
		return resBt;
	}
	
	
	public void test() {
		ElectrombileStationMapper electr = (ElectrombileStationMapper)BeanUtil.getBean("electrombileStationMapper");
		
		ElectrombileStation ele=new ElectrombileStation();
		
		ele.setEle_gua_card_num(123456);
		ele.setStation_phy_num(111111);
		ele.setHard_read_time("2018-12-21 12:00:00");
		ele.setUpdate_time("2018-12-21 12:00:00");
		
		electr.insert(ele);
	}
}
