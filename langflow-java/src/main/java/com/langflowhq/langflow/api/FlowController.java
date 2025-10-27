package com.langflowhq.langflow.api;

import com.langflowhq.langflow.domain.Flow;
import com.langflowhq.langflow.executor.FlowExecutor;
import com.langflowhq.langflow.service.FlowService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/flows")
public class FlowController {

    private final FlowService flowService;
    private final FlowExecutor flowExecutor;

    public FlowController(FlowService flowService, FlowExecutor flowExecutor) {
        this.flowService = flowService;
        this.flowExecutor = flowExecutor;
    }

    @PostMapping
    public ResponseEntity<Flow> create(@Valid @RequestBody Flow flow) {
        Flow saved = flowService.create(flow);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Flow>> list() {
        return ResponseEntity.ok(flowService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flow> get(@PathVariable String id) {
        Optional<Flow> flow = flowService.get(id);
        return flow.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flow> update(@PathVariable String id, @Valid @RequestBody Flow flow) {
        Optional<Flow> updated = flowService.update(id, flow);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean removed = flowService.delete(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/run")
    public ResponseEntity<Map<String, Object>> run(@PathVariable String id, @RequestBody(required = false) Map<String, Object> inputs) {
        Optional<Flow> flow = flowService.get(id);
        if (flow.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> result = flowExecutor.execute(flow.get(), inputs == null ? Map.of() : inputs);
        return ResponseEntity.ok(result);
    }
}