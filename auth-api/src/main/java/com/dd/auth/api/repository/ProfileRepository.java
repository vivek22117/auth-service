package com.dd.auth.api.repository;

import com.dd.auth.api.entity.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ProfileRepository extends CrudRepository<Profile, Long> {
	
	public List<Profile> findByNameIgnoreCase(String name);
	public List<Profile> findByUsernameAndPassword(String username, String password);
	public List<Profile> findByUsernameStartingWith(String prefix);
	public List<Profile> findByApprovedTrue();
	
	@Query(value = "select * from signup", nativeQuery = true)
	public List<Profile> findProfiles();
	
	@Query(value = "from Profile")
	public Stream<Profile> findStreamProfiles();
	
	@Query(value = "from Profile p WHERE p.profileId = :id")
	public Profile findProfile(@Param("id") Long id);
	
	
	
	@Query(value = "from Profile p WHERE p.username = ?1 AND p.password = ?2")
	public List<Profile> findProfiles(String username, String password);

	@Query(value = "Select * from signup WHERE username LIKE ?1 AND password LIKE ?2", nativeQuery = true)
	public List<Profile> findNativeProfiles(String username, String password);
	
	public List<Profile> findByFirstName(String name);

    Optional<Profile> findByUsername(String username);

	Optional<Profile> findByEmail(String username);

	Optional<Profile> findByResetToken(String resetToken);
}
