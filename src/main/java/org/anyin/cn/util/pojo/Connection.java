package org.anyin.cn.util.pojo;

import io.netty.channel.Channel;

/**
 * Created by Anyin on 2016/2/9.
 */
public class Connection {
    private Integer connId;
    private String name;
    private Channel channel;

    public Integer getConnId() {
        return connId;
    }

    public void setConnId(Integer connId) {
        this.connId = connId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "connId=" + connId +
                ", name='" + name + '\'' +
                '}';
    }
}
