package com.dd.auth.api.repository;

import com.dd.auth.api.model.PermissionSets;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionSetsRepository extends CrudRepository<PermissionSets, Long> {
	
	@Query("SELECT ps FROM PermissionSets ps WHERE ps.loginId = ?1")
	public List<PermissionSets> findAllByLoginId(Long id);
}
