package com.crm.gym.api.repositories.interfaces;

import com.crm.gym.api.entities.Training;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.crm.gym.api.repositories.TrainingQueryCriteria;

import java.util.List;

public interface DynamicQueryRepository
{
    List<Training> findByCriteria(TrainingQueryCriteria criteria);
    Page<Training> findByCriteria(TrainingQueryCriteria criteria, Pageable pageable);
}
