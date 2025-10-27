package com.langflowhq.langflow.executor;

import com.langflowhq.langflow.domain.Edge;
import com.langflowhq.langflow.domain.Flow;
import com.langflowhq.langflow.domain.Node;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FlowExecutor {

    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;

    public FlowExecutor(ObjectProvider<ChatClient.Builder> chatClientBuilderProvider) {
        this.chatClientBuilderProvider = chatClientBuilderProvider;
    }

    public Map<String, Object> execute(Flow flow, Map<String, Object> inputs) {
        Map<String, Node> idToNode = flow.nodes().stream().collect(Collectors.toMap(Node::id, Function.identity()));
        Map<String, List<String>> adjacency = new HashMap<>();
        Map<String, Integer> indegree = new HashMap<>();
        for (Node node : flow.nodes()) {
            adjacency.put(node.id(), new ArrayList<>());
            indegree.put(node.id(), 0);
        }
        for (Edge edge : flow.edges()) {
            adjacency.get(edge.source()).add(edge.target());
            indegree.put(edge.target(), indegree.get(edge.target()) + 1);
        }

        Queue<String> queue = new ArrayDeque<>();
        for (Map.Entry<String, Integer> e : indegree.entrySet()) {
            if (e.getValue() == 0) queue.add(e.getKey());
        }

        Map<String, Object> context = new HashMap<>(inputs);
        Map<String, Object> outputs = new LinkedHashMap<>();

        while (!queue.isEmpty()) {
            String nodeId = queue.poll();
            Node node = idToNode.get(nodeId);
            Object result = runNode(node, context);
            outputs.put(nodeId, result);
            context.put(nodeId, result);
            for (String next : adjacency.getOrDefault(nodeId, List.of())) {
                indegree.put(next, indegree.get(next) - 1);
                if (indegree.get(next) == 0) queue.add(next);
            }
        }
        return outputs;
    }

    private Object runNode(Node node, Map<String, Object> context) {
        String type = node.type();
        Map<String, Object> cfg = node.config() == null ? Map.of() : node.config();
        switch (type) {
            case "input" -> {
                return cfg.getOrDefault("value", context.getOrDefault("input", ""));
            }
            case "concat" -> {
                Object a = resolveRef(cfg.get("a"), context);
                Object b = resolveRef(cfg.get("b"), context);
                return String.valueOf(a) + String.valueOf(b);
            }
            case "llm" -> {
                Object promptObj = resolveRef(cfg.get("prompt"), context);
                String prompt = String.valueOf(promptObj);
                ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
                if (builder == null) {
                    throw new IllegalStateException("ChatClient is not configured. Please set OPENAI_API_KEY.");
                }
                ChatClient chatClient = builder.defaultAdvisors(new SimpleLoggerAdvisor()).build();
                return chatClient.prompt().user(prompt).call().content();
            }
            default -> {
                return cfg.getOrDefault("value", null);
            }
        }
    }

    private Object resolveRef(Object raw, Map<String, Object> context) {
        if (raw == null) return null;
        if (raw instanceof String s && s.startsWith("$") && s.length() > 1) {
            return context.getOrDefault(s.substring(1), null);
        }
        return raw;
    }
}