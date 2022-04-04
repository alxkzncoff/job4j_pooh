package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {

    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        return switch (req.httpRequestType()) {
            case "POST" -> {
                queue.putIfAbsent(req.getSourceName(), new ConcurrentLinkedQueue<>());
                queue.get(req.getSourceName()).add(req.getParam());
                yield new Resp("", "204");
            }
            case "GET" -> {
                if (queue.isEmpty()) {
                    yield new Resp("", "204");
                } else {
                    yield new Resp(queue.getOrDefault(
                            req.getSourceName(), new ConcurrentLinkedQueue<>()).poll(),
                            "200");
                }
            }
            default -> new Resp("", "501");
        };
    }
}
