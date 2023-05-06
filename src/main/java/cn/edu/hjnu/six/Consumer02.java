package cn.edu.hjnu.six;

import cn.edu.hjnu.two.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

public class Consumer02 {

    public static final String DEAD_EXCHANGE = "dead_exchange";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();

        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        String deadQueue=  "dead-queue";
        channel.queueDeclare(deadQueue,false,false,false,null);

        //死信队列绑定死信交换机和routingKye
        channel.queueBind(deadQueue,DEAD_EXCHANGE,"lisi");

        System.out.println("等待接收消息");
        DeliverCallback deliverCallback = (consumerTage, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("Consumer02接收到消息:" + message);
        };
        channel.basicConsume(deadQueue,true,deliverCallback,consumerTag -> {});


    }
}
