package org.example.toyapsback.controller;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.functions.T;
import org.example.toyapsback.dto.SolveApiResult;
import org.example.toyapsback.entity.Scenario;
import org.example.toyapsback.entity.ScenarioJob;
import org.example.toyapsback.entity.ScenarioSchedule;
import org.example.toyapsback.repository.*;
import org.example.toyapsback.request.ScenarioCreateRequest;
import org.example.toyapsback.response.ScenarioListResponse;
import org.example.toyapsback.response.ScenarioResponse;
import org.example.toyapsback.response.ScenarioScheduleListResponse;
import org.springframework.boot.ssl.NoSuchSslBundleException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
@CrossOrigin
public class ScenarioController {

    private final ScenarioRepository scenarioRepository;
    private final JobRepository jobRepository;
    private final ScenarioJobRepository scenarioJobRepository;
    private final TaskRepository taskRepository;
    private final ScenarioScheduleRepository scenarioScheduleRepository;

    @PostMapping
    public ResponseEntity<?> handlePostScenarios(@RequestBody ScenarioCreateRequest scenarioCreateRequest) {

        Scenario scenario = Scenario.builder()
                .description(scenarioCreateRequest.getDescription())
                .scheduleAt(scenarioCreateRequest.getScheduleAt())
                .build();

        scenarioRepository.save(scenario);

        List<ScenarioJob> scenarioJobList =
                scenarioCreateRequest.getJobIds().stream().map(one -> ScenarioJob.builder()
                        .scenario(scenario)
                        .job(jobRepository.findById(one).orElseThrow())
                        .build()).toList();
        scenarioJobRepository.saveAll(scenarioJobList);
        Map<String, Object> response = Map.of("created", scenario);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{scenarioId}/simulate")
    public ResponseEntity<?> handlePostScenarioSimulate(@PathVariable String scenarioId) {

        Scenario scenario = scenarioRepository.findById(scenarioId).orElseThrow();
        RestClient restClient = RestClient.create();

        SolveApiResult result =
                restClient.post().uri("http://127.0.0.1:5000/api/solve").body(scenario).retrieve().body(SolveApiResult.class);
        // 파이썬에게 맡기고, 받아온 응답을 잘 파싱해서, DB 에 insert
        // ....
        scenario.setStatus(result.getStatus());
        scenario.setWorkTime(result.getMakespan());
        scenarioRepository.save(scenario);
        // 그다음에, schedule 을 등록하면 된다.
        List<ScenarioSchedule> schedules = result.getSchedules().stream().map(one -> {
            return ScenarioSchedule.builder().scenario(scenario)
                    .task(taskRepository.findById(one.getTaskId()).orElseThrow())
                    .startAt(scenario.getScheduleAt().plusHours(one.getStart()))
                    .endAt(scenario.getScheduleAt().plusHours(one.getEnd()))
                    .build();
        }).toList();

        scenarioScheduleRepository.saveAll(schedules);

        Map<String, Object> response = Map.of("status", result.getStatus());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{scenarioId}/json")
    public ResponseEntity<?> handleGetScenarioJson(@PathVariable String scenarioId) {
        Scenario scenario = scenarioRepository.findById(scenarioId).orElseThrow();
        return ResponseEntity.ok(scenario);
    }


    @GetMapping
    public ResponseEntity<?> handleGetAllScenarios() {
        List<Scenario> scenarios = scenarioRepository.findAll();


        List<ScenarioListResponse.Item> items =
                scenarios.stream().map(one -> ScenarioListResponse.Item.builder()
                        .id(one.getId())
                        .description(one.getDescription())
                        .status(one.getStatus())
                        .jobCount(one.getScenarioJobs().size())
                        .build()).toList();

        // Map<String, Object> response = Map.of("scenarios", scenarios);
        ScenarioListResponse response = ScenarioListResponse.builder().scenarios(items).build();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{scenarioId}/simulate")
    public ResponseEntity<?> handleGetScenarioSimulate(@PathVariable String scenarioId) throws IllegalAccessException {
        Scenario scenario = scenarioRepository.findById(scenarioId).orElseThrow(() -> new NoSuchElementException());
        if (scenario.getStatus().equals("READY")) {
            throw new IllegalAccessException();
        }

        List<ScenarioSchedule> allSchedules = scenarioScheduleRepository.findAll();
        List<ScenarioSchedule> selectedSchedules =
                allSchedules.stream().filter(one -> one.getScenario().equals(scenario)).toList();
        List<ScenarioScheduleListResponse.Item> items =
        selectedSchedules.stream().map(ScenarioScheduleListResponse.Item::fromEntity).toList();

        Map<String, Object> response = Map.of("status", scenario.getStatus(), "schedules", items);

        return ResponseEntity.ok(response);


    }

}
