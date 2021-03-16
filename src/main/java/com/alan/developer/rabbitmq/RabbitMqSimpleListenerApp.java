package com.alan.developer.rabbitmq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.IOException;

@SpringBootApplication
public class RabbitMqSimpleListenerApp {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMqSimpleListenerApp.class, args);
    }

    /**
     * Default sample channel name to respond for requests from clients.
     */
    public static final String DEFAULT_QUEUE = "test";

    /**
     * Environment properties file from rabbitmq configuration.
     */
    @Autowired
    private Environment env;

    /**
     * Establish a connection to a rabbit mq server.
     *
     * @return Rabbit connection factory for rabbitmq access.
     * @throws IOException If wrong parameters are used for connection.
     */
    @Bean
    public RabbitConnectionFactoryBean connectionFactoryBean() throws IOException {
        RabbitConnectionFactoryBean connectionFactoryBean = new RabbitConnectionFactoryBean();
        connectionFactoryBean.setHost(env.getProperty("rabbit.host"));
        connectionFactoryBean.setPort(new Integer(env.getProperty("rabbit.port")));
        connectionFactoryBean.setUsername(env.getProperty("rabbit.username"));
        connectionFactoryBean.setPassword(env.getProperty("rabbit.password"));
        //connectionFactoryBean.setSkipServerCertificateValidation(false);
        connectionFactoryBean.setEnableHostnameVerification(false);

        // SSL-Configuration if set
        if (env.getProperty("rabbit.ssl") != null) {
            connectionFactoryBean.setUseSSL(true);
            connectionFactoryBean.setSslAlgorithm(env.getProperty("rabbit.ssl"));

            // This information should be stored safely !!!
            connectionFactoryBean.setKeyStore(env.getProperty("rabbit.keystore.name"));
            connectionFactoryBean.setKeyStorePassphrase(env.getProperty("rabbit.keystore.password"));
            connectionFactoryBean.setTrustStore(env.getProperty("rabbit.truststore"));
            connectionFactoryBean.setTrustStorePassphrase(env.getProperty("rabbit.truststore.password"));
        }

        return connectionFactoryBean;
    }

    /**
     * Connection factory which established a rabbitmq connection used from a connection factory
     *
     * @param connectionFactoryBean Connection factory bean to create connection.
     * @return A connection factory to create connections.
     * @throws Exception If wrong parameters are used for connection.
     */
    @Bean(name = "GEO_RABBIT_CONNECTION")
    public ConnectionFactory connectionFactory(RabbitConnectionFactoryBean connectionFactoryBean) throws Exception {
        return new CachingConnectionFactory(connectionFactoryBean.getObject());
    }

    /**
     * Queue initialization from rabbitmq to listen a queue.
     *
     * @return An queue to listen for listen receiver.
     */
    @Bean
    public Queue queue() {
        // Create an new queue to handle incoming responds
        return new Queue(DEFAULT_QUEUE, true, false, false, null);
    }

    @Bean
    Queue copsEventQueue() {
        return new Queue(DEFAULT_QUEUE);
    }


    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(DEFAULT_QUEUE);
        container.setMessageListener(listenerAdapter);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}
