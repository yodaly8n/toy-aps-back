package org.example.toyapsback.response;


import lombok.Builder;
import lombok.Getter;
import org.example.toyapsback.entity.Task;

import java.util.List;

@Getter
@Builder
public class TaskListResponse {
    private List<Task> tasks;
}
