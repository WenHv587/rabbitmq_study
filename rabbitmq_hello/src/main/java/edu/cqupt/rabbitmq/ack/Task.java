package edu.cqupt.rabbitmq.ack;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import edu.cqupt.rabbitmq.workQueue.RabbbitmqUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * @author LWenH
 * @create 2021/7/26 - 19:01
 * <p>
 * 任务进程
 */
public class Task {
    private final static String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        try (Channel channel = RabbbitmqUtils.getChannel()) {
            // durable = true 开启队列的持久化（注意是队列的持久化，而不是消息的持久化）
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            //从控制台当中接受信息
            System.out.println("请发送消息");
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String message = scanner.next();
                // 第三个参数：开启消息的持久化（持久化到磁盘中，但不能完全保证不丢失消息。可能消息还在缓存中没来得及刷盘）
                channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
                System.out.println("发送消息完成:" + message);
            }
        }
    }
}

