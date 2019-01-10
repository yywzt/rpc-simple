package com.example.selectors.consume;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author ywyw2424@foxmail.com
 * @date 2019/1/10 15:59
 * @descLock 消费者
 */
public class ConsumeSelectorTest {

    static int BLOCK=2014;
    protected ByteBuffer readBuffer = ByteBuffer.allocate(BLOCK);
    protected ByteBuffer writeBuffer = ByteBuffer.allocate(BLOCK);

    protected Selector selector;

    public void start() {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);//非阻塞模式

            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress("127.0.0.1",9090));

            while(true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
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
        if(selectionKey.isConnectable()){
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            if(channel.isConnectionPending()){
                channel.finishConnect();
            }
            writeBuffer.clear();
            writeBuffer.put("heihei".getBytes());
            writeBuffer.flip();
            while(writeBuffer.hasRemaining()) {
                channel.write(writeBuffer);
            }
            System.out.println("writeBuffer: " + new String(writeBuffer.array()));
            channel.register(selector,SelectionKey.OP_READ);
        }else if(selectionKey.isReadable()){
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            int read = channel.read(readBuffer);
            if(read > 0){
                readBuffer.flip();
                while(readBuffer.hasRemaining()){
                    System.out.println("readBuffer: " + new String(readBuffer.array()));
                }
                readBuffer.clear();
            }else {
                channel.close();
            }
        }
    }

    public static void main(String[] args){
        ConsumeSelectorTest selectorTest = new ConsumeSelectorTest();
        selectorTest.start();
    }
}
