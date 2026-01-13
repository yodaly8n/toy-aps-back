package org.example.toyapsback.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.toyapsback.entity.Tool;

import java.util.List;

@Setter
@Getter
@Builder
public class ToolListResponse {
    private long total;
    private List<Tool> tools;
}
