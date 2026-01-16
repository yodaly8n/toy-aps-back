package org.example.toyapsback.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class ScenarioCreateRequest {
    String description;
    LocalDateTime scheduleAt;
    List<String> jobIds;
}
