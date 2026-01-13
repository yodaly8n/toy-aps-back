package org.example.toyapsback.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobUpsertResponse {
    int created;
    int updated;
    int deleted;
}
