package com.crm.gym;

import org.apache.activemq.broker.BrokerService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ActiveMqServerApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ActiveMqServerApplication.class);
    }

    @Bean
    public BrokerService broker() throws Exception
    {
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://0.0.0.0:61616");
        broker.setPersistent(false);
        broker.start();
        return broker;
    }
}