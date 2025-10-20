package com.crm.gym.api.controllers;

import com.crm.gym.api.auth.services.JwtTokenService;
import com.crm.gym.api.auth.services.LoginLockoutService;
import com.crm.gym.api.auth.services.TokenBlacklistService;
import com.crm.gym.api.dtos.user.UserChangePasswordRequest;
import com.crm.gym.api.dtos.user.UserLoginRequest;
import com.crm.gym.api.dtos.user.UserRespDto;
import com.crm.gym.api.dtos.user.UserTokenWrapper;
import com.crm.gym.api.entities.User;
import com.crm.gym.api.exceptions.*;
import com.crm.gym.api.repositories.interfaces.UserRepository;
import com.crm.gym.api.services.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class UserController<
        Service extends UserService<? extends User, ? extends UserRepository<? extends User>>,
        Dto extends UserRespDto>
{
    private JwtTokenService jwtTokenManager;
    private TokenBlacklistService tokenBlacklistService;
    private LoginLockoutService loginLockoutService;

    private UserTokenWrapper.Builder<Dto> userTokenWrapperBuilder;
    private RepresentationModelAssembler<Dto, EntityModel<Dto>> assembler;
    private Service userService;

    public UserController(
            Service userService,
            RepresentationModelAssembler<Dto, EntityModel<Dto>> assembler,
            UserTokenWrapper.Builder<Dto> userTokenWrapperBuilder
    )
    {
        this.userService = userService;
        this.assembler = assembler;
        this.userTokenWrapperBuilder = userTokenWrapperBuilder;
    }

    protected abstract Supplier<Dto> userRefSupplier();

    protected EntityModel<Dto> login(UserLoginRequest userRequestDto)
    {
        String username = userRequestDto.getUsername();

        if(loginLockoutService.isUserBlocked(username)) {throw new UserLockoutException();}

        boolean logged = userService.login(username, userRequestDto.getPassword());
        if(!logged)
        {
            loginLockoutService.incrementUserFailedLogins(username);
            throw new InvalidCredentialsException();
        }
        loginLockoutService.resetUserFailedLogins(username);

        Dto userRespDto = userRefSupplier().get();
        userRespDto.setUsername(username);

        return assembler.toModel(userTokenWrapperBuilder.wrap(userRespDto));
    }

    protected EntityModel<Dto> changePassword(UserChangePasswordRequest userRequestDto)
    {
        String username = userRequestDto.getUsername();

        if(loginLockoutService.isUserBlocked(username)) {throw new UserLockoutException();}

        boolean passwordChanged = userService.changePassword(
                username,
                userRequestDto.getOldPassword(),
                userRequestDto.getNewPassword()
        );

        if(!passwordChanged)
        {
            loginLockoutService.incrementUserFailedLogins(username);
            throw new InvalidCredentialsException();
        }
        loginLockoutService.resetUserFailedLogins(username);

        Dto userRespDto = userRefSupplier().get();
        userRespDto.setUsername(username);

        return assembler.toModel(userRespDto);
    }

    protected ResponseEntity<Void> logout(String refreshToken)
    {
        Claims accessTokenClaims = (Claims) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        tokenBlacklistService.blacklistTokenFromClaims(accessTokenClaims);

        if(Objects.nonNull(refreshToken))
        {
            Claims refreshTokenClaims = jwtTokenManager.decodeToken(refreshToken);

            String refreshTokenType = refreshTokenClaims.get("type", String.class);
            if(!refreshTokenType.equals("refresh")) {throw new UnexpectedTokenTypeException();}

            tokenBlacklistService.blacklistTokenFromClaims(refreshTokenClaims);
        }

        return ResponseEntity.noContent().build();
    }

    protected EntityModel<Dto> refresh(String refreshToken, String requiredRefreshTokenRole)
    {
        Claims refreshTokenClaims = jwtTokenManager.decodeToken(refreshToken);

        String refreshTokenType = refreshTokenClaims.get("type", String.class);
        if(!refreshTokenType.equals("refresh")){throw new UnexpectedTokenTypeException();}

        String refreshTokenRole = refreshTokenClaims.get("role", String.class);
        if(!refreshTokenRole.equals(requiredRefreshTokenRole)){throw new PermissionDeniedException();}

        String refreshTokenJti = refreshTokenClaims.getId();
        if(tokenBlacklistService.isTokenBlacklisted(refreshTokenJti)){throw new RevokedTokenException();}

        tokenBlacklistService.blacklistTokenFromClaims(refreshTokenClaims); // Refresh Token Rotation

        Dto userRespDto = userRefSupplier().get();
        userRespDto.setUsername(refreshTokenClaims.getSubject());

        return assembler.toModel(userTokenWrapperBuilder.wrap(userRespDto));
    }

    @Autowired
    private void setJwtTokenManager(JwtTokenService jwtTokenService)
    {
        this.jwtTokenManager = jwtTokenService;
    }

    @Autowired
    private void setJwtBlacklistService(TokenBlacklistService tokenBlacklistService)
    {
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Autowired
    private void setLoginAttemptService(LoginLockoutService loginLockoutService)
    {
        this.loginLockoutService = loginLockoutService;
    }
}
