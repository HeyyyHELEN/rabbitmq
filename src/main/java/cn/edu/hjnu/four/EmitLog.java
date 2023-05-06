package cn.edu.hjnu.four;

import cn.edu.hjnu.two.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

public class EmitLog {

    public static final String EXCHANGES_NAME = "logs";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGES_NAME, BuiltinExchangeType.FANOUT);
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入信息");
        while (scanner.hasNext()){
            String message = scanner.next();
            channel.basicPublish(EXCHANGES_NAME,"",null,message.getBytes());
        }

    }

}
