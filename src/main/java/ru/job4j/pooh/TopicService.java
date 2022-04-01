package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {
    private final ConcurrentHashMap<String,
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topics = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Resp result = null;
        if ("GET".equals(req.httpRequestType())) {
            topics.putIfAbsent(req.getSourceName(), new ConcurrentHashMap<>());
            topics.get(req.getSourceName()).putIfAbsent(req.getParam(), new ConcurrentLinkedQueue<>());
            String text = topics.get(req.getSourceName()).get(req.getParam()).poll();
            result = text == null ? new Resp("", "204") : new Resp(text, "200");
        }
        if ("POST".equals(req.httpRequestType())) {
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> topic = topics.get(req.getSourceName());
            for (ConcurrentLinkedQueue<String> queue: topic.values()) {
                queue.add(req.getParam());
            }
        }
        return result;
    }
}
