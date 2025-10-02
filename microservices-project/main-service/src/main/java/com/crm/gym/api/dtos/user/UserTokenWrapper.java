package com.crm.gym.api.dtos.user;

import lombok.Getter;
import lombok.Setter;
import java.util.Optional;
import java.util.function.Supplier;
import com.crm.gym.api.auth.services.JwtTokenService;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter @Setter
public abstract class UserTokenWrapper implements UserRespDto
{
    private UserRespDto user;
    private String accessToken;
    private String refreshToken;

    @Override
    @JsonIgnore
    public String getUsername()
    {
        return Optional.ofNullable(user)
                .map(UserRespDto::getUsername)
                .orElse(null);
    }

    @Override
    public void setUsername(String username)
    {
        Optional.ofNullable(user).ifPresent(u -> u.setUsername(username));
    }

    public abstract static class Builder<Dto extends UserRespDto>
    {
        private JwtTokenService jwtTokenService;

        public Builder(JwtTokenService jwtTokenService)
        {
            this.jwtTokenService = jwtTokenService;
        }

        public abstract Dto wrap(Dto user);

        protected Dto wrap(Supplier<? extends UserTokenWrapper> wrapperSupplier, Dto user, String role)
        {
            UserTokenWrapper userTokenWrapper = wrapperSupplier.get();
            userTokenWrapper.setUser(user);

            Optional.ofNullable(user)
            .map(UserRespDto::getUsername)
            .ifPresent(username -> {
                userTokenWrapper.setAccessToken(jwtTokenService.issueAccessToken(username, role));
                userTokenWrapper.setRefreshToken(jwtTokenService.issueRefreshToken(username, role));
            });

            return (Dto) userTokenWrapper;
        }
    }
}
