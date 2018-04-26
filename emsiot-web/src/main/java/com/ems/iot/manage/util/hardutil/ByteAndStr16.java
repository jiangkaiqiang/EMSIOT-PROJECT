package com.ems.iot.manage.util.hardutil;

/** 
 * @说明 十六进制与十进制之间的转
 * @author 
 * @version 
 * @since 
 */  
public class ByteAndStr16 {  
    private final static byte[] hex = "0123456789ABCDEF".getBytes();  
    private static int parse(char c) {  
        if (c >= 'a')  
            return (c - 'a' + 10) & 0x0f;  
        if (c >= 'A')  
            return (c - 'A' + 10) & 0x0f;  
        return (c - '0') & 0x0f;  
    }  
    // 从字节数组到十六进制字符串转 
    public static String Bytes2HexString(byte[] b) {  
        byte[] buff = new byte[3 * b.length];  
        for (int i = 0; i < b.length; i++) {  
            buff[3 * i] = hex[(b[i] >> 4) & 0x0f];  
            buff[3 * i + 1] = hex[b[i] & 0x0f];  
            buff[3 * i + 2] = 45;  
        }  
        String re = new String(buff);  
        return re.replace("-", " ");  
    }  
    // 从十六进制字符串到字节数组转
    public static byte[] HexString2Bytes(String hexstr) {  
        hexstr = hexstr.replace(" ", "");  
        byte[] b = new byte[hexstr.length() / 2];  
        int j = 0;  
        for (int i = 0; i < b.length; i++) {  
            char c0 = hexstr.charAt(j++);  
            char c1 = hexstr.charAt(j++);  
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));  
        }  
        return b;  
    } 
    
    
    
    //16进制字符串转ascii字符串
    public static String strascii(String hexStr){
    	return  String.valueOf((char)Integer.parseInt(hexStr, 16));
    }
    
    
}  