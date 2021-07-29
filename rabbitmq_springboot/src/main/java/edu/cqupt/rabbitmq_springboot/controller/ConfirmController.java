package edu.cqupt.rabbitmq_springboot.controller;

import edu.cqupt.rabbitmq_springboot.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * @author wddv587
 * @create 2021/7/29 - 16:04
 * <p>
 * 发布确认模块测试用
 */
@RestController
@Slf4j
@RequestMapping("/confirm")
public class ConfirmController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MyCallBack myCallBack;

    /**
     * 当这个Controller创建bean实例化时，会调用这个方法，为rabbitTemplate设置确认回调
     */
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(myCallBack);
        /*
            当消息未能成功发送到队列
            true：回退消息
            false: 丢弃消息
         */
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(myCallBack);
    }

    @GetMapping("/sendMsg/{message}")
    public String sendMsg(@PathVariable("message") String message) {
        CorrelationData correlationData1 = new CorrelationData("1");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE,
                ConfirmConfig.CONFIRM_ROUTING_KEY, message + "-1", correlationData1);
        log.info("发送消息内容:{}", message);

        CorrelationData correlationData2 = new CorrelationData("2");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE,
                ConfirmConfig.CONFIRM_ROUTING_KEY + "123", message + "-2", correlationData2);
        log.info("发送消息内容:{}", message);
        return "send successfully";
    }

    /**
     * 发布确认的回调接口
     */
    @Component
    public static class MyCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

        /**
         * 确认的回调方法
         *
         * @param correlationData 消息的相关数据
         * @param ack             是否得到交换机的确认
         * @param cause           未得到确认的原因
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            String id = correlationData == null ? "" : correlationData.getId();
            if (ack) {
                log.info("id为{}的消息得到了交换机的确认", id);
            } else {
                log.info("id为{}的消息未得到交换机的确认,原因:{}", id, cause);
            }
        }

        /**
         * 消息无法送达队列（无法路由）时的回调
         *
         * @param returned
         */
        @Override
        public void returnedMessage(ReturnedMessage returned) {
            Message message = returned.getMessage();
            String exchange = returned.getExchange();
            String replyText = returned.getReplyText();
            String routingKey = returned.getRoutingKey();
            log.warn("消息{},被交换机{}退回，退回原因:{},路由key:{}", new String(message.getBody()), exchange, replyText, routingKey);
        }
    }

}
