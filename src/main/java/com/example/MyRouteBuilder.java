package com.example;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.jms;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.timer;

import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.ConfigProvider;

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;

import jakarta.enterprise.inject.Produces;
import jakarta.jms.ConnectionFactory;

public class MyRouteBuilder extends RouteBuilder {
    @Produces
    ConnectionFactory createConnectionFactory() {
        MQConnectionFactory connectionFactory = new MQConnectionFactory();
        connectionFactory.setHostName(ConfigProvider.getConfig().getValue("host", String.class));
        try {
            connectionFactory.setPort(ConfigProvider.getConfig().getValue("port", Integer.class));
            connectionFactory.setChannel(ConfigProvider.getConfig().getValue("channel", String.class));
            connectionFactory.setQueueManager(ConfigProvider.getConfig().getValue("queue.manager", String.class));
            connectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            connectionFactory.setStringProperty(WMQConstants.USERID, ConfigProvider.getConfig().getValue("userid", String.class));
            connectionFactory.setStringProperty(WMQConstants.PASSWORD, ConfigProvider.getConfig().getValue("password", String.class));
        } catch (Exception e) {
            throw new RuntimeException("Unable to create new IBM MQ connection factory", e);
        }
        return connectionFactory;
    }

    @Override
    public void configure() throws Exception {
        from(timer("x").repeatCount(1)).setBody(constant("Hello")).to(jms("queue:DEV.QUEUE.1"));
        from(jms("queue:DEV.QUEUE.1")).log("${body}");
    }
}
