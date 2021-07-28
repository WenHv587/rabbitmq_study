package edu.cqupt.rabbitmq.deadLetter;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import edu.cqupt.rabbitmq.workQueue.RabbbitmqUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author LWenH
 * @create 2021/7/28 - 15:46
 */
public class Producer {
    private static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] args) {
        try (Channel channel = RabbbitmqUtils.getChannel()) {
            channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
            // 设置消息ttl 10s 可以在发送端设置消息的ttl。也可以在声明队列的时候以参数的形式指定队列中所有消息的存活时间。
//            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();
            for (int i = 0; i < 10; i++) {
                String message = i + "";
                channel.basicPublish(NORMAL_EXCHANGE, "normal", null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println("发送消息：" + message);
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
