package org.ctpn.chungtayphongngua.repository;

import org.ctpn.chungtayphongngua.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
