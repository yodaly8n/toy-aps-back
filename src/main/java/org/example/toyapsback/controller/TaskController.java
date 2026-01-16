package org.example.toyapsback.controller;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.example.toyapsback.entity.Job;
import org.example.toyapsback.entity.Task;
import org.example.toyapsback.repository.JobRepository;
import org.example.toyapsback.repository.TaskRepository;
import org.example.toyapsback.repository.ToolRepository;
import org.example.toyapsback.request.JobBulkUpsertRequest;
import org.example.toyapsback.request.TaskBulkUpsertRequest;
import org.example.toyapsback.response.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin
public class TaskController {

    private final TaskRepository taskRepository;
    private final JobRepository jobRepository;
    private final ToolRepository toolRepository;

    @PostMapping("/parse/xls")
    public ResponseEntity<?> handlePostParseXls(@RequestParam MultipartFile file) throws IOException {
        if (file.isEmpty()) return ResponseEntity.badRequest().build();

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
//            System.out.println(sheet.getFirstRowNum());
//            System.out.println(sheet.getLastRowNum());

        Iterator<Row> iterator = sheet.rowIterator();
        Row header = iterator.next();
        DataFormatter formatter = new DataFormatter();
        List<TaskParseResponse.Item> items = new ArrayList<>();
        while (iterator.hasNext()) {
            Row row = iterator.next();

            TaskParseResponse.Item one = TaskParseResponse.Item.builder()
                    .id(formatter.formatCellValue(row.getCell(0)))
                    .jobId(formatter.formatCellValue(row.getCell(1)))
                    .seq(Integer.parseInt(formatter.formatCellValue(row.getCell(2))))
                    .name(formatter.formatCellValue(row.getCell(3)))
                    .description(formatter.formatCellValue(row.getCell(4)))
                    .toolId(formatter.formatCellValue(row.getCell(5)))
                    .duration(Integer.parseInt(formatter.formatCellValue(row.getCell(6))))
                    .build();

            items.add(one);
        }

        TaskParseResponse response = TaskParseResponse.builder().items(items).build();

        return ResponseEntity.ok(response);
    }


    @PostMapping
    public ResponseEntity<?> handlePostTasks(@RequestBody TaskBulkUpsertRequest taskBulkUpsertRequest) {
        List<TaskBulkUpsertRequest.Item> items = taskBulkUpsertRequest.getTasks();
        List<String> itemIds = items.stream().map(e -> e.getId()).toList();

        // 요청객체 자체는 비슷하다고 생각했을때, 빼고 넘기면 지워버릴꺼다.
        List<Task> savedTasks = taskRepository.findAll();
        List<Task> notContains = savedTasks.stream().filter(e -> !itemIds.contains(e.getId())).toList();   // 지울 애들만 남길 생각

        taskRepository.deleteAll(notContains);

        List<Task> upsertTasks = items.stream().map(e -> Task.builder().id(e.getId())
                .job(jobRepository.findById(e.getJobId()).orElseThrow())
                .seq(e.getSeq())
                .name(e.getName())
                .description(e.getDescription())
                .tool(toolRepository.findById(e.getToolId()).orElseGet(() -> null))
                .duration(e.getDuration()).build())
                .toList();
        taskRepository.saveAll(upsertTasks);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> handleGetJobs() {
        List<Task> tasks = taskRepository.findAll();
        TaskListResponse response = TaskListResponse.builder().tasks(tasks).build();

        return ResponseEntity.ok(response);
    }
}
