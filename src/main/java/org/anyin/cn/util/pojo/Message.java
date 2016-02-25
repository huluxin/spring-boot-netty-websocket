package org.anyin.cn.util.pojo;

/**
 * Created by Anyin on 2016/1/24.
 */
public class Message {
    private Integer from;     //发送者
    private String fromName;   //发送者昵称
    private Integer to;       //接收者
    private String toName;  //接收者昵称
    private String message; //消息内容
    private byte type;      //消息类型，看messageType

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", fromName='" + fromName + '\'' +
                ", to=" + to +
                ", toName='" + toName + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                '}';
    }
}
