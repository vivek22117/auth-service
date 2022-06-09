package com.dd.auth.api.service;

import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.dd.auth.api.entity.Profile;
import com.dd.auth.api.exception.ApplicationException;
import com.dd.auth.api.model.NotificationEmail;
import com.dd.auth.api.model.dto.ForgotPasswordRequest;
import com.dd.auth.api.model.dto.Message;
import com.dd.auth.api.model.dto.ResetPasswordRequest;
import com.dd.auth.api.repository.ProfileRepository;
import com.dd.auth.api.util.AppUtility;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@AllArgsConstructor
@Service
@Transactional
public class PasswordPolicyService {

    private static final Logger LOGGER = getLogger(PasswordPolicyService.class);

    private final ProfileRepository profileRepository;
    private final SESEmailService sesEmailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updateResetPasswordToken(ForgotPasswordRequest forgotPasswordRequest)
            throws UserNotFoundException {
        String email = forgotPasswordRequest.getEmail();
        String token = UUID.randomUUID().toString();
        try {
            String resetPasswordLink = AppUtility.ENDPOINT_URL + AppUtility.API_AUTH_ROOT_URI + "/reset-password?token=" + token;

            NotificationEmail notificationEmail = NotificationEmail.builder()
                    .subject("Forgot password link")
                    .recipient(email)
                    .body(resetPasswordLink)
                    .build();
            sesEmailService.sendEmail(notificationEmail);
            Profile profile = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new ApplicationException("Invalid email id"));
            profile.setResetToken(token);
            profileRepository.save(profile);
        } catch (UserNotFoundException ex) {
            LOGGER.error("Exception while sending processing ForgotPasswordRequest message" + ex.getMessage());
            throw new ApplicationException("Exception while sending password reset link!");
        }
    }

    public void getByResetPasswordToken(ResetPasswordRequest request) {
        String token = request.getToken();
        String password = request.getPassword();
        Profile profile = profileRepository.findByResetToken(token)
                .orElseThrow(() -> new ApplicationException("You've encountered some errors while trying to reset your password."));

        updatePassword(profile, password);
    }

    public void updatePassword(Profile profile, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        profile.setPassword(encodedPassword);
        profile.setResetToken(null);
        profileRepository.save(profile);
    }
}
