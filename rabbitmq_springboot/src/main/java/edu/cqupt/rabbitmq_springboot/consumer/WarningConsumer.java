package edu.cqupt.rabbitmq_springboot.consumer;

import edu.cqupt.rabbitmq_springboot.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author LWenH
 * @create 2021/7/29 - 17:06
 *
 * 报警消费者——消费与备用交换机绑定的报警队列warning_queue的消息
 */
@Component
@Slf4j
public class WarningConsumer {

    @RabbitListener(queues = ConfirmConfig.WARNING_QUEUE)
    public void receive(Message message) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.warn("{}报警队列收到了不可路由的消息：{}", ConfirmConfig.WARNING_QUEUE, msg);
    }
}
