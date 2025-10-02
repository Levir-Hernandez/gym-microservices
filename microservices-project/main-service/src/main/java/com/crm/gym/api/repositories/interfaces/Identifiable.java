package com.crm.gym.api.repositories.interfaces;

public interface Identifiable<Id>
{
    Id getId();
    void setId(Id id);
}
