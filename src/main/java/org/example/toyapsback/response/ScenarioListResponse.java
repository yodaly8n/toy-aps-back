package org.example.toyapsback.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class ScenarioListResponse {

    List<?> scenarios;


    @Builder
    @Getter
    public static class Item {
        String id;
        String description;
        String status;
        int jobCount;
    }


}
