package com.dd.auth.api.repository;

import com.dd.auth.api.model.VerificationToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerificationRepository extends CrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);
}
