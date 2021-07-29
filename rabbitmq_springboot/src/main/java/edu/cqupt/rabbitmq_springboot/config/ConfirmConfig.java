package edu.cqupt.rabbitmq_springboot.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LWenH
 * @create 2021/7/29 - 15:38
 *
 * 发布确认模式相关配置
 * 之前的发布确认是当消息确认发送到指定队列以后，为生产者返回确认消息。这全部都建立在rabbitmq本身组件正常工作的基础之上。
 * 现在配置的是发布确认的高级部分：当生产者发布消息的时候，如果rabbitmq挂掉了，或是交换机、队列其中一个出了问题，如何处理消息
 */
@Configuration
public class ConfirmConfig {
    public static final String CONFIRM_QUEUE = "confirm_queue";
    public static final String CONFIRM_EXCHANGE = "confirm_exchange";
    public static final String CONFIRM_ROUTING_KEY = "confirm";
    public static final String BACKUP_EXCHANGE = "backup_exchange";
    public static final String WARNING_QUEUE = "warning_queue";
    public static final String BACKUP_QUEUE = "backup_queue";

    @Bean
    public Binding warningBinding(@Qualifier(WARNING_QUEUE) Queue queue,
                                  @Qualifier(BACKUP_EXCHANGE) FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @Bean
    public Binding backupBinding(@Qualifier(BACKUP_QUEUE) Queue queue,
                                 @Qualifier(BACKUP_EXCHANGE) FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @Bean
    public Binding confirmBinding(@Qualifier(CONFIRM_QUEUE) Queue queue,
                                  @Qualifier(CONFIRM_EXCHANGE) DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(CONFIRM_ROUTING_KEY);
    }

    @Bean(BACKUP_EXCHANGE)
    public FanoutExchange backupExchange() {
        return new FanoutExchange(BACKUP_EXCHANGE);
    }

    @Bean(CONFIRM_EXCHANGE)
    public DirectExchange confirmExchange() {
        Exchange exchange = ExchangeBuilder.directExchange(CONFIRM_EXCHANGE).durable(true)
                // 为交换机指定备用交换机
                .withArgument("alternate-exchange", BACKUP_EXCHANGE).build();
        return (DirectExchange) exchange;
    }

    @Bean(WARNING_QUEUE)
    public Queue warningQueue() {
        return QueueBuilder.durable(WARNING_QUEUE).build();
    }

    @Bean(BACKUP_QUEUE)
    public Queue backupQueue() {
        return QueueBuilder.durable(BACKUP_QUEUE).build();
    }

    @Bean(CONFIRM_QUEUE)
    public Queue confirmQueue() {
        return QueueBuilder.durable(CONFIRM_QUEUE).build();
    }
}
