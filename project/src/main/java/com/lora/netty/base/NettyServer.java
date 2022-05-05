package com.lora.netty.base;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    public static void main(String[] args) {
        //创建两个线程 :   bossGroup和workerGroup ，含有的子线程NioEventLoop的个数默认为cpu核数的两倍
        // bossGroup只是处理连接请求 ,  和客户端处理业务的，交由workerGroup完成
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();


        try {
            //创建服务器启动对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            //配置参数
            bootstrap.group(bossGroup,workerGroup)  //设置两个线程组
                    .channel(NioServerSocketChannel.class)  //使用NioServerSocketChannel作为服务器的通信通道的实现
                    //初始化连接队列大小,服务器处理客户端请求是 顺序 处理的 ，所以同一时间只能处理一个客户端连接
                    //多个客户端连接的时候，服务端将不处理的请求放到队列中等待处理
                    .option(ChannelOption.SO_BACKLOG,1024)
                    //创建通信初始化通道，设置初始化参数
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //对workerGroup的socketChannel设置处理器
                            ch.pipeline().addLast(new NettyServerHandler());
                        }

                    });

            System.out.println("netty server ready finish");

            //绑定端口    并且生成一个ChannelFuture异步对象，通过isDone()等方法可以判断异步事件的执行情况
            ChannelFuture cf = bootstrap.bind(9000).sync();
            //cf.isDone();


            //等待服务端监听端口关闭，closeFuture时异步操作
            //通过sync方法同步等待通道关闭处理完毕，这里会阻塞等待通道完成，内部调用的是Object.wait()方法
            cf.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
