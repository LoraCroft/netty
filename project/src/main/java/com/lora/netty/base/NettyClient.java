package com.lora.netty.base;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    public static void main(String[] args) {
        //创建两个线程 :   bossGroup和workerGroup ，含有的子线程NioEventLoop的个数默认为cpu核数的两倍
        // bossGroup只是处理连接请求 ,  和客户端处理业务的，交由workerGroup完成
        EventLoopGroup group = new NioEventLoopGroup();


        try {
            //创建服务器启动对象
            Bootstrap bootstrap = new Bootstrap();
            //配置参数
            bootstrap.group(group)  //设置线程
                    .channel(NioSocketChannel.class)  //使用NioSocketChannel作为服务器的通信通道的实现
                    //创建通信初始化通道，设置初始化参数
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //对workerGroup的socketChannel设置处理器
                            ch.pipeline().addLast(new NettyClientHandler());
                        }

                    });

            System.out.println("netty client ready finish");

            //绑定端口    并且生成一个ChannelFuture异步对象，通过isDone()等方法可以判断异步事件的执行情况
            ChannelFuture cf = bootstrap.connect("localhost",9000).sync();

            //等待服务端监听端口关闭，closeFuture时异步操作
            //通过sync方法同步等待通道关闭处理完毕，这里会阻塞等待通道完成，内部调用的是Object.wait()方法
            cf.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

}
