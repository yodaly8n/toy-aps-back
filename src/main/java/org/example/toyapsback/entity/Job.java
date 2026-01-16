package org.example.toyapsback.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    String id;

    String name;
    String description;
    boolean active;

    @OneToMany(mappedBy = "job")
    List<Task> tasks;

}
