package org.example.toyapsback.response;

import lombok.Builder;
import lombok.Getter;
import org.example.toyapsback.entity.Job;

import java.util.List;

@Builder
@Getter
public class JobParseResponse {
    List<Item> items;

    @Builder
    @Getter
    public static class Item {
        private String id;
        private String name;
        private String description;
        private boolean active;
    }
}
