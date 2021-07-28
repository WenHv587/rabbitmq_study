package edu.cqupt.rabbitmq.deadLetter;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import edu.cqupt.rabbitmq.workQueue.RabbbitmqUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author LWenH
 * @create 2021/7/28 - 17:16
 *
 * 消费死信队列消息的消费和
 */
public class Consumer2 {
    private static final String DEAD_QUEUE = "dead_queue";
    public static void main(String[] args) throws IOException {
        Channel channel = RabbbitmqUtils.getChannel();
        System.out.println("等待接收死信队列消息。。。");
        DeliverCallback deliverCallback = (tag, msg) -> {
            String message = new String(msg.getBody(), StandardCharsets.UTF_8);
            System.out.println("Consumer2收到消息：" + message);
        };
        // 如果不设置自动确认 消费者重启以后还会再次重发一遍未确认的消息
        channel.basicConsume(DEAD_QUEUE,true, deliverCallback, a -> {});
    }
}
