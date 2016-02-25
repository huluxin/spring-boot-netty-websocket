package org.anyin.cn.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import org.anyin.cn.util.pojo.Connection;

import java.util.List;
import java.util.Map;

/**
 * Created by Anyin on 2016/1/23.
 * 链路管理器
 */
public class ConnectionManager {

    //保存每个链路
    private Map<Integer,Connection> connId2Connection = Maps.newConcurrentMap();

    /**
     * 添加链路
     * @param connId
     * @param connection
     */
    public void add(Integer connId,Connection connection){
        connId2Connection.put(connId,connection);
    }

    /**
     * 移除链路
     * @param connId
     */
    public void remove(Integer connId){
        connId2Connection.remove(connId);
    }

    /**
     * 获得链路
     * @param connId
     * @return
     */
    public Connection get(Integer connId){
        return connId2Connection.get(connId);
    }

    /**
     * 获得所有链路
     * @return
     */
    public List<Connection> getConnections(){
        return Lists.newArrayList(connId2Connection.values());
    }
}
