package edu.cqupt.rabbitmq.ack;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import edu.cqupt.rabbitmq.workQueue.RabbbitmqUtils;

import java.io.IOException;
import java.sql.SQLOutput;

/**
 * @author LWenH
 * @create 2021/7/26 - 21:24
 * <p>
 * 模拟处理消息较快的工作线程
 */
public class Worker1 {

    private final static String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws IOException {
        Channel channel = RabbbitmqUtils.getChannel();
        // 设置不公平分发，让处理快的工作线程处理更多的消息
        channel.basicQos(1);

        // 设置预取值
//        channel.basicQos(2);

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("worker1收到消息~");
            String msg = new String(message.getBody());
            System.out.println("worker1开始处理消息~");
            try {
                // 模拟处理消息的时间
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("处理完成，得到消息：" + msg);
            // 处理完成，发送确认 false代表仅对当前消息返回确认，不进行累计确认
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };
        CancelCallback cancelCallback = (consumerTag) -> System.out.println(consumerTag + "消息消费被中断");
        System.out.println("消息处理较快的worker1等待接收消息...");
        /*
            第二个参数为false，手动应答
            自动应答在刚收到消息时就会进行应答，mq就会删除这条消息。
            如果在处理消息的过程中，出现错误或者服务器宕机，这条消息就丢失了。
            所以要改成手动应答的模式，在处理逻辑完成以后再手动进行应答。
         */
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback);
    }
}
