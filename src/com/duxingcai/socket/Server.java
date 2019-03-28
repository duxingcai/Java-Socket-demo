/**
 *  Java Socket 通信的例子。具体通讯过程如下： 
先启动Server端，进入一个死循环以便一直监听某端口是否有连接请求。
然后运行Client端，客户端发出连接请求，服务端监听到这次请求后向客户端发回接收消息，连接建立，
启动一个线程去处理这次请求，然后继续死循环监听其他请求。客户端输入字符串后按回车键，向服务器发送数据。
器读取数据后回复客户端数据。此次请求处理完毕后，启动的线程消亡。如果客户端接收到 “OK”之外的返回数据，
会再次发送连接请求并发送数据，服务器会为这次连接再次启动一个线程进行响应
。直到当客户端接收到的返回数据为”OK”时，客户端退出。
 */
package com.duxingcai.socket;

import java.io.BufferedReader;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.InputStreamReader;  
import java.net.ServerSocket;
import java.net.Socket;  

public class Server {
    public static final int PORT = 12345;//监听的端口号

    public static void main(String[] args) {
        System.out.println("服务器启动...\n");
        Server server = new Server();
        server.init();
    }    

    public void init() {
        try {    
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                // 一旦有堵塞, 则表示服务器与客户端获得了连接
                Socket client = serverSocket.accept();
                // 开启一个新的线程处理这次连接
                new HandlerThread(client);
            }
        } catch (Exception e) {
            System.out.println("服务器异常: " + e.getMessage());
        }
    }

    //  开启一个新的线程处理连接
    private class HandlerThread implements Runnable {
        private Socket socket;    
        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
        }

        public void run() {
            try {
                // 读取客户端数据    
                DataInputStream input = new 
                DataInputStream(socket.getInputStream());  
                
                //这里要注意和客户端输出流的写方法对应,否则会抛 EOFException  
                String clientInputStr = input.readUTF();
                
                // 处理客户端数据
                System.out.println("客户端发过来的内容:" + clientInputStr);    
               
                // 向客户端回复信息
                DataOutputStream out = new  DataOutputStream(socket.getOutputStream());
                System.out.print("请输入:\t");
                
                // 发送键盘输入的一行
                String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
                out.writeUTF(s);
 
                out.close();    
                input.close();    
            } catch (Exception e) {    
                System.out.println("服务器 run 异常: " + e.getMessage());    
            } finally {    
                if (socket != null) {    
                    try {    
                        socket.close();    
                    } catch (Exception e) {
                        socket = null;    
                        System.out.println("服务端 finally 异常:" + e.getMessage());    
                    }    
                }    
            }   
        }    
    }    
}   
