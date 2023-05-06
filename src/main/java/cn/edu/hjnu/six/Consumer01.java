package cn.edu.hjnu.six;

import cn.edu.hjnu.two.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Consumer01 {
    //普通交换机
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机
    public static final String DEAD_EXCHANGE = "dead_exchange";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        String deadQueue = "dead-queue";
        channel.queueDeclare(deadQueue,false,false,false,null);

        //死信队列绑定死信交换机和routingKye
        channel.queueBind(deadQueue,DEAD_EXCHANGE,"lisi");

        //正常队列绑定死信队列
        Map<String, Object> params = new HashMap<>();
        //正常队列设置死信交换机 参数key是固定值
        params.put("x-dead-letter-exchange",DEAD_EXCHANGE);
        //正常队列设置死信routing-key
        params.put("x-dead-letter-routing-key","lisi");
        //设置正常队列长度的限制
        params.put("x-max-length",6);

        String normalQueue = "normal-queue";
        channel.queueDeclare(normalQueue,false,false,false,null);
        channel.queueBind(normalQueue,NORMAL_EXCHANGE,"zhangsan");
        System.out.println("等待接收消息");
        DeliverCallback deliverCallback = (consumerTage,delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            if (message.equals("info5")){
                System.out.println("Consumer01接收到消息" + message + "并拒绝签收该消息");
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(),false);
            }else {
                System.out.println("Consumer01接收到消息:" + message);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            }
        };
        boolean autoAck = false;
        channel.basicConsume(normalQueue,autoAck,deliverCallback,consumerTag -> {});

    }
}
