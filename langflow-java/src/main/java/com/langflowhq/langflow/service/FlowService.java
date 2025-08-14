package com.langflowhq.langflow.service;

import com.langflowhq.langflow.domain.Flow;
import com.langflowhq.langflow.repo.InMemoryFlowRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlowService {
    private final InMemoryFlowRepository repository;

    public FlowService(InMemoryFlowRepository repository) {
        this.repository = repository;
    }

    public Flow create(Flow flow) {
        return repository.save(flow);
    }

    public List<Flow> list() {
        return repository.findAll();
    }

    public Optional<Flow> get(String id) {
        return repository.findById(id);
    }

    public Optional<Flow> update(String id, Flow flow) {
        if (repository.findById(id).isEmpty()) {
            return Optional.empty();
        }
        Flow withId = new Flow(id, flow.name(), flow.description(), flow.nodes(), flow.edges());
        return Optional.of(repository.save(withId));
    }

    public boolean delete(String id) {
        return repository.deleteById(id);
    }
}