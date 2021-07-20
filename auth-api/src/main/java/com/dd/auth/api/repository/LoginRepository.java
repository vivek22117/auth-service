package com.dd.auth.api.repository;

import com.dd.auth.api.entity.Login;
import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public interface LoginRepository extends CrudRepository<Login, Long> {

	Optional<Login> findByUsername(String username);
	@Async
	CompletableFuture<Login> findOneByUsername(String username);
	@Async
	ListenableFuture<Login> findOneByUserId(Long id);
}
