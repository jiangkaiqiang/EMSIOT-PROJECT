package com.ems.iot.manage.service.harddriver;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.ems.iot.manage.util.hardutil.ByteAndStr16;
import com.ems.iot.manage.util.hardutil.CRC16B;
import com.ems.iot.manage.util.hardutil.DataAnalysis;
import com.ems.iot.manage.util.hardutil.DataParmUtil;
import com.ems.iot.manage.util.hardutil.HandleInterface;

/**
 * 秀派读卡器处理程序（根据触发器判断进出）
 * @author Administrator
 *
 */

public class ServerXPDataHandlerThird implements IoHandler, HandleInterface{
	
	//记录上次的数据
	private Map<String, String> temporaryData = new HashMap<String, String>(60);
	
	//客户端界面
	
	public ServerXPDataHandlerThird() {

	}
	
	
	
	public Map<String, String> getLabelDatas(){
		return temporaryData;
	}
	
	
	@Override
	public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception {
		System.out.println("exceptionCaught");
		
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		String clientIP=((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
		System.out.println(clientIP);
		//待删除的键（把有00000的删除）
		List<String> keys = new ArrayList<String>();
		
		//mina接收16进制的数据
 	    IoBuffer bbuf = (IoBuffer) message;  

        byte[] byten = new byte[bbuf.limit()];  
        bbuf.get(byten, bbuf.position(), bbuf.limit());
        //将字节数组转化为16进制字符串
        String sensorData=ByteAndStr16.Bytes2HexString(byten);
        System.err.println("sensorData："+sensorData);
        String[] sensorDataArray = sensorData.split(" ");
        String eqNum = sensorDataArray[14]+sensorDataArray[15]+sensorDataArray[16]+sensorDataArray[17];
        //基站编号
        Integer eqNumInt = Integer.valueOf(eqNum, 16);
        String equIp = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
        
        if(byten.length!=18){
        	//数据解析：标签号-时间（毫秒数）
        	Map<String, String> res = DataAnalysis.getNewTrigger(sensorData);
        	System.out.println(res);
        	
        	//需要存放到数据库中的数据 卡号，读卡器号，时间
        	List<String[]> temDb = new ArrayList<String[]>();
        	for (Map.Entry<String, String> entry  : res.entrySet()) {
        		String label = entry.getKey();
				String labelData = entry.getValue();
				
					//是否有上次记录
					if (temporaryData.containsKey(label)) {
						//上次记录与本次记录的基站编号是否相同
						if (!temporaryData.get(label).equals(eqNumInt.toString())) {
		        			temporaryData.put(label, eqNumInt.toString());
		        			
		        			temDb.add(new String[]{label, eqNumInt.toString(),labelData});
		        			System.err.println("标签号+基站编号+接收时间："+label +" "+ eqNumInt.toString() +" "+ labelData);	
						} else {
							continue;
						}
					} else {
						temDb.add(new String[]{label, eqNumInt.toString(),labelData});
						System.err.println("标签号+基站编号+接收时间："+label +" "+ eqNumInt.toString() +" "+ labelData);
						temporaryData.put(label, eqNumInt.toString());
					}
				
        	}
        	saveDataRedis(temDb, equIp);

        	//系统受到数据后发送确认指令
        	String[] str = sensorData.split(" ");
        	
        	//吸顶式阅读器编码指令
        	//String sendStrFirst = "FFFFFFFF3A59C1020009"; 
        	//String sendStrEnd="4008"+str[14]+str[15]+str[16]+str[17]+str[18];
        	//固定式阅读器编码指令
        	String sendStrFirst = "FFFFFFFF1A1BC102000B"; 
        	String sendStrEnd="4008"+str[14]+str[15]+str[16]+str[17]+str[18]+str[19]+str[20];
        	//计算sendStrEnd的校验码
        	int checkInt = CRC16B.getCRC1021(ByteAndStr16.HexString2Bytes(sendStrEnd));
        	String checkStr = Integer.toHexString(checkInt);
        	StringBuffer sbf = new StringBuffer();
        	//16进制字符串不足4位，前面补零
      	  	if(checkStr.length()!=4){
      		  
      		  for (int i = 0; i < 4-checkStr.length(); i++) {
      			sbf.append("0");
      		  }
      		  
      	  	}
      	  	sbf.append(checkStr);
        	
        	String sendRes = sendStrFirst+sbf.toString()+sendStrEnd;
//        	System.out.println("发送消息："+sendRes);
        	byte[] sendResBt = ByteAndStr16.HexString2Bytes(sendRes);
        	  IoBuffer iobuffer =IoBuffer.allocate(sendResBt.length);
              iobuffer.put(sendResBt);
              iobuffer.flip();
              session.write(iobuffer); //往客户端发送指令
        	
        	return;
        }
        
        
        /*收到呼吸声后向阅读器发送时间校验指令（固定式阅读器指令）*/
        byte[] bt=new byte[22];
        bt[0] = (byte) 0xFF;
        bt[1] = (byte) 0xFF;
        bt[2] = (byte) 0xFF;
        bt[3] = (byte) 0xFF;
        
        bt[4] = (byte) 0x6A;
        bt[5] = (byte) 0xFC;
        
        bt[6] = (byte) 0xC1;
        bt[7] = (byte) 0x02;
        
        bt[8] = (byte) 0x00;
        bt[9] = (byte) 0x0C;
        
        //计算校验码      
        byte[] checkbt =  getCheckNum((sensorDataArray[14]+sensorDataArray[15]+sensorDataArray[16]+sensorDataArray[17]));
        bt[10] = checkbt[0];
        bt[11] = checkbt[1];
        
        bt[12] = checkbt[2];
        
        bt[13] = checkbt[3];
        
        bt[14] = checkbt[4];
        bt[15] = checkbt[5];
        
        bt[16] = checkbt[6];
        bt[17] = checkbt[7];
        bt[18] = checkbt[8];
        bt[19] = checkbt[9];
        bt[20] = checkbt[10];
        bt[21] = checkbt[11];
        
        
        IoBuffer iobuffer =IoBuffer.allocate(bt.length);
        iobuffer.put(bt);
        iobuffer.flip();
        session.write(iobuffer); //往客户端发送指令
        
	
		
	}

	@Override
	public void messageSent(IoSession arg0, Object arg1) throws Exception {
		System.out.println("messageSent");
			
	}

	@Override
	public void sessionClosed(IoSession arg0) throws Exception {
		System.out.println("sessionClosed");
		
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("IP:" + session.getRemoteAddress().toString());
		
	}

	@Override
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
		System.out.println("sessionIdle");
		
	}

	@Override
	public void sessionOpened(IoSession arg0) throws Exception {
		System.out.println("sessionOpened");
		
	}

	
	/**
	 * 计算校验码(2个字节)
	 * 帧选项 0x40 命令代码0x06 设备地址0x00 0x37 时间戳4个字节
	 * @param 设备地址 例如：0037
	 * @return
	 */
	public byte[] getCheckNum(String equNum){
			//获取当前时间戳的16进制字符串
		 	String currentDate = Long.toHexString(System.currentTimeMillis()/1000);
	        String dataCheck = "4006"+equNum+currentDate;
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
	        byte[] resBt = new byte[14];
	        resBt[0]=checkbt[0];
	        resBt[1]=checkbt[1];
	        for (int i = 0; i < dataBy.length; i++) {
				resBt[i+2]=dataBy[i];
			}
	        return resBt;
	}
	
	/**
	 * 从读卡器读出的数据不进行过滤存放到数据库中 
	 * @param labels 例：卡号，读卡器号，时间，结果
	 * @param equipIp 定位器ip地址
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	private synchronized  void saveDataRedis(List<String[]> labels, String equipIp) throws NumberFormatException, Exception {
		String timeSave = DataParmUtil.sdf.format(System.currentTimeMillis());
		try {
			
		
		String sql = "insert INTO electrombile_station (ele_gua_card_num, station_phy_num, hard_read_time, update_time) VALUES(?,?,?,?)";
		//System.out.println("往数据库中存放的数据："+res);
	    
		for (int i = 0; i < labels.size(); i++) {
			String[] labelDataArr = labels.get(i);
			System.out.print(labelDataArr[0]+"-");
		}
		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}



	@Override
	public void inputClosed(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
}

