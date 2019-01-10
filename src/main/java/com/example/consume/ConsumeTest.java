package com.example.consume;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author ywyw2424@foxmail.com
 * @date 2019/1/9 15:59
 * @desc 服务消费者
 */
public class ConsumeTest implements Runnable{

    @Override
    public void run() {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
//            socketChannel.configureBlocking(false);//非阻塞模式
            socketChannel.connect(new InetSocketAddress("127.0.0.1",9090));

//            while(! socketChannel.finishConnect() ) {
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

                writeBuffer.clear();
                writeBuffer.put("asdjhasdg".getBytes());
                writeBuffer.flip();
                while (writeBuffer.hasRemaining()) {
                    socketChannel.write(writeBuffer);
                }

                readBuffer.clear();
                int read = socketChannel.read(readBuffer);
                System.out.println("read: " + read);
                System.out.println("readBuffer: " + new String(readBuffer.array()));
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socketChannel!=null) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
