package edu.cqupt.rabbitmq_springboot.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LWenH
 * @create 2021/7/28 - 19:06
 */
@Configuration
public class TtlQueueConfig {
    /**
     * 发布消息的普通队列
     */
    public static final String NORMAL_QUEUE = "normal_queue";
    /**
     * 发布交换机
     */
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    /**
     * 普通消息 routingKey
     */
    public static final String NORMAL_ROUTING_KEY = "normal";
    /**
     * 死信队列
     */
    public static final String DEAD_LETTER_QUEUE = "dead_letter_queue";
    /**
     * 死信交换机
     */
    public static final String DEAD_LETTER_EXCHANGE = "dead_letter_exchange";
    /**
     * 死信 routingKey
     */
    public static final String DEAD_ROUTING_KEY = "dead";

    /**
     * 绑定死信交换机和死信队列
     *
     * @param queue
     * @param directExchange
     * @return
     */
    @Bean
    public Binding deadBinding(@Qualifier(DEAD_LETTER_QUEUE) Queue queue,
                               @Qualifier(DEAD_LETTER_EXCHANGE) DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(DEAD_ROUTING_KEY);
    }

    /**
     * 绑定普通交换机和普通队列
     *
     * @return
     */
    @Bean
    public Binding normalBinding(@Qualifier(NORMAL_QUEUE) Queue queue,
                                 @Qualifier(NORMAL_EXCHANGE) DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(NORMAL_ROUTING_KEY);
    }

    /**
     * 生产者普通消息的队列 指定死信交换机的相关内容
     *
     * @return
     */
    @Bean(NORMAL_QUEUE)
    public Queue normalQueue() {
        Map<String, Object> params = new HashMap<>(2);
        // 指定与当前队列的死信交换机
        params.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        // 指定死信的路由routingKey
        params.put("x-dead-letter-routing-key", DEAD_ROUTING_KEY);
        return QueueBuilder.durable(NORMAL_QUEUE).withArguments(params).build();
    }

    /**
     * 死信队列
     *
     * @return
     */
    @Bean(DEAD_LETTER_QUEUE)
    public Queue deadLetterQueue() {
        return new Queue(DEAD_LETTER_QUEUE);
    }

    /**
     * 发送生产者消息的普通交换机
     *
     * @return
     */
    @Bean(NORMAL_EXCHANGE)
    public DirectExchange publishExchange() {
        return new DirectExchange(NORMAL_EXCHANGE);
    }

    /**
     * 死信交换机
     *
     * @return
     */
    @Bean(DEAD_LETTER_EXCHANGE)
    public DirectExchange deadExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

}
