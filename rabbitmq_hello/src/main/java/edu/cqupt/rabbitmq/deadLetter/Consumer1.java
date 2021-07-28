package edu.cqupt.rabbitmq.deadLetter;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import edu.cqupt.rabbitmq.workQueue.RabbbitmqUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LWenH
 * @create 2021/7/28 - 16:00
 * <p>
 * 这个消费者用于正常接收消息 会声明一个死信队列用于接收死信
 */
public class Consumer1 {
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    private static final String NORMAL_QUEUE = "normal_queue";
    private static final String DEAD_LETTER_EXCHANGE = "dead_letter_exchange";
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws IOException {
        Channel channel = RabbbitmqUtils.getChannel();
        // 声明普通队列 和 交换机
        Map<String, Object> params = new HashMap<>(4);
        // 为普通队列设置私信队列的相关参数 其中key是固定写法
        params.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        params.put("x-dead-letter-routing-key", "dead");
        params.put("x-max-length", 6);
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, params);
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE, "normal");
        // 声明死信队列 和 交换机
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        channel.exchangeDeclare(DEAD_LETTER_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.queueBind(DEAD_QUEUE, DEAD_LETTER_EXCHANGE, "dead");

        System.out.println("等待接收消息。。。");
        DeliverCallback deliverCallback = (tag, msg) -> {
            String message = new String(msg.getBody(), StandardCharsets.UTF_8);
            if ("4".equals(message)) {
                System.out.println("拒绝该消息：" + message);
                // 第二个参数代表是否再次入队
                channel.basicReject(msg.getEnvelope().getDeliveryTag(), false);
            } else {
                System.out.println("Consumer1收到消息：" + message);
                channel.basicAck(msg.getEnvelope().getDeliveryTag(), false);
            }
        };
        channel.basicConsume(NORMAL_QUEUE, false, deliverCallback, a -> {
        });
    }
}
