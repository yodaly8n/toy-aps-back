package org.example.toyapsback.controller;

import lombok.RequiredArgsConstructor;
import org.example.toyapsback.entity.Tool;
import org.example.toyapsback.repository.ToolRepository;
import org.example.toyapsback.request.ToolBulkUpsertRequest;
import org.example.toyapsback.response.ToolListResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tools")
@CrossOrigin
@RequiredArgsConstructor
public class ToolController {
    private final ToolRepository toolRepository;

    @GetMapping
    public ResponseEntity<?> handleGetTools() {
        List<Tool> tools = toolRepository.findAll();
        long total = toolRepository.count();

        ToolListResponse response = ToolListResponse.builder()
                .tools(tools)
                .total(total).build();


        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping
    public ResponseEntity<?> handlePostTools(@RequestBody ToolBulkUpsertRequest toolBulkUpsertRequest) {

//      List<Tool> savedTools = toolRepository.findAll();

        List<Tool> tools = toolBulkUpsertRequest.getTools().stream().map(
                one -> Tool.builder().id(one.getId()).name(one.getName()).description(one.getDescription()).build()
        ).toList();
        toolRepository.saveAll(tools);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
