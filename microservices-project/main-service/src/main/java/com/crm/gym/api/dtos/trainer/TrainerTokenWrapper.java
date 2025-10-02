package com.crm.gym.api.dtos.trainer;

import com.crm.gym.api.auth.services.JwtTokenService;
import com.crm.gym.api.dtos.user.UserRespDto;
import com.crm.gym.api.dtos.user.UserTokenWrapper;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.server.core.Relation;


@Relation(collectionRelation = "trainers")
public class TrainerTokenWrapper extends UserTokenWrapper implements TrainerRespDto
{
    @Override
    @JsonProperty(value = "trainer", index = 0)
    public UserRespDto getUser()
    {
        return super.getUser();
    }

    @Component
    public static class Builder extends UserTokenWrapper.Builder<TrainerRespDto>
    {
        public Builder(JwtTokenService jwtTokenService)
        {
            super(jwtTokenService);
        }

        @Override
        public TrainerRespDto wrap(TrainerRespDto user)
        {
            return wrap(TrainerTokenWrapper::new, user, "TRAINER");
        }
    }
}