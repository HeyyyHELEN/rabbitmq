package cn.edu.hjnu.three;

import cn.edu.hjnu.two.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConfirmSelect {

    public static void main(String[] args) throws Exception {
        //ConfirmSelect.publishMessageIndividually();
        //ConfirmSelect.publishMessageBatch();
        ConfirmSelect.publishMessageAsync();
    }


    public static void publishMessageIndividually() throws Exception{ //572ms
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,false,false,false,null);
        //开启发布确认
        channel.confirmSelect();
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            String message = i + "";
            channel.basicPublish("",queueName,null,message.getBytes());
            //服务器端返回false或超时未返回，生产者可以消息重发
            boolean falg = channel.waitForConfirms();
            if (falg){
                System.out.println("消息发送成功");
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时："+ (end - begin) + "ms");
    }



    public static void publishMessageBatch() throws Exception{ //85ms
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,false,false,false,null);
        //开启发布确认
        channel.confirmSelect();
        //批量确认消息大小
        int batchSize = 100;
        //批量确认消息个数
        int outstandingMessageCount = 0;
        long begin = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            String message = i + "";
            channel.basicPublish("",queueName,null,message.getBytes());
            outstandingMessageCount++;
            if (outstandingMessageCount == batchSize){
                channel.waitForConfirms();
                outstandingMessageCount = 0;
            }
        }
        //为了确保还有剩余没有确认消息，再次确认
        if (outstandingMessageCount > 0){
            channel.waitForConfirms();
        }

        long end = System.currentTimeMillis();
        System.out.println("耗时："+ (end - begin) + "ms");
    }


    public static void publishMessageAsync() throws Exception{ //45ms
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,false,false,false,null);
        //开启发布确认
        channel.confirmSelect();
        ConcurrentSkipListMap<Long,String> outstandingConfirms = new ConcurrentSkipListMap<>();
        ConfirmCallback ackCallback = (sequenceNumber,multiple) -> {
            if (multiple){
                //返回的是小于等于当前序列号的未确认消息 是一个map
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(sequenceNumber, true);
                //清除该部分为确认消息
                confirmed.clear();
            }else {
                //只清除当前序列号的消息
                outstandingConfirms.remove(sequenceNumber);
            }
        };

        ConfirmCallback nackCallback = (sequenceNumber,multiple) -> {
            String message = outstandingConfirms.get(sequenceNumber);
            System.out.println("发布的消息" + message + "未被确认，序列号" + sequenceNumber);
        };

        /**
         * 添加一个一步确认的监听器
         * 1.确认收到消息的回调
         * 2.未收到消息的回调
         */
        channel.addConfirmListener(ackCallback,null);
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            String message = i + "";
            outstandingConfirms.put(channel.getNextPublishSeqNo(),message);
            channel.basicPublish("",queueName,null,message.getBytes());
        }

        long end = System.currentTimeMillis();
        System.out.println("耗时："+ (end - begin) + "ms");

    }


}
