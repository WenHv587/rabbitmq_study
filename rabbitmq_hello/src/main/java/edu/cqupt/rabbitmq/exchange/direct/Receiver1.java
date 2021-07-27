package edu.cqupt.rabbitmq.exchange.direct;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import edu.cqupt.rabbitmq.workQueue.RabbbitmqUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author LWenH
 * @create 2021/7/27 - 19:37
 * <p>
 * 测试 direct 直接交换机 叫消息发送给指定队列
 */
public class Receiver1 {
    private static final String EXCHANGE_NAME = "direct";
    private static final String QUEUE_NAME = "console";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbbitmqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 绑定direct交换机和console队列 并且交换机向队列转发routingKey为info和warning的消息
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME,"info");
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME,"warning");
        DeliverCallback deliverCallback = (tag, msg) -> {
            String message = new String(msg.getBody(), StandardCharsets.UTF_8);
            System.out.println("接收绑定键 :"+ msg.getEnvelope().getRoutingKey()+", 消息:"+message);
        };
        System.out.println("Receiver1等待接收消息");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, a -> {
        });
    }
}
