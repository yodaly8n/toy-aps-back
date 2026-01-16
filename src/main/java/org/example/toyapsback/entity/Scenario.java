package org.example.toyapsback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scenario {
    @Id
    String id;

    String description;
    String status;
    LocalDateTime scheduleAt;

    Integer workTime;

    @OneToMany(mappedBy = "scenario")
    List<ScenarioJob> scenarioJobs;


    @PrePersist
    public void prePersist() {
        this.id =   UUID.randomUUID().toString().split("-")[0];
        this.status = "READY";
    }
}
