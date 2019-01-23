package com.ems.iot.manage.util.hardutil;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;


public class ClientHandler implements IoHandler {

    @Override
    public void exceptionCaught(IoSession session, Throwable throwable) throws Exception
    {
        System.out.println("客户端exceptionCaught被调用！");
        throwable.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception
    {
        System.out.println("客户端messageReceived被调用！");
        System.out.println("client端接收信息：" + message.toString());
       //mina接收16进制的数据
 	    IoBuffer bbuf = (IoBuffer) message;  

        byte[] byten = new byte[bbuf.limit()];  
        bbuf.get(byten, bbuf.position(), bbuf.limit());
        //将字节数组转化为16进制字符串
        String sensorData=ByteAndStr16.Bytes2HexString(byten);
        System.out.println("client端接收信息：" + sensorData);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception
    {
        System.out.println("客户端messageSent被调用！");
      //mina接收16进制的数据
 	    IoBuffer bbuf = (IoBuffer) message;  

        byte[] byten = new byte[bbuf.limit()];  
        bbuf.get(byten, bbuf.position(), bbuf.limit());
        //将字节数组转化为16进制字符串
        String sensorData=ByteAndStr16.Bytes2HexString(byten);
        System.out.println("client端发送信息：" + sensorData);
    }

    public void inputClosed(IoSession session) throws Exception
    {
        System.out.println("客户端inputClosed被调用！");
        System.out.println("client端：" + session.getId() + " 关闭输入");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception
    {
        System.out.println("客户端sessionClosed被调用！");
        System.out.println("client端与：" + session.getRemoteAddress().toString() + " 关闭连接");
        System.exit(0);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception
    {
        System.out.println("客户端sessionCreated被调用！");
        System.out.println("client端与：" + session.getRemoteAddress().toString() + " 建立连接");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception
    {
        System.out.println("客户端sessionIdle被调用！");
        System.out.println("client端闲置连接：会话 " + session.getId() + " 被触发 " + session.getIdleCount(status) + " 次");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception
    {
        System.out.println("客户端sessionOpened被调用！");
        System.out.println("client端打开连接");
    }

}


