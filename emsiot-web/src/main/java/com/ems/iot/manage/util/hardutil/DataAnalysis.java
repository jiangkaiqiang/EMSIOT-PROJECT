package com.ems.iot.manage.util.hardutil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataAnalysis {
	
	public static void main(String[] args) {
		Map<String, String> map = dataResAsc("0079080008ffffffffffffffffffd49f3b5a0000000000d49f3b5a0000000000201610121816061");
		System.out.println(map);
		
	}
	/**
	 * 秀派数据解析
	 * @param data
	 * @return 将解析后的数据存放到map中  键：标签号； 值：标签号-触发器号-时间
	 */
	public static Map<String,String> dataRes(String data){
		Map<String,String> res = new HashMap<String, String>();
		String[] str = data.split(" ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		String recordDate=sdf.format(new Date());
		//获取本次的记录数
		int size = Integer.parseInt(str[18], 16);
		//从第20开始
		int j=19;
		for (int i = 0; i < size; i++) {
			//标签号
			StringBuffer labelNum = new StringBuffer();
			labelNum.append(str[j+1]).append(str[j+2]).append(str[j+3]).append(str[j+4]);
			int labelNumInt = Integer.parseInt(labelNum.toString(), 16);
		    
			//触发器号
		    StringBuffer triggerNum = new StringBuffer();
		    triggerNum.append(str[j+6]).append(str[j+7]);
		    int triggerNumInt = Integer.parseInt(triggerNum.toString(), 16);
		    String labtri;
		    if(triggerNumInt==0){
		    	labtri = labelNumInt+"-"+"00000" + "-" +recordDate;
		    }else{
		    	 //打印 标签号-触发器号
			    labtri = labelNumInt+"-"+triggerNumInt + "-" +recordDate;
		    }
		   
			
		    //System.out.println(labtri);
			res.put(Integer.toString(labelNumInt), labtri);
			j+=20;
		}

		System.out.println("检测的标签数："+res.size()+"检测到的标签："+res);
		return res;
	}
	
	/**
	 * 电科所数据解析
	 * 示例：0079080008 ffffffffffffffffff d49f3b5a 0000000000 d49f3b5a 0000000000 20161012181606 1(接收到的数据没有空格，加空格是为了方便查看)
	 * @param data   ascii字符串
	 * @return 卡号-触发器号-判决结果
	 */
	public static Map<String, String> dataResAsc(String data){
		
		Map<String, String> map = new HashMap<String, String>();
		
		//标签数据
		String record = data.substring(10);
	
		String device_id = record.substring(0, 18);
	

		String start_time = record.substring(record.length()-15, record.length()-1);

		String Opt_type = record.substring(record.length()-1);
		
		int size = (data.length()-43)/18;
		int start=18;
		for (int i = 0; i < size; i++) {
			
			String card_id = Long.parseLong(record.substring(start, start+8), 16)+"";
			map.put(card_id, card_id+"-"+"00000"+"-"+data.substring(data.length()-1));
			start+=18;
		}
		
		start=18;
		
		System.out.println("检测的标签数：" + map.size()+"检测到的标签：" + map);
		
		return map;
	} 
	
	
	/**
	 * 秀派数据解析
	 * @param data
	 * @return 将解析后的数据存放到map中  键：标签号； 值：标签号-触发器号-旧触发器号
	 */
	public static Map<String,String> passWaydataRes(String data){
		Map<String,String> res = new HashMap<String, String>();
		String[] str = data.split(" ");
		//获取本次的记录数
		int size = Integer.parseInt(str[18], 16);
		//从第20开始
		int j=19;
		for (int i = 0; i < size; i++) {
			//标签号
			StringBuffer labelNum = new StringBuffer();
			labelNum.append(str[j+1]).append(str[j+2]).append(str[j+3]).append(str[j+4]);
			int labelNumInt = Integer.parseInt(labelNum.toString(), 16);
		    
			//触发器号
		    StringBuffer triggerNum = new StringBuffer();
		    triggerNum.append(str[j+6]).append(str[j+7]);
		    
		  
		    int triggerNumOld = Integer.parseInt(str[j+8]+str[j+9], 16);
		  
		    
		    
		    int triggerNumInt = Integer.parseInt(triggerNum.toString(), 16);
		    String labtri;
		    String triggerNumOldStr = null;
		    if(triggerNumOld==0){
		    	triggerNumOldStr="00000";
		    }else{
		    	triggerNumOldStr=triggerNumOld+"";
		    }
		    if(triggerNumInt==0){
		    	labtri = labelNumInt+"-"+"00000"+"-"+triggerNumOldStr;
		    }else{
		    	 //打印 标签号-触发器号
			    labtri = labelNumInt+"-"+triggerNumInt+"-"+triggerNumOldStr;
		    }
		   
		   
		    
			
		    //System.out.println(labtri);
			res.put(Integer.toString(labelNumInt), labtri);
			j+=20;
		}

//		System.out.println("检测的标签数："+res.size()+"检测到的标签："+res);
		return res;
	}

	/**
	 * 秀派数据解析
	 * @param data
	 * @return 将解析后的数据存放到map中  键：标签号； 值：毫秒数
	 */
	public static Map<String,String> getNewTrigger(String data){
		Map<String,String> res = new HashMap<String, String>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String[] str = data.split(" ");
		//获取本次的记录数
		int size = Integer.parseInt(str[20], 16);
		//从第20开始
		int j=21;
		for (int i = 0; i < size; i++) {
			//标签号
			StringBuffer labelNum = new StringBuffer();
			labelNum.append(str[j+1]).append(str[j+2]).append(str[j+3]).append(str[j+4]);
			int labelNumInt = Integer.parseInt(labelNum.toString(), 16);
		    
			String receiveDateStr=str[j+16]+str[j+17]+str[j+18]+str[j+19];
		 	long receiveDateL=Integer.parseInt(receiveDateStr,16);
		 	/*System.err.println(L);
		 	System.err.println(L*1000);*/
		 	
		 	String receiveDate=sdf.format(new Date());
		    //标签号-时间毫秒数
			res.put(Integer.toString(labelNumInt), (receiveDateL*1000)+"");
			j+=20;
		}

//		System.out.println("检测的标签数："+res.size()+"检测到的标签："+res);
		return res;
	}
	
	/**
	 * 秀派读卡器消息数据解析(根据读卡器判断进出)
	 * @param data
	 * @return 将解析后的数据存放到map中  键：标签号； 值：读卡器号-UNIX时间戳
	 */
	public static Map<Integer,String> dataResPd(String[] str,int equNum){
		
		Map<Integer,String> res = new HashMap<Integer, String>();

		//获取本次的记录数
		int size = Integer.parseInt(str[18], 16);
		//System.out.println("本次记录数："+size);
		//从第20开始
		int j=19;
		for (int i = 0; i < size; i++) {
			//标签号
			StringBuffer labelNum = new StringBuffer();
			labelNum.append(str[j+1]).append(str[j+2]).append(str[j+3]).append(str[j+4]);
			int labelNumInt = Integer.parseInt(labelNum.toString(), 16);
		    
			StringBuffer senseDateStr = new StringBuffer();
			senseDateStr.append(str[j+16]).append(str[j+17]).append(str[j+18]).append(str[j+19]);
			//UNIX时间戳（秒数）
			long senseDate = Long.parseLong(senseDateStr.toString(), 16);
			j+=20;
			res.put(labelNumInt, equNum+"-"+senseDate);
			
		}
	
		System.out.println("检测的标签数："+res.size()+"检测到的标签："+res);
		return res;
	}
	
	
	
	
	
	
	
	
	
	
}
