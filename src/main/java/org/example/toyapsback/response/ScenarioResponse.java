package org.example.toyapsback.response;

import lombok.Builder;
import lombok.Getter;
import org.example.toyapsback.entity.Scenario;

@Getter
@Builder
public class ScenarioResponse {
    Scenario scenario;
}
