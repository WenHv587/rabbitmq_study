package edu.cqupt.rabbitmq.priorityQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import edu.cqupt.rabbitmq.workQueue.RabbbitmqUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LWenH
 * @create 2021/7/29 - 17:57
 * <p>
 * 使用优先级队列
 */
public class Consumer {
    private static final String QUEUE_NAME = "priority_queue";

    public static void main(String[] args) throws IOException {
        Channel channel = RabbbitmqUtils.getChannel();
        Map<String, Object> params = new HashMap<>();
        // 指定最大优先级（最大支持255）
        params.put("x-max-priority", 10);
        channel.queueDeclare(QUEUE_NAME, true, false, false, params);
        DeliverCallback deliverCallback = (tag, msg) -> {
            String message = new String(msg.getBody(), StandardCharsets.UTF_8);
            System.out.println("收到消息：" + message);
        };
        System.out.println("等待接收消息");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback,
                (consumerTag) -> System.out.println("消费者无法消费消息时调用，如队列被删除"));
    }
}
