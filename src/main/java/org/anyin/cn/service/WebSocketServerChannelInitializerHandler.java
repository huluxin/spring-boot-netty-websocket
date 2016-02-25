package org.anyin.cn.service;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by Anyin on 2016/2/10.
 */
public class WebSocketServerChannelInitializerHandler extends ChannelInitializer<SocketChannel> {

    private WebSocketServer webSocketServer;

    public WebSocketServerChannelInitializerHandler(WebSocketServer webSocketServer){
        super();
        this.webSocketServer = webSocketServer;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-codec",new HttpServerCodec())
                .addLast("aggregator",new HttpObjectAggregator(65536))
                .addLast("http-chunked",new ChunkedWriteHandler())
                .addLast("handler",new WebSocketServerHandler(webSocketServer));
    }
}
