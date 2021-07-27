package edu.cqupt.rabbitmq.start;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * @author LWenH
 * @create 2021/7/26 - 17:53
 * <p>
 * 生产者
 */
public class Consumer {

    // 指定消息队列的名称
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.80.128");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123");

        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();
        System.out.println("等待接收消息");


        // 推送的消息如何进行消费的接口回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody());
            System.out.println("收到消息：" + msg);
        };
        // 消费被中断时的回调方法
        CancelCallback cancelCallback = (consumerTag) ->
                System.out.println(consumerTag + "消息消费被中断");

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
