package org.example.market.services;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.market.dao.UserRepository;
import org.example.market.exception.UserExistsException;
import org.example.market.model.User;
import org.example.market.model.enums.RoleEnum;
import org.example.market.requests.JwtAuthenticationResponse;
import org.example.market.requests.Register;

import org.example.market.requests.SignInRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    public static Logger logger = LogManager.getLogger(AuthorizationService.class);
    private final UserService userService;
    private final JwtTokensService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void registrationUser(Register register) {
        Optional<User> usernameInUser = userRepository.findByUsername(register.getUsername());
        if (usernameInUser.isPresent()) {
            throw new UserExistsException("The user with the given name already exists");
        }
        BigDecimal balance = BigDecimal.valueOf(0);
        userRepository.save(User.builder()
                .username(register.getUsername())
                .password(passwordEncoder.encode(register.getPassword()))
                .balance(balance)
                .roles(Collections.singleton(RoleEnum.ROLE_USER))
                .build());
    }

    @Transactional
    public JwtAuthenticationResponse signIn(SignInRequest signInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signInRequest.getUsername(),
                signInRequest.getPassword()
        ));

        var user = userService.getUserDetailsService().loadUserByUsername(signInRequest.getUsername());

        String jwtAccess = jwtService.generateAccessToken(user);
        String jwtRefresh = jwtService.generateRefreshToken(user);

        return new JwtAuthenticationResponse(jwtAccess, jwtRefresh);
    }
}
