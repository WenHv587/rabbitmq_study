package edu.cqupt.rabbitmq_springboot.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LWenH
 * @create 2021/7/28 - 23:24
 *
 * 利用rebbitmq的插件实现延时队列 此处做相关配置
 */
@Configuration
public class DelayedQueueConfig {

    public static final String DELAYED_QUEUE = "dalayed_queue";
    public static final String DELAYED_EXCHANGE = "delayed_exchange";
    public static final String DELAYED_ROUTING_KEY = "delayed";

    @Bean
    public Binding delayBinding(@Qualifier(DELAYED_QUEUE) Queue queue,
                                @Qualifier(DELAYED_EXCHANGE) CustomExchange delayExchange) {
        return BindingBuilder.bind(queue).to(delayExchange).with(DELAYED_ROUTING_KEY).noargs();
    }

    @Bean(DELAYED_EXCHANGE)
    public CustomExchange delayedExchange() {
        Map<String, Object> params = new HashMap<>();
        // 自定义交换机类型
        params.put("x-delayed-type", "direct");
        return new CustomExchange(DELAYED_EXCHANGE,"x-delayed-message", true, false, params);
    }

    @Bean(DELAYED_QUEUE)
    public Queue delayedQueue() {
        return new Queue(DELAYED_QUEUE);
    }
}
