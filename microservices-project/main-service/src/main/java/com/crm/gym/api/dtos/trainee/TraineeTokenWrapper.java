package com.crm.gym.api.dtos.trainee;

import com.crm.gym.api.auth.services.JwtTokenService;
import com.crm.gym.api.dtos.user.UserRespDto;
import com.crm.gym.api.dtos.user.UserTokenWrapper;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "trainees")
public class TraineeTokenWrapper extends UserTokenWrapper implements TraineeRespDto
{
    @Override
    @JsonProperty(value = "trainee", index = 0)
    public UserRespDto getUser()
    {
        return super.getUser();
    }

    @Component
    public static class Builder extends UserTokenWrapper.Builder<TraineeRespDto>
    {
        public Builder(JwtTokenService jwtTokenService)
        {
            super(jwtTokenService);
        }

        @Override
        public TraineeRespDto wrap(TraineeRespDto user)
        {
            return wrap(TraineeTokenWrapper::new, user, "TRAINEE");
        }
    }
}
