package com.lambdaschool.dogsinitial;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
public class DogsinitialApplication
{
    public static final String EXCHANGE_NAME = "LambdaServer";
    public static final String QUEUE_NAME_LOW = "LowPriorityQueue";
    public static final String QUEUE_NAME_HIGH = "HighPriorityQueue";
    public static final String QUEUE_ERROR = "QUEUE_ERROR";

    public static DogList ourDogList;
    public static void main(String[] args)
    {
        ourDogList = new DogList();
        ApplicationContext ctx = SpringApplication.run(DogsinitialApplication.class, args);

        DispatcherServlet dispatcherServlet = (DispatcherServlet)ctx.getBean("dispatcherServlet");
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
    }

    @Bean
    public TopicExchange appExchange()
    {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue appQueueHigh()
    {
        return new Queue(QUEUE_NAME_HIGH);
    }

    @Bean
    public Binding declareBindingHigh()
    {
        return BindingBuilder.bind(appQueueHigh()).to(appExchange()).with(QUEUE_NAME_HIGH);
    }

    @Bean
    public Queue appQueueLow()
    {
        return new Queue(QUEUE_NAME_LOW);
    }

    @Bean
    public Binding declareBindingLow()
    {
        return BindingBuilder.bind(appQueueHigh()).to(appExchange()).with(QUEUE_NAME_LOW);
    }

    @Bean
    public Queue appQueueError()
    {
        return new Queue(QUEUE_ERROR);
    }

    @Bean
    public Binding declareBindingError()
    {
        return BindingBuilder.bind(appQueueError()).to(appExchange()).with(QUEUE_ERROR);
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter()
    {
        return new Jackson2JsonMessageConverter();
    }
}


