package com.crm.gym.api.config.jms;

import java.util.Optional;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

public class TraceIdAwareJmsListenerContainerFactory extends DefaultJmsListenerContainerFactory
{
    @Override
    public DefaultMessageListenerContainer createListenerContainer(JmsListenerEndpoint endpoint)
    {
        DefaultMessageListenerContainer messageListenerContainer = super.createListenerContainer(endpoint);

        Optional.ofNullable(messageListenerContainer.getMessageListener())
                .map(TraceIdAwareMessageListenerAdapter::new)
                .ifPresent(messageListenerContainer::setMessageListener);

        return messageListenerContainer;
    }
}
