package edu.cqupt.rabbitmq.workQueue;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;

/**
 * @author LWenH
 * @create 2021/7/26 - 18:56
 *
 * 工作进程
 */
public class Worker {
    private final static String QUEUE_NAME = "hello";
    public static void main(String[] args) throws IOException {
        Channel channel = RabbbitmqUtils.getChannel();
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody());
            System.out.println("收到消息：" + msg);
        };
        CancelCallback cancelCallback = (consumerTag) -> System.out.println(consumerTag + "消息消费被中断");

        System.out.println("worker等待接收消息");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
