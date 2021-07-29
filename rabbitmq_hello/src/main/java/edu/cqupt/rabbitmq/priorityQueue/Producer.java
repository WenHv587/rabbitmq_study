package edu.cqupt.rabbitmq.priorityQueue;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import edu.cqupt.rabbitmq.workQueue.RabbbitmqUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author LWenH
 * @create 2021/7/29 - 17:56
 */
public class Producer {
    private static final String QUEUE_NAME = "priority_queue";

    public static void main(String[] args) {
        try (Channel channel = RabbbitmqUtils.getChannel()) {
            // 设置优先级属性
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().priority(5).build();
            for (int i = 0; i < 10; i++) {
                String message = i + "";
                if (i == 4 || i == 9) {
                    channel.basicPublish("", QUEUE_NAME, properties, message.getBytes(StandardCharsets.UTF_8));
                } else {
                    channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
                }
            }
            System.out.println("消息发送完成");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
