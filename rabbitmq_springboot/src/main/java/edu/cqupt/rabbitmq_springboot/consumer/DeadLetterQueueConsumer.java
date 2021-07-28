package edu.cqupt.rabbitmq_springboot.consumer;

import com.rabbitmq.client.Channel;
import edu.cqupt.rabbitmq_springboot.config.TtlQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author LWenH
 * @create 2021/7/28 - 20:58
 * <p>
 * 死信队列消费者
 */
@Slf4j
@Component
public class DeadLetterQueueConsumer {

    @RabbitListener(queues = TtlQueueConfig.DEAD_LETTER_QUEUE)
    public void receive(Message message, Channel channel) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("当前时间：{},收到死信队列信息{}", new Date().toString(), msg);
    }
}
