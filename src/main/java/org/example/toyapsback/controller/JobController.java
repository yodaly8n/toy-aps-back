package org.example.toyapsback.controller;


import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.example.toyapsback.entity.Job;
import org.example.toyapsback.repository.JobRepository;
import org.example.toyapsback.request.JobBulkUpsertRequest;
import org.example.toyapsback.response.JobListResponse;
import org.example.toyapsback.response.JobParseResponse;
import org.example.toyapsback.response.JobUpsertResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@CrossOrigin
public class JobController {

    private final JobRepository jobRepository;


    @PostMapping("/parse/xls")
    public ResponseEntity<?> handlePostParseXls(@RequestParam MultipartFile file) throws IOException {
        if (file.isEmpty())
            return ResponseEntity.badRequest().build();

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
//            System.out.println(sheet.getFirstRowNum());
//            System.out.println(sheet.getLastRowNum());

        Iterator<Row> iterator = sheet.rowIterator();
        Row header = iterator.next();
        DataFormatter formatter = new DataFormatter();
        List<JobParseResponse.Item> items = new ArrayList<>();
        while (iterator.hasNext()) {
            Row row = iterator.next();

            JobParseResponse.Item one =
                    JobParseResponse.Item.builder()
                            .id(formatter.formatCellValue(row.getCell(0)))
                            .name(formatter.formatCellValue(row.getCell(1)))
                            .description(formatter.formatCellValue(row.getCell(2)))
                            .active(Boolean.parseBoolean(formatter.formatCellValue(row.getCell(3))))
                            .build();
            items.add(one);
        }
        JobParseResponse response = JobParseResponse.builder().items(items).build();

        return ResponseEntity.ok(response);
    }


    @PostMapping
    public ResponseEntity<?> handlePostJobs(@RequestBody JobBulkUpsertRequest jobBulkUpsertRequest) {
        List<JobBulkUpsertRequest.JobUpsertItem> items = jobBulkUpsertRequest.getJobs();
        List<String> itemIds = items.stream().map(e -> e.getId()).toList();

        // 요청객체 자체는 비슷하다고 생각했을때, 빼고 넘기면 지워버릴꺼다.
        List<Job> savedJobs = jobRepository.findAll();
        List<Job> notContainsJobs = savedJobs.stream().filter(e -> !itemIds.contains(e.getId())
        ).toList();   // 지울 애들만 남길 생각

        jobRepository.deleteAll(notContainsJobs);

        List<Job> upsertJobs = items.stream().map(e ->
                Job.builder().id(e.getId()).name(e.getName()).description(e.getDescription()).active(e.isActive()).build()
        ).toList();
        jobRepository.saveAll(upsertJobs);

        int deleted = notContainsJobs.size();
        int updated = savedJobs.size() - deleted;
        int created = upsertJobs.size() - updated;

        JobUpsertResponse response = JobUpsertResponse.builder()
                .deleted(deleted).updated(updated).created(created).build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> handleGetJobs() {
        List<Job> jobs = jobRepository.findAll();
        JobListResponse response = JobListResponse.builder().jobs(jobs).build();

        return ResponseEntity.ok(response);
    }
}
