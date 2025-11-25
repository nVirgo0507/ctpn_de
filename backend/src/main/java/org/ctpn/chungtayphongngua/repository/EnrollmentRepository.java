package org.ctpn.chungtayphongngua.repository;

import org.ctpn.chungtayphongngua.entity.Enrollment;
import org.ctpn.chungtayphongngua.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    long countByUserAndStatus(User user, String status);

    List<Enrollment> findByUserAndStatus(User user, String status);
}
