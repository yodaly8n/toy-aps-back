package org.example.toyapsback.response;

import lombok.Builder;
import lombok.Getter;
import org.example.toyapsback.entity.Job;

import java.util.List;

@Getter
@Builder
public class JobListResponse {
    List<Job> jobs;
}
