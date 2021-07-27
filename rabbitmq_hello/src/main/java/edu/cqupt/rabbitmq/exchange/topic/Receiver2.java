package edu.cqupt.rabbitmq.exchange.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import edu.cqupt.rabbitmq.workQueue.RabbbitmqUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author LWenH
 * @create 2021/7/27 - 22:25
 */
public class Receiver2 {
    private static final String EXCHANGE_NAME = "topic_logs";
    private static final String QUEUE_NAME = "Q2";

    public static void main(String[] args) throws IOException {
        Channel channel = RabbbitmqUtils.getChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME, "*.*.rabbit");
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME, "lazy.#");

        DeliverCallback deliverCallback = (tag, msg) -> {
            String message = new String(msg.getBody(), StandardCharsets.UTF_8);
            System.out.println("Receiver2收到routingKey为：" + msg.getEnvelope().getRoutingKey() + "的消息：" + message);
        };
        System.out.println("Receiver2等待接收消息");
        channel.basicConsume(QUEUE_NAME, true,deliverCallback, a -> {});
    }
}
