package com.dd.auth.api.service;

import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.dd.auth.api.entity.Login;
import com.dd.auth.api.entity.PasswordResetToken;
import com.dd.auth.api.entity.Profile;
import com.dd.auth.api.exception.ApplicationException;
import com.dd.auth.api.exception.NotFoundException;
import com.dd.auth.api.model.NotificationEmail;
import com.dd.auth.api.model.dto.ForgotPasswordRequest;
import com.dd.auth.api.model.dto.ResetPasswordRequest;
import com.dd.auth.api.repository.LoginRepository;
import com.dd.auth.api.repository.PasswordResetTokenRepository;
import com.dd.auth.api.repository.ProfileRepository;
import com.dd.auth.api.util.AppUtility;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.dd.auth.api.util.AppUtility.API_AUTH_ROOT_URI;
import static com.dd.auth.api.util.AppUtility.ENDPOINT_URL;
import static org.slf4j.LoggerFactory.getLogger;

@AllArgsConstructor
@Service
@Transactional
public class PasswordPolicyService {

    private static final Logger LOGGER = getLogger(PasswordPolicyService.class);

    private final ProfileRepository profileRepository;
    private final LoginRepository loginRepository;
    private final SESEmailService sesEmailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public void updateResetPasswordToken(ForgotPasswordRequest forgotPasswordRequest)
            throws UserNotFoundException {
        String email = forgotPasswordRequest.getEmail();

        if (!AppUtility.emailValidator(email)) {
            throw new ValidationException("Please enter a valid email address");
        }

        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("This email is not registered in our website"));

        try {
            PasswordResetToken token = new PasswordResetToken();
            token.setProfile(profile);
            token.setToken(UUID.randomUUID().toString());
            token.setExpirationDate(LocalDateTime.now().plusMinutes(30));
            token = passwordResetTokenRepository.save(token);

            String resetPasswordLink = ENDPOINT_URL + API_AUTH_ROOT_URI
                    + "/auth/public/reset_password?token="
                    + token.getToken();
            NotificationEmail notificationEmail = NotificationEmail.builder()
                    .subject("Forgot password link")
                    .recipient(email)
                    .body(AppUtility.resetPasswordEmailContent(resetPasswordLink, profile.getFirstName()))
                    .build();

            sesEmailService.sendEmail(notificationEmail);
        } catch (UserNotFoundException ex) {
            LOGGER.error("Exception while sending processing ForgotPasswordRequest message" + ex.getMessage());
            throw new ApplicationException("Exception while sending password reset link!");
        }
    }

    public void getByResetPasswordToken(ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new NotFoundException("You've encountered some errors while trying to reset your password."));

        updatePassword(token.getProfile(), request.getPassword());
        passwordResetTokenRepository.delete(token);
    }

    @Modifying
    public void updatePassword(Profile profile, String newPassword) {
        LOGGER.debug("Updating new password" + newPassword);
        String encodedPassword = passwordEncoder.encode(newPassword);
        profile.setPassword(encodedPassword);
        profileRepository.save(profile);

        Login login = profile.getLogin();
        login.setPassword(newPassword);
        login.setPassphrase(passwordEncoder.encode(newPassword));
        loginRepository.save(login);
    }
}
