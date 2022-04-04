package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {
    private final ConcurrentHashMap<String,
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topics = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        return switch (req.httpRequestType()) {
            case "POST" -> {
                ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> topic = topics.getOrDefault(req.getSourceName(), new ConcurrentHashMap<>());
                for (ConcurrentLinkedQueue<String> queue: topic.values()) {
                    queue.add(req.getParam());
                }
                yield new Resp("", "204");
            }
            case "GET" -> {
                topics.putIfAbsent(req.getSourceName(), new ConcurrentHashMap<>());
                topics.get(req.getSourceName()).putIfAbsent(req.getParam(), new ConcurrentLinkedQueue<>());
                String text = topics.get(req.getSourceName()).get(req.getParam()).poll();
                yield text == null ? new Resp("", "204") : new Resp(text, "200");
            }
            default -> new Resp("", "501");
        };
    }
}
