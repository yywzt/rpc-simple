package com.example.produce;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author ywyw2424@foxmail.com
 * @date 2019/1/9 15:58
 * @desc 服务端
 */
public class ProductTest implements Runnable{

    @Override
    public void run() {
        try {
            ServerSocketChannel channel = ServerSocketChannel.open();
            channel.bind(new InetSocketAddress("127.0.0.1",9090));
//            channel.configureBlocking(false);//切换成非阻塞模式

            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

            while (true){
                SocketChannel socketChannel = channel.accept();
                if(socketChannel != null){
                    readBuffer.clear();
                    int read = socketChannel.read(readBuffer);
                    System.out.println("readBuffer: " + new String(readBuffer.array()));
                    //相关操作
                    writeBuffer.clear();
                    writeBuffer.put("response".getBytes());
                    writeBuffer.flip();
                    int write = socketChannel.write(writeBuffer);
                    System.out.println("writeBuffer: " + new String(writeBuffer.array()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
