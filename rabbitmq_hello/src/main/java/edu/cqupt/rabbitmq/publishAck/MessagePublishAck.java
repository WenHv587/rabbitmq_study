package edu.cqupt.rabbitmq.publishAck;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import edu.cqupt.rabbitmq.workQueue.RabbbitmqUtils;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author LWenH
 * @create 2021/7/27 - 11:36
 * <p>
 * 消息的发布确认
 * 1. 单个发布确认模式：发布者每发布一条消息就等待确认，收到确认才接着发布。 706ms
 * 2. 批量发布确认模式：发送一定数量的消息进行一次确认。95ms
 * 3. 异步发布确认模式：通过异步的方式，监听发布确认消息。利用回调函数进行确认/未确认的处理。 45ms
 */
public class MessagePublishAck {
    /**
     * 发送消息数量
     */
    private static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
//        publishAckIndividually();
//        publishAckBatch();
        publishAckAsync();
    }


    /**
     * 异步发布确认：通过异步的方式，监听发布确认消息。利用回调函数进行确认/未确认的处理
     */
    private static void publishAckAsync() throws Exception {
        Channel channel = RabbbitmqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        // 生成一个队列
        channel.queueDeclare(queueName, false, false, false, null);
        // 开启发布确认功能 发布者可以得到确认
        channel.confirmSelect();
        /*
            ConcurrentSkipListMap：线程安全的并发访问的**排序**映射表。内部是SkipList（跳表）结构实现。
            将发布出去的消息装进集合，收到确认的从集合中删除，最后剩下的就是未收到确认需要重传的消息。
         */
        ConcurrentSkipListMap<Long, String> map = new ConcurrentSkipListMap<>();
        // 收到确认的回调
        ConfirmCallback ackCallback = (sequenceNumber, multiple) -> {
            // 第二个参数multiple代表是否批量确认 true代表累积确认 false代表只确认当前
            if (multiple) {
                System.out.println("批量确认：" + sequenceNumber);
                // 如果是累计确认，得到被确认的部分。这里返回的是原本map的部分视图，删除这里的元素，原map中的元素也会被删除
                ConcurrentNavigableMap<Long, String> confirmedMap = map.headMap(sequenceNumber, true);
                // 清除被累积确认的所有消息
                confirmedMap.clear();
            } else {
                // 清除当前被确认的消息
                System.out.println("单个确认：" + sequenceNumber);
                map.remove(sequenceNumber);
            }
        };

        // 未收到确认的回调
        ConfirmCallback nackCallback = (sequenceNumber, multiple) -> {
            String msg = map.get(sequenceNumber);
            System.out.println("发布的消息" + msg + "未被确认，序列号" + sequenceNumber);
        };

        // 添加确认消息的监听器
        channel.addConfirmListener(ackCallback, nackCallback);
        long start = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String msg = i + "";
            // 将发布过的消息全部存储在map中
            map.put(channel.getNextPublishSeqNo(), msg);
            channel.basicPublish("", queueName, null, msg.getBytes());

        }
        long end = System.currentTimeMillis();
        System.out.println("批量发布确认模式——消息发送完毕，共消耗：" + (end - start) + "ms");

    }

    /**
     * 批量发布确认：累计确认。发送一批消息，一起进行确认。
     */
    private static void publishAckBatch() {
        try (Channel channel = RabbbitmqUtils.getChannel()) {
            String queueName = UUID.randomUUID().toString();
            // 生成一个队列
            channel.queueDeclare(queueName, false, false, false, null);
            // 开启发布确认功能 发布者可以得到确认
            channel.confirmSelect();
            long start = System.currentTimeMillis();
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String msg = i + "";
                channel.basicPublish("", queueName, null, msg.getBytes());
                if (i % 100 == 0) {
                    // 每发送100条消息，等待一次确认
                    boolean flag = channel.waitForConfirms();
                    // 这里1000条消息会得到9次确认
                    if (flag) {
                        System.out.println("得到确认");
                    }
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("批量发布确认模式——消息发送完毕，共消耗：" + (end - start) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 单个发布确认：发布的消息得到发布确认以后再发布下一条消息
     */
    private static void publishAckIndividually() {
        try (Channel channel = RabbbitmqUtils.getChannel()) {
            String queueName = UUID.randomUUID().toString();
            // 生成一个队列
            channel.queueDeclare(queueName, false, false, false, null);
            // 开启发布确认功能 发布者可以得到确认
            channel.confirmSelect();
            long start = System.currentTimeMillis();
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String msg = i + "";
                channel.basicPublish("", queueName, null, msg.getBytes());
                // 每发送一条消息，进等待mq的确认
                boolean flag = channel.waitForConfirms();
                if (flag) {
                    System.out.println("得到确认");
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("单个发布确认模式——消息发送完毕，共消耗：" + (end - start) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
