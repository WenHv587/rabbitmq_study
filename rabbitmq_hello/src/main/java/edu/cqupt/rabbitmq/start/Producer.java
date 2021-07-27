package edu.cqupt.rabbitmq.start;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author LWenH
 * @create 2021/7/26 - 17:40
 * <p>
 * 生产者代码
 */
public class Producer {
    // 指定消息队列的名称
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        // 创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.80.128");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123");

        // 创建连接
        try (Connection connection = connectionFactory.newConnection();
             // channel实现了AutoClose接口 自动关闭
             Channel channel = connection.createChannel()) {
            // 声明消息队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            // 发送消息
            channel.basicPublish("",QUEUE_NAME,null,"hello, rabbitmq!".getBytes());
            System.out.println("消息发送完毕！");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
