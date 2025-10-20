package com.crm.gym.client.reports;

import com.crm.gym.client.reports.dtos.TrainerWorkloadRequest;
import com.crm.gym.client.reports.dtos.TrainerWorkloadSummary;
import com.crm.gym.client.reports.config.TrainerWorkloadQueueProperties;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.http.ResponseEntity;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Component
public class TrainerWorkloadClientImpl implements TrainerWorkloadClient
{
    private JmsTemplate jmsTemplate;
    private Supplier<String> messageSelectorSupplier;
    private MessagePostProcessor messagePostProcessor;
    private TrainerWorkloadQueueProperties trainerWorkloadQueues;

    public TrainerWorkloadClientImpl(JmsTemplate jmsTemplate, Supplier<String> messageSelectorSupplier, MessagePostProcessor messagePostProcessor, TrainerWorkloadQueueProperties trainerWorkloadQueues)
    {
        this.jmsTemplate = jmsTemplate;
        this.messageSelectorSupplier = messageSelectorSupplier;
        this.messagePostProcessor = messagePostProcessor;
        this.trainerWorkloadQueues = trainerWorkloadQueues;
    }

    @Override
    @CircuitBreaker(name = "TrainerWorkloadClient", fallbackMethod = "fallbackForGetTrainerWorkloadByUsername")
    public ResponseEntity<TrainerWorkloadSummary> getTrainerWorkloadByUsername(String trainerUsername)
    {
        TrainerWorkloadSummary workload = convertSendAndReceive(
                trainerWorkloadQueues.getGetOneRequest(), trainerWorkloadQueues.getGetOneResponse(),
                trainerUsername, TrainerWorkloadSummary.class
        );

        if(Objects.isNull(workload)) {return ResponseEntity.notFound().build();}
        else {return ResponseEntity.ok(workload);}
    }

    @Override
    @CircuitBreaker(name = "TrainerWorkloadClient", fallbackMethod = "fallbackForGetAllTrainersWorkloads")
    public ResponseEntity<List<TrainerWorkloadSummary>> getAllTrainersWorkloads()
    {
        List<TrainerWorkloadSummary> workloads = convertSendAndReceive(
                trainerWorkloadQueues.getGetAllRequest(), trainerWorkloadQueues.getGetAllResponse(),
                "getAllTrainersWorkloads", List.class
        );
        return ResponseEntity.ok(workloads);
    }

    @Override
    @CircuitBreaker(name = "TrainerWorkloadClient", fallbackMethod = "fallbackForUpdateTrainerWorkload")
    public ResponseEntity<Void> updateTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest)
    {
        jmsTemplate.convertAndSend(trainerWorkloadQueues.getUpdateRequest(), trainerWorkloadRequest);
        return ResponseEntity.ok().build();
    }

    private <T> T convertSendAndReceive(String requestQueue, String responseQueue, Object message, Class<T> responseType)
    {
        jmsTemplate.convertAndSend(requestQueue, message, messagePostProcessor);
        Object response = jmsTemplate.receiveSelectedAndConvert(responseQueue, messageSelectorSupplier.get());
        return responseType.cast(response);
    }
}
