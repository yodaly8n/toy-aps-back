package org.example.toyapsback.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class TaskParseResponse {
    List<Item> items;

    @Builder
    @Getter
    public static class Item {
        private String id;
        private String jobId;
        private int seq;
        private String name;
        private String description;
        private String toolId;
        private int duration;
    }
}
