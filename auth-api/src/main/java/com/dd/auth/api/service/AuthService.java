package com.dd.auth.api.service;

import com.dd.auth.api.entity.Login;
import com.dd.auth.api.entity.PermissionSets;
import com.dd.auth.api.entity.Profile;
import com.dd.auth.api.entity.VerificationToken;
import com.dd.auth.api.exception.ApplicationException;
import com.dd.auth.api.exception.UserAuthenticationException;
import com.dd.auth.api.model.NotificationEmail;
import com.dd.auth.api.model.dto.AuthenticationResponse;
import com.dd.auth.api.model.dto.LoginRequest;
import com.dd.auth.api.model.dto.RefreshTokenRequest;
import com.dd.auth.api.model.dto.RegisterRequest;
import com.dd.auth.api.repository.LoginRepository;
import com.dd.auth.api.repository.PermissionSetsRepository;
import com.dd.auth.api.repository.ProfileRepository;
import com.dd.auth.api.repository.VerificationRepository;
import com.dd.auth.api.security.AppJwtTokenUtil;
import com.dd.auth.api.util.AppUtility;
import com.dd.auth.api.util.JsonUtility;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.LongStream;

import static java.util.UUID.randomUUID;
import static org.slf4j.LoggerFactory.getLogger;

@AllArgsConstructor
@Service
@Transactional
public class AuthService {
    private static final Logger LOGGER = getLogger(AuthService.class);

    private static final long JWT_TOKEN_VALIDITY = 3600;
    private static final Boolean IS_ADMIN_PROFILE = true;

    private final PermissionSetsRepository permissionSetsRepository;
    private final VerificationRepository verificationRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final ProfileRepository profileRepository;
    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppJwtTokenUtil jwtTokenUtil;
    private final SESEmailService mailService;
    private final JsonUtility jsonUtility;

    @Transactional(readOnly = true)
    public Profile getCurrentUser() {
        User principal = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return profileRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found - " + principal.getUsername()));
    }

    @Transactional
    public void signup(RegisterRequest request) {
        Profile profile = new Profile();

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setMobile(request.getMobile());
        profile.setAddress(request.getAddress());
        profile.setUsername(request.getUsername());
        profile.setEmail(request.getEmail());

        String passPhrase = passwordEncoder.encode(request.getPassword());

        profile.setPassword(passPhrase);
        profile.setCreatedOn(Instant.now());
        profile.setApproved(false);
        profileRepository.save(profile);

        Login login = new Login();
        login.setUsername(request.getUsername());
        login.setPassword(request.getPassword());
        login.setPassphrase(passPhrase);
        login.setProfile(profile);

        loginRepository.save(login);

        addPermissions(login, profile);

        String token = generateVerificationToken(profile);
        LOGGER.info("http://auth-api.cloud-interview.in/api/public/auth/accountVerification/" + token);
        mailService.sendEmail(NotificationEmail.builder()
                .subject("Account Activation!!")
                .recipient(profile.getEmail())
                .body(AppUtility.refreshTokenEmailContent("http://auth-api.cloud-interview.in/api/public/auth/accountVerification/"
                        + token, profile.getFirstName())).build());
    }

    private Set<PermissionSets> addPermissions(Login login, Profile profile) {
        Set<PermissionSets> userPermissions = new HashSet<>();
        if (IS_ADMIN_PROFILE) {
            LongStream.range(1, 5).forEach(i -> {
                PermissionSets pSets = new PermissionSets();
                pSets.setLoginId(login.getUserId());
                pSets.setLogin(login);
                pSets.setId(profile.getProfileId());
                pSets.setRoleId(1L);
                pSets.setPermId(i);
                permissionSetsRepository.save(pSets);
                userPermissions.add(pSets);
            });
        }
        return userPermissions;
    }

    public void verityToken(String token) {
        VerificationToken verificationToken = verificationRepository.findByToken(token)
                .orElseThrow(() -> new ApplicationException("Invalid Token!"));

        fetchUserAndEnable(verificationToken);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Profile profile = profileRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(new Supplier<RuntimeException>() {
                    @Override
                    public RuntimeException get() {
                        return new ApplicationException("No user found with name - " + loginRequest.getUsername());
                    }
                });

        if (profile.getApproved()) {
            try {


                Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authenticate);
                String token = jwtTokenUtil.generateToken(authenticate);

                return AuthenticationResponse.builder()
                        .authenticationToken(token)
                        .username(loginRequest.getUsername())
                        .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                        .expiresAt(Instant.now().plusMillis(JWT_TOKEN_VALIDITY * 1000))
                        .build();
            } catch (BadCredentialsException ex) {
                throw new UserAuthenticationException(ex.getMessage());
            }
        } else {
            throw new ApplicationException("Please verify your account!");
        }
    }


    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        refreshTokenService.validateRefreshToken(request.getRefreshToken());
        String tokenAfterRefresh = jwtTokenUtil.generateTokenWithUsername(request.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(tokenAfterRefresh)
                .refreshToken(request.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(JWT_TOKEN_VALIDITY * 1000))
                .build();
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String userEmail = verificationToken.getProfile().getEmail();
        Profile profile = profileRepository.findByEmail(userEmail)
                .orElseThrow(new Supplier<RuntimeException>() {
                    @Override
                    public RuntimeException get() {
                        return new ApplicationException("No user found with name - " +
                                verificationToken.getProfile().getFirstName());
                    }
                });

        profile.setApproved(true);
        profileRepository.save(profile);
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }

    public JWKSet getKey() {
        try {
            return jwtTokenUtil.getPublicKey();
        } catch (JOSEException joseException) {
            throw new ApplicationException(joseException.getMessage());
        }
    }

    private String generateVerificationToken(Profile profile) {
        String verificationToken = randomUUID().toString();

        VerificationToken token = new VerificationToken();
        token.setToken(verificationToken);
        token.setProfile(profile);
        token.setExpiryDate(Instant.now().plus(10, ChronoUnit.MINUTES));

        verificationRepository.save(token);
        return verificationToken;
    }
}
