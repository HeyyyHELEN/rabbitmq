package cn.edu.hjnu.three;

import cn.edu.hjnu.two.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Work03 {
    private static final String ACK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("等待接受消息处理时间较短");
        DeliverCallback deliverCallback = (consumerTag,delivery) -> {
            String message = new String(delivery.getBody());
            SleepUtils.sleep(1);
            System.out.println("接收到消息:"+message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
        };
        //设置不公平分发
        int prefetchCount = 2;
        channel.basicQos(prefetchCount);
        //采取手动应答
        boolean autoAck = false;
        channel.basicConsume(ACK_QUEUE_NAME,autoAck,deliverCallback,(consumerTag) -> {
            System.out.println(consumerTag+"消费者取消消费接口回调逻辑");
        });
    }
}
