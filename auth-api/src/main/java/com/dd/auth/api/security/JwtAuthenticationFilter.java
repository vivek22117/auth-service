package com.dd.auth.api.security;

import com.dd.auth.api.service.ProfileUserDetailService;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = getLogger(JwtAuthenticationFilter.class);

    private final AppJwtTokenUtil jwtTokenUtil;
    private final ProfileUserDetailService profileDetailService;

    @Autowired
    public JwtAuthenticationFilter(AppJwtTokenUtil jwtTokenUtil,
                                   ProfileUserDetailService profileDetailService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.profileDetailService = profileDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwtFromRequest = getJwtFromRequest(request);

        if (StringUtils.hasText(jwtFromRequest) && jwtTokenUtil.validateToken(jwtFromRequest)) {

            try {

                String username = jwtTokenUtil.getUsernameFromToken(jwtFromRequest);

                UserDetails userDetails = profileDetailService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (RuntimeException ex) {
                SecurityContextHolder.clearContext();
                throw ex;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        LOGGER.info("BEARER..." + bearerToken);
        return bearerToken;
    }
}
