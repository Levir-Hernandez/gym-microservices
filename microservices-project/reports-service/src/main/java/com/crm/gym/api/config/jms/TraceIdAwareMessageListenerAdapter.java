package com.crm.gym.api.config.jms;

import org.slf4j.MDC;
import java.util.Optional;
import jakarta.jms.Session;
import jakarta.jms.Message;
import jakarta.jms.JMSException;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

public class TraceIdAwareMessageListenerAdapter extends MessageListenerAdapter
{
    public TraceIdAwareMessageListenerAdapter(Object delegate)
    {
        super(delegate);
    }

    @Override
    public void onMessage(Message message, Session session) throws JMSException
    {
        Optional.ofNullable(message.getJMSCorrelationID())
                .ifPresent(traceId -> MDC.put("traceId", traceId));
        try {super.onMessage(message, session);}
        finally {MDC.remove("traceId");}
    }
}