package com.ems.iot.manage.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.comet4j.core.CometConnection;
import org.comet4j.core.CometContext;
import org.comet4j.core.CometEngine;
import org.comet4j.core.event.ConnectEvent;
import org.comet4j.core.listener.ConnectListener;

import com.ems.iot.manage.entity.SysUser;
import com.ems.iot.manage.entity.MessageEntity;


public class CometUtil extends ConnectListener implements ServletContextListener {
     /**
      * 初始化上下文
      */
     public void contextInitialized(ServletContextEvent arg0) {
             // CometContext ： Comet4J上下文，负责初始化配置、引擎对象、连接器对象、消息缓存等。
             CometContext cc = CometContext.getInstance();
             // 注册频道，即标识哪些字段可用当成频道，用来作为向前台传送数据的“通道”
             cc.registChannel(Constant.CHANNEL_MSGCOUNT);
             cc.registChannel(Constant.CHANNEL_MSG_DATA);
             cc.registChannel(Constant.CHANNEL_LIMIT_DATA);
             //添加监听器  
             CometEngine engine = CometContext.getInstance().getEngine();  
             engine.addConnectListener(this); 
     }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub
    }
    @Override
    public boolean handleEvent(ConnectEvent connEvent){
        // TODO Auto-generated method stub
        final CometConnection conn = connEvent.getConn();
        SysUser user = (SysUser)conn.getRequest().getSession().getAttribute("user");
        if (user!=null) {
        	  CacheManager.putContent(user.getUser_id()+"", connEvent);
		}
        return true;
    }
    private void doCache(final CometConnection conn,String userId) {  
        if (userId != null) {  
            CacheManager.putContent(conn.getId(), String.valueOf(userId), Constant.EXPIRE_AFTER_ONE_HOUR);  
        }  
    }
    /**
     * 推送给所有的客户端
     * @param comet
     */
    public void pushToAll(MessageEntity comet){
        try {
            CometEngine engine = CometContext.getInstance().getEngine();
               //推送到所有客户端  
               engine.sendToAll(Constant.CHANNEL_MSGCOUNT,1);
               engine.sendToAll(Constant.CHANNEL_MSG_DATA,comet.getContent());
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
           
    }
    
    /**
     * 推送给所有的客户端(限制区域)
     * @param comet
     */
    public void pushToLimit(MessageEntity comet){
    	try {
    		CometEngine engine = CometContext.getInstance().getEngine();
    		//推送到所有客户端  
    		engine.sendToAll(Constant.CHANNEL_MSGCOUNT,1);
    		engine.sendToAll(Constant.CHANNEL_LIMIT_DATA,comet.getContent());
    	} catch (Exception e) {
    		// TODO: handle exception
    		System.out.println(e.getMessage());
    	}
    	
    }
    
    /**
     * 推送给指定客户端
     * @param comet
     */
    public void pushTo(MessageEntity comet){
        try {
            ConnectEvent connEvent = (ConnectEvent) CacheManager.getContent(comet.getReceiverid().intValue()+"").getValue();
            final CometConnection conn = connEvent.getConn();
               //建立连接和用户的关系  
               doCache(conn,comet.getReceiverid().intValue()+"");
               final String connId = conn.getId(); 
               CometEngine engine = CometContext.getInstance().getEngine();
               if (CacheManager.getContent(connId).isExpired()) {  
                   doCache(conn,comet.getReceiverid().intValue()+"");  
               }
               //推送到指定的客户端  
              engine.sendTo(Constant.CHANNEL_MSGCOUNT, engine.getConnection(connId), 1);
              engine.sendTo(Constant.CHANNEL_MSG_DATA, engine.getConnection(connId), comet.getReceiverid().intValue());
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
