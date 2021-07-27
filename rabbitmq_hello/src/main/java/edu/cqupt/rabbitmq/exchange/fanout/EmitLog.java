package edu.cqupt.rabbitmq.exchange.fanout;

import com.rabbitmq.client.Channel;
import edu.cqupt.rabbitmq.workQueue.RabbbitmqUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * @author LWenH
 * @create 2021/7/27 - 19:09
 */
public class EmitLog {
    private static final String EXCHANGE_NAME = "log";
    public static void main(String[] args) {
        try (Channel channel = RabbbitmqUtils.getChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入消息");
            while (scanner.hasNext()) {
                String msg = scanner.nextLine();
                channel.basicPublish(EXCHANGE_NAME, "",null,msg.getBytes(StandardCharsets.UTF_8));
                System.out.println("发布消息：" + msg);
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
