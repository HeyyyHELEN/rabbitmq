package cn.edu.hjnu.two;

import com.rabbitmq.client.Channel;

import java.util.Scanner;

public class Task01 {

    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        //从控制台接受信息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message = scanner.next();
            channel.basicPublish("",QUEUE_NAME,null, message.getBytes());
            System.out.println("消息发送完成"+message);
        }
    }

}
