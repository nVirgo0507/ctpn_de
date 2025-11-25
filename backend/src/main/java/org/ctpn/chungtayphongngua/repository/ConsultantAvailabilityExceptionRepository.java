package org.ctpn.chungtayphongngua.repository;

import org.ctpn.chungtayphongngua.entity.ConsultantAvailabilityException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface ConsultantAvailabilityExceptionRepository extends JpaRepository<ConsultantAvailabilityException, Long> {

    @Query("SELECT e FROM ConsultantAvailabilityException e WHERE e.user.userId = :consultantId AND e.exceptionDate = :date AND e.startTime <= :time AND e.endTime > :time")
    Optional<ConsultantAvailabilityException> findExceptionAtDateTime(@Param("consultantId") Long consultantId, @Param("date") LocalDate date, @Param("time") LocalTime time);
}
