package org.example.toyapsback.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TaskBulkUpsertRequest {
    @Setter
    @Getter
    public static class Item {
        String id;
        String jobId;
        int seq;
        String name;
        String description;
        String toolId;
        int duration;
    }

    private List<Item> tasks;
}
