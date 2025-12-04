package org.ctpn.chungtayphongngua.repository;

import org.ctpn.chungtayphongngua.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUserUserId(Long userId);

    Optional<Enrollment> findByUserUserIdAndCourseCourseId(Long userId, Long courseId);

    List<Enrollment> findByUserUserIdAndStatus(Long userId, String status);
}
