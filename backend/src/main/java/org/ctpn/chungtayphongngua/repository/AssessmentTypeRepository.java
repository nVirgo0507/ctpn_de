package org.ctpn.chungtayphongngua.repository;

import org.ctpn.chungtayphongngua.entity.AssessmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssessmentTypeRepository extends JpaRepository<AssessmentType, Long> {
    Optional<AssessmentType> findByTypeNameAndAgeGroup(String typeName, String ageGroup);
    boolean existsByTypeNameAndAgeGroup(String typeName, String ageGroup);
}
