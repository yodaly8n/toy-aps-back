package org.example.toyapsback.repository;

import org.example.toyapsback.entity.ScenarioJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScenarioJobRepository extends JpaRepository<ScenarioJob,Long> {
}
