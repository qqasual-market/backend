package org.example.market.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.market.response.TokenResponse;
import org.example.market.services.JwtTokensService;
import org.example.market.services.UserService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION = "/auth/login";
    private static final String REGISTRATION_PATH = "/auth/registration";
    public static final String BEARER_PREFIX = "Bearer ";
    private final UserService userService;
    private final JwtTokensService jwtTokensService;
    public final static Logger logger = LogManager.getLogger(JwtTokensService.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @org.springframework.lang.NonNull HttpServletResponse response,
            @org.springframework.lang.NonNull FilterChain filterChain)
            throws ServletException, IOException {
        var accessToken = request.getHeader("AccessToken");
        var refreshToken = request.getHeader("RefreshToken");
        final String path = request.getPathInfo().toString();

        if (StringUtils.isEmpty(accessToken)
                && StringUtils.isEmpty(refreshToken)
                && path.equals(AUTHORIZATION)
                || path.equals(REGISTRATION_PATH)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null
                && refreshToken != null && accessToken != null) {
            boolean validateRefresh = jwtTokensService.validateRefreshToken(refreshToken);
            boolean validateAccess = jwtTokensService.validateAccessToken(accessToken);
        if (validateRefresh && validateAccess) {
            var usernameFromToken = jwtTokensService.getAccessClaims(accessToken.toString()).getSubject();
                AuthenticateUserFromRequest(usernameFromToken, request);
            filterChain.doFilter(request, response);
        }

        else if (validateRefresh && !validateAccess) {
            TokenResponse tokenResponse = jwtTokensService.updateAccessTokenByRefreshToken(refreshToken);
            var usernameInTokenNew = jwtTokensService.getAccessClaims(tokenResponse.getAccessToken()).getSubject();
                AuthenticateUserFromRequest(usernameInTokenNew, request);
                response.addHeader("AccessToken", tokenResponse.getAccessToken());
                Cookie cookie = new Cookie("RefreshToken", refreshToken);
                response.addCookie(cookie);
            filterChain.doFilter(request, response);
            }
        }
    }

    public void AuthenticateUserFromRequest(String usernameFromToken, HttpServletRequest request) throws IOException {
        UserDetails userDetails = userService.getUserDetailsService().loadUserByUsername(usernameFromToken);

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    }
}
