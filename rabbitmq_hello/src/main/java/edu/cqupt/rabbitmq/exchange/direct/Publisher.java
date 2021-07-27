package edu.cqupt.rabbitmq.exchange.direct;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import edu.cqupt.rabbitmq.workQueue.RabbbitmqUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author LWenH
 * @create 2021/7/27 - 20:06
 */
public class Publisher {
    private static final String EXCHANGE_NAME = "direct";

    public static void main(String[] args) {
        try (Channel channel = RabbbitmqUtils.getChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            // 创建发送给不同routingKey的消息
            Map<String, String> keyMap = new HashMap<>();
            keyMap.put("info","info消息");
            keyMap.put("warning","warning警告");
            keyMap.put("error","error错误");
            // 与 direct 交换机绑定的所有队列中，没有人接收routingKey为debug的消息 所以没人处理 消息丢失
            keyMap.put("debug","debug日志");

            for (String key : keyMap.keySet()) {
                channel.basicPublish(EXCHANGE_NAME, key, null, keyMap.get(key).getBytes(StandardCharsets.UTF_8));
                System.out.println("生产者发布了routingKey为：" + key + "的消息");
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
