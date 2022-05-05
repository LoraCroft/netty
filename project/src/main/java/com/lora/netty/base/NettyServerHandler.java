package com.lora.netty.base;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;

/**
 * 服务端处理器
 * server端收到客户端数据后会经过handler处理
 *
 * 自定义的 handler 需要继承netty规定好的某个handler （netty规范）
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取客户端发送的数据
     * @param ctx 上下文对象，含有通道channel，管道pipeline
     * @param msg 客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //super.channelRead(ctx, msg);
        System.out.println("server read thread :"+Thread.currentThread().getName());
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("client send msg {"+buf.toString(CharsetUtil.UTF_8)+"}");
    }

    /**
     * 数据读取完毕处理方法, {@link ChannelInboundHandlerAdapter#channelRead(ChannelHandlerContext, Object)}执行完毕后调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //super.channelReadComplete(ctx);
        ByteBuf buf = Unpooled.copiedBuffer("msg receive success".getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(buf);
    }

    /**
     * 处理异常，通常是
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
