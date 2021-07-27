package edu.cqupt.rabbitmq.workQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author LWenH
 * @create 2021/7/26 - 18:49
 */
public class RabbbitmqUtils {

    private static Connection connection;
    //创建一个连接工厂
    static {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.80.128");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123");
        try {
            connection = connectionFactory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到连接信道
     *
     * @return channel
     */
    public static Channel getChannel() throws IOException {
        return connection.createChannel();
    }
}
