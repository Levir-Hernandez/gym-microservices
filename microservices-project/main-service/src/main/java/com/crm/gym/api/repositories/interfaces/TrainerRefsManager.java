package com.crm.gym.api.repositories.interfaces;

import com.crm.gym.api.entities.Trainer;

public interface TrainerRefsManager
{
    void resolveReferencesByAltKeys(Trainer trainer);
    void nullifyInvalidReferences(Trainer trainer);
}
