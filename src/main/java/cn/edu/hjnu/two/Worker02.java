package cn.edu.hjnu.two;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Worker02 {

    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        DeliverCallback deliverCallback = (consumerTag,deliverty) -> {
            String receivemessage = new String(deliverty.getBody());
            System.out.println("接受到消息：" + receivemessage);
        };
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
        };
        System.out.println("C2消费者启动等待消费");
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);

    }

}
