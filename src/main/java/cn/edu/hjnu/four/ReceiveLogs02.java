package cn.edu.hjnu.four;

import cn.edu.hjnu.two.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class ReceiveLogs02 {

    public static final String EXCHANGES_NAME = "logs";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGES_NAME,"fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName,EXCHANGES_NAME,"");
        System.out.println("等待接收消息，把接受到的消息写到文件");
        DeliverCallback deliverCallback = (consumerTag,delivery) -> {
            String message = new String(delivery.getBody(),"UTF-8");
            File file = new File("D:\\rabbitmq_info.txt");
            FileUtils.writeStringToFile(file,message,"UTF-8");
            System.out.println("数据写入文件成功");

        };
        channel.basicConsume(queueName,true,deliverCallback,consumerTag -> {});

    }
}
