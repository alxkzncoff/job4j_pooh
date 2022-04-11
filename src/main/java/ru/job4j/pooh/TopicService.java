package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Отправитель посылает запрос на добавление данных с указанием топика (weather)
 * и значением параметра (temperature=18).
 * Сообщение помещается в конец каждой индивидуальной очереди получателей.
 * Если топика нет в сервисе, то данные игнорируются.
 *
 * Получатель посылает запрос на получение данных с указанием топика.
 * Если топик отсутствует, то создается новый. А если топик присутствует,
 * то сообщение забирается из начала индивидуальной очереди получателя и удаляется.
 *
 * Когда получатель впервые получает данные из топика – для него создается индивидуальная пустая очередь.
 * Все последующие сообщения от отправителей с данными для этого топика помещаются в эту очередь тоже.
 * @author Aleksandr Kuznetsov.
 * @version 1.0
 */

public class TopicService implements Service {
    private final ConcurrentHashMap<String,
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topics = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Resp result = new Resp("", "501");
        if ("POST".equals(req.httpRequestType())) {
            result = post(req.getSourceName(), req.getParam());
        } else if ("GET".equals(req.httpRequestType())) {
            result = get(req.getSourceName(), req.getParam());
        }
        return result;
    }

    /**
     * Метод обрабатывает POST запросы.
     * @param sourceName поле sourceName класса Req.
     * @param param поле param класса Req.
     * @return Resp результат обработки запроса.
     */
    private Resp post(String sourceName, String param) {
        ConcurrentHashMap<String,
                ConcurrentLinkedQueue<String>> topic = topics.getOrDefault(sourceName, new ConcurrentHashMap<>());
        for (ConcurrentLinkedQueue<String> queue : topic.values()) {
            queue.add(param);
        }
        return new Resp("", "204");
    }

    /**
     * Метод обрабаотывает GET запросы.
     * @param sourceName поле sourceName класса Req.
     * @param param поле param класса Req.
     * @return Resp результат обработки запроса.
     */
    private Resp get(String sourceName, String param) {
        topics.putIfAbsent(sourceName, new ConcurrentHashMap<>());
        topics.get(sourceName).putIfAbsent(param, new ConcurrentLinkedQueue<>());
        String text = topics.get(sourceName).get(param).poll();
        return text != null ? new Resp(text, "200") : new Resp("", "204");
    }
}
