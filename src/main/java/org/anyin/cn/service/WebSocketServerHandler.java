package org.anyin.cn.service;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.anyin.cn.util.pojo.Message;
import org.anyin.cn.util.pojo.MessageType;
import org.anyin.cn.util.pojo.Connection;
import org.anyin.cn.util.serialize.Serializer;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by Anyin on 2016/1/11.
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = Logger.getLogger(WebSocketServerHandler.class);

    //webSocket握手处理接口
    private WebSocketServerHandshaker shaker;
    //链路管理器
    private ConnectionManager connectionManager;
    //链路ID生成器
    private static AtomicInteger idCreate = new AtomicInteger();
    //链路ID
    private Integer connId;
    //序列化工具
    private Serializer serializer;

    public WebSocketServerHandler(WebSocketServer webSocketServer){
        super();
        this.connId = idCreate.incrementAndGet();
        this.connectionManager = webSocketServer.connectionManager;
        this.serializer = webSocketServer.serializer;
    }

    /**
     * 获得链路ID
     * @return
     */
    public Integer getConnId(){
        return this.connId;
    }


    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        //传统http接入
        if(msg instanceof FullHttpRequest){
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }
        //webSocket接入
        else if(msg instanceof WebSocketFrame){
            handleWebSocketFrame(ctx,(WebSocketFrame)msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 处理传统http请求，第一次请求为http请求，作为webSocket的握手请求
     * @param ctx
     * @param req
     * @throws Exception
     */
    private void handleHttpRequest(ChannelHandlerContext ctx,FullHttpRequest req) throws Exception{
        boolean isSuccess = req.decoderResult().isSuccess();
        if(!isSuccess || (!"websocket".equals(req.headers().get("Upgrade")))){
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        //构造握手处理类工厂
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("ws://localhost:8099/websocket",null,false);
        //从工厂返回握手处理类
        shaker = factory.newHandshaker(req);
        if(shaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else{
            //动态添加webSocket的编解码器
            shaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 添加链路触发事件
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Connection connection = new Connection();
        connection.setConnId(connId);
        connection.setChannel(ctx.channel());
        this.connectionManager.add(connId, connection);

        Message message = new Message();
        message.setType(MessageType.REGISTER);
        message.setFrom(connId);
        message.setTo(connId);

        String json = serializer.Object2String(message);
        ctx.channel().writeAndFlush(new TextWebSocketFrame(json));
        logger.info("新建一个链路："+connId);
    }

    /**
     * 处理webSocket请求
     * @param ctx
     * @param frame
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame){
        //是否是关闭链路指令
        if(frame instanceof CloseWebSocketFrame){
            //从连接器移除链路
            connectionManager.remove(connId);
            //关闭链路
            shaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            logger.info("关闭一个链路：" + connId);

            List<Connection> connections = connectionManager.getConnections();
            for(int i = 0 , len = connections.size() ; i < len ; i++){
                Connection connection = connections.get(i);
                Message message = new Message();
                message.setType(MessageType.LOGOUT);
                message.setMessage(connId+"");
                String json = serializer.Object2String(message);
                connection.getChannel().writeAndFlush(new TextWebSocketFrame(json));
            }

            return;
        }
        //是否是心跳指令
        if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        //是否是文本消息
        if(!(frame instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException(String.format("%s frame types not supported",frame.getClass().getName()));
        }
        //转为消息对象
        String request = ((TextWebSocketFrame) frame).text();
        Message message = serializer.String2Object(request,Message.class);

        //注册
        if(message.getType() == MessageType.REGISTER){
            Connection connection = connectionManager.get(connId);
            connection.setName(message.getMessage());

            List<Connection> connections = connectionManager.getConnections();
            //告诉所有的在线用户有新用户进来
            for(int i = 0 , len = connections.size() ; i < len ; i ++){
                Connection conn = connections.get(i);
                Message msg = new Message();
                msg.setFrom(conn.getConnId());
                msg.setTo(connId);
                msg.setType(MessageType.REGISTER);
                msg.setMessage(message.getMessage());
                String json = serializer.Object2String(msg);
                conn.getChannel().writeAndFlush(new TextWebSocketFrame(json));
                //当前用户显示已经在线的用户列表
                if(!conn.getConnId().equals(connId)){
                    msg.setMessage(conn.getName());
                    msg.setFrom(connId);
                    msg.setTo(conn.getConnId());
                    json = serializer.Object2String(msg);
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(json));
                }
            }
        }else{
            Connection connection = connectionManager.get(message.getTo());
            Message msg = new Message();
            msg.setMessage(message.getMessage());
            String json = serializer.Object2String(msg);
            connection.getChannel().writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    /**
     * 发送http响应
     * @param ctx
     * @param request
     * @param response
     */
    private static void sendHttpResponse(ChannelHandlerContext ctx,FullHttpRequest request ,FullHttpResponse response){
        if(response.status().code() != 200){
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            HttpHeaderUtil.setContentLength(response, response.content().readableBytes());
        }

        ChannelFuture future = ctx.channel().writeAndFlush(response);
        if(HttpHeaderUtil.isKeepAlive(request) || response.status().code() != 200){
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        connectionManager.remove(connId);
        ctx.close();
    }
}
