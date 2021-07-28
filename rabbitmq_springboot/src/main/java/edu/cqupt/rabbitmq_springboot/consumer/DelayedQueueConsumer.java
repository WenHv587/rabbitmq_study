package edu.cqupt.rabbitmq_springboot.consumer;

import edu.cqupt.rabbitmq_springboot.config.DelayedQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author LWenH
 * @create 2021/7/29 - 0:20
 */
@Component
@Slf4j
public class DelayedQueueConsumer {

    @RabbitListener(queues = DelayedQueueConfig.DELAYED_QUEUE)
    public void receive(Message message) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("当前时间：{},收到延迟队列信息{}", new Date().toString(), msg);
    }
}
