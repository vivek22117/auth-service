package com.dd.auth.api.config;

import com.dd.auth.api.security.JwtAuthenticationFilter;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@Slf4j
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public AppSecurityConfig(@Qualifier("profileUserDetailService") UserDetailsService userDetailService,
                             JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailService = userDetailService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /*
        We also need to register the TimedAspect bean in our Spring context.
        This will allow Micrometer to add a timer to custom methods.
    */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/auth/**")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/api/**")
                .permitAll()
                .antMatchers("/internal",
                        "/h2-console/**",
                        "/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**")
                .permitAll()
                .antMatchers("/api/comments**").hasAuthority("ADMIN READ")
                .antMatchers("/api/ddblog**").hasAuthority("ADMIN READ")
                .antMatchers("/api/ddblog/post**").hasAuthority("ADMIN READ")
                .antMatchers("/api/votes**").hasAuthority("ADMIN READ")
                .anyRequest()
                .authenticated();
        httpSecurity.headers().frameOptions().disable();
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
    }
}
