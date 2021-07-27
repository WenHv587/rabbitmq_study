package edu.cqupt.rabbitmq.exchange.topic;

import com.rabbitmq.client.Channel;
import edu.cqupt.rabbitmq.workQueue.RabbbitmqUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LWenH
 * @create 2021/7/27 - 22:09
 */
public class Publisher {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] argv) {
        try (Channel channel = RabbbitmqUtils.getChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            /*
              Q1-->绑定的是: 中间带 orange 带 3 个单词的字符串(*.orange.*)
              Q2-->绑定的是: 最后一个单词是 rabbit 的 3 个单词(*.*.rabbit) & 第一个单词是 lazy 的多个单词(lazy.#) *
            */
            Map<String, String> bindingKeyMap = new HashMap<>();
            bindingKeyMap.put("quick.orange.rabbit", "被队列 Q1Q2 接收到");
            bindingKeyMap.put("lazy.orange.elephant", "被队列 Q1Q2 接收到");
            bindingKeyMap.put("quick.orange.fox", "被队列 Q1 接收到");
            bindingKeyMap.put("lazy.brown.fox", "被队列 Q2 接收到");
            bindingKeyMap.put("lazy.pink.rabbit", "虽然满足两个绑定但只被队列 Q2 接收一次");
            bindingKeyMap.put("quick.brown.fox", "不匹配任何绑定不会被任何队列接收到会被丢弃");
            bindingKeyMap.put("quick.orange.male.rabbit", "是四个单词不匹配任何绑定会被丢弃");
            bindingKeyMap.put("lazy.orange.male.rabbit", "是四个单词但匹配 Q2");

            for (String key : bindingKeyMap.keySet()) {
                channel.basicPublish(EXCHANGE_NAME, key, null, bindingKeyMap.get(key).getBytes(StandardCharsets.UTF_8));
                System.out.println("生产者发布了routingKey为：" + key + "的消息");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
