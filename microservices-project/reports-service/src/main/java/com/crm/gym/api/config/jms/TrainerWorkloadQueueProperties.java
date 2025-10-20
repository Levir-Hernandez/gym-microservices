package com.crm.gym.api.config.jms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Component
@Getter @Setter
@ConfigurationProperties(prefix = "spring.activemq.queues.trainer-workload")
public class TrainerWorkloadQueueProperties
{
    private String getOneRequest;   private String getOneResponse;
    private String getAllRequest;   private String getAllResponse;
    private String updateRequest;

    public TrainerWorkloadQueueProperties()
    {
        this.getOneRequest = "trainer.workload.get_one.request.queue";
        this.getOneResponse = "trainer.workload.get_one.response.queue";
        this.getAllRequest = "trainer.workload.get_all.request.queue";
        this.getAllResponse = "trainer.workload.get_all.response.queue";
        this.updateRequest = "trainer.workload.update.request.queue";
    }
}
