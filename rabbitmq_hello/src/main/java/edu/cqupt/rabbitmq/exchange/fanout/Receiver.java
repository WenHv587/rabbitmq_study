package edu.cqupt.rabbitmq.exchange.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import edu.cqupt.rabbitmq.workQueue.RabbbitmqUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author LWenH
 * @create 2021/7/27 - 18:47
 * <p>
 * 测试 fanout 扇出 交换机 发布/订阅模式
 */
public class Receiver {
    private static final String EXCHANGE_NAME = "log";

    public static void main(String[] args) throws IOException {
        Channel channel = RabbbitmqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        // 声明一个临时队列
        String queue = channel.queueDeclare().getQueue();
        // 将该临时队列绑定到指定的 exchange 其中routineKey(也称为binding key)为空字符串
        channel.queueBind(queue, EXCHANGE_NAME, "");
        System.out.println("等待接收消息。。。");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            System.out.println("收到消息：" + msg);
        };
        channel.basicConsume(queue,true, deliverCallback, a -> {});
    }
}
