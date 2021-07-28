package edu.cqupt.rabbitmq_springboot.controller;

import edu.cqupt.rabbitmq_springboot.config.DelayedQueueConfig;
import edu.cqupt.rabbitmq_springboot.config.TtlQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author LWenH
 * @create 2021/7/28 - 21:11
 */
@Slf4j
@RestController
public class MessageController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("delayed/{message}/{delayedTime}")
    public String sendDelayedMessage(@PathVariable("message") String message,
                                     @PathVariable("delayedTime") Integer delayedTime) {
        rabbitTemplate.convertAndSend(DelayedQueueConfig.DELAYED_EXCHANGE,
                DelayedQueueConfig.DELAYED_ROUTING_KEY, message, msg -> {
                    msg.getMessageProperties().setDelay(delayedTime);
                    return msg;
                });
        log.info("当前时间：{},发送一条延迟时间为{}毫秒的信息给delayed_queue,信息内容是：{}", new Date(), delayedTime, message);
        return "send delay info successfully";
    }

    @GetMapping("/sendMsg/{msg}/{ttl}")
    public String sendTtlMsg(@PathVariable("msg") String msg,
                             @PathVariable("ttl") String ttl) {
        rabbitTemplate.convertAndSend(TtlQueueConfig.NORMAL_EXCHANGE,
                TtlQueueConfig.NORMAL_ROUTING_KEY, msg, message -> {
                    message.getMessageProperties().setExpiration(ttl);
                    return message;
                });
        log.info("当前时间：{},发送一条ttl为{}毫秒的信息给normal队列,信息内容是：{}", new Date(), ttl, msg);
        return "send success";
    }
}
