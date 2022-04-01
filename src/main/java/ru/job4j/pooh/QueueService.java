package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {

    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Resp result = null;
        if ("POST".equals(req.httpRequestType())) {
            queue.putIfAbsent(req.getSourceName(), new ConcurrentLinkedQueue<>());
            queue.get(req.getSourceName()).add(req.getParam());
            result = new Resp("", "200");
        }
        if ("GET".equals(req.httpRequestType())) {
            if (queue.isEmpty()) {
                result = new Resp("", "204");
            } else {
                result = new Resp(queue.get(req.getSourceName()).poll(), "200");
            }
        }
        return result;
    }
}
