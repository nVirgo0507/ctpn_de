package org.ctpn.chungtayphongngua.repository;

import org.ctpn.chungtayphongngua.entity.Assessment;
import org.ctpn.chungtayphongngua.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByUserOrderByCompletedAtDesc(User user);
    Optional<Assessment> findTopByUserOrderByCompletedAtDesc(User user);
}
