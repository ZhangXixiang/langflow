package com.langflowhq.langflow.repo;

import com.langflowhq.langflow.domain.Flow;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryFlowRepository {
    private final Map<String, Flow> idToFlow = new ConcurrentHashMap<>();

    public Flow save(Flow flow) {
        String id = flow.id() != null && !flow.id().isBlank() ? flow.id() : UUID.randomUUID().toString();
        Flow toSave = new Flow(id, flow.name(), flow.description(), flow.nodes(), flow.edges());
        idToFlow.put(id, toSave);
        return toSave;
    }

    public Optional<Flow> findById(String id) {
        return Optional.ofNullable(idToFlow.get(id));
    }

    public List<Flow> findAll() {
        return new ArrayList<>(idToFlow.values());
    }

    public boolean deleteById(String id) {
        return idToFlow.remove(id) != null;
    }
}