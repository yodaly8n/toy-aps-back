package org.example.toyapsback.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    private String id;

    @ManyToOne
    @JsonIgnore
    Job job;

    int seq;
    String name;
    String description;

    @ManyToOne
    Tool tool;
    int duration;
}
