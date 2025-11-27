package org.ctpn.chungtayphongngua.repository;

import org.ctpn.chungtayphongngua.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.verificationToken = :token AND u.verificationTokenExpiresAt > :now")
    Optional<User> findByValidVerificationToken(@Param("token") String token, @Param("now") LocalDateTime now);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userProfile up LEFT JOIN FETCH up.specializations JOIN u.userRoles ur JOIN ur.role r WHERE r.roleName = 'CONSULTANT'")
    List<User> findConsultantsWithProfile();
}
