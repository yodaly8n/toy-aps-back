package org.example.toyapsback.repository;

import org.example.toyapsback.entity.ScenarioSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScenarioScheduleRepository extends JpaRepository<ScenarioSchedule,Long> {
}
