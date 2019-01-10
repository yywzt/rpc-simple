package com.example.selectors.produce;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author ywyw2424@foxmail.com
 * @date 2019/1/10 15:58
 * @desc 服务端
 */
public class ProductSelectorServer {

    static int BLOCK=2014;
    protected Selector selector;
    protected ByteBuffer readBuffer = ByteBuffer.allocate(BLOCK);
    protected ByteBuffer writeBuffer = ByteBuffer.allocate(BLOCK);

    public void start() {
        try {
            ServerSocketChannel channel = ServerSocketChannel.open();

            selector = Selector.open();
            channel.bind(new InetSocketAddress("127.0.0.1",9090));
            channel.configureBlocking(false);//非阻塞模式
            channel.register(selector, SelectionKey.OP_ACCEPT);

            while (true){
                int readyChannels = selector.select();
                if(readyChannels == 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    try {
                        process(selectionKey);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void process(SelectionKey selectionKey) throws IOException{

        if(selectionKey.isAcceptable()){//接收请求

            ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel socketChannel = channel.accept();
            socketChannel.configureBlocking(false);//非阻塞模式
            socketChannel.register(selector,SelectionKey.OP_READ);
        }else if(selectionKey.isConnectable()){
            // 连接就绪 a connection was established with a remote server.
        }else if(selectionKey.isReadable()){//读信息
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            int read = socketChannel.read(readBuffer);
            if(read>0){
                readBuffer.flip();
                String msg = new String(readBuffer.array());
                System.out.println("readBuffer: " + msg);
                SelectionKey register = selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
//                SelectionKey register = socketChannel.register(selector, SelectionKey.OP_WRITE);
                register.attach(msg);
            }else{
                socketChannel.close();
            }
            readBuffer.clear();
        }else if(selectionKey.isWritable()){//写事件
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            String msg = (String) selectionKey.attachment();

            writeBuffer.clear();
            writeBuffer.put(msg.getBytes());
            writeBuffer.flip();
            while(writeBuffer.hasRemaining()) {
                socketChannel.write(writeBuffer);
            }
            System.out.println("writeBuffer: " + new String(writeBuffer.array()));
            SelectionKey register = selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args){
        ProductSelectorServer server = new ProductSelectorServer();
        server.start();
    }

}
