package cn.edu.hjnu.four;

import cn.edu.hjnu.two.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class ReceiveLogs01 {
    public static final String EXCHANGES_NAME = "logs";

    public static void main(String[] args)  throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        //声明路由
        channel.exchangeDeclare(EXCHANGES_NAME,"fanout");
        //创建临时队列
        String queueName = channel.queueDeclare().getQueue();
        //将队列绑定到exchange
        channel.queueBind(queueName,EXCHANGES_NAME,"");
        System.out.println("等待接收消息，");
        DeliverCallback deliverCallback = (consumerTag,delivery) -> {
            String message = new String(delivery.getBody(),"UTF-8");
            System.out.println("控制台接受到的消息" + message);
        };
        channel.basicConsume(queueName,true,deliverCallback,consumerTag -> {});

    }

}
