package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Отправитель посылает запрос на добавление данных с указанием очереди (weather)
 * и значением параметра (temperature=18). Сообщение помещается в конец очереди.
 * Если очереди нет в сервисе, то нужно создать новую и поместить в нее сообщение.
 *
 * Получатель посылает запрос на получение данных с указанием очереди.
 * Сообщение забирается из начала очереди и удаляется.
 *
 * Если в очередь приходят несколько получателей, то они поочередно получают сообщения из очереди.
 *
 * Каждое сообщение в очереди может быть получено только одним получателем.
 * @author Aleksandr Kuznetsov.
 * @version 1.0
 */

public class QueueService implements Service {

    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Resp result = new Resp("", "501");
        if ("POST".equals(req.httpRequestType())) {
            result = post(req.getSourceName(), req.getParam());
        } else if ("GET".equals(req.httpRequestType())) {
            result = get(req.getSourceName());
        }
        return result;
    }

    /**
     * Метод обрабаьтывает POST запросы.
     * @param sourceName поле sourceName класса Req.
     * @param param поле param класса Req.
     * @return Resp результат обработки запроса.
     */
    private Resp post(String sourceName, String param) {
        queue.putIfAbsent(sourceName, new ConcurrentLinkedQueue<>());
        queue.get(sourceName).add(param);
        return new Resp("", "200");
    }

    /**
     * Метод обрабаотывает GET запросы.
     * @param sourceName поле sourceName класса Req.
     * @return Resp результат обработки запроса.
     */
    private Resp get(String sourceName) {
        Resp result = new Resp("", "204");
        String text = queue.getOrDefault(sourceName, new ConcurrentLinkedQueue<>()).poll();
        if (text != null) {
            result = new Resp(text, "200");
        }
        return result;
    }
}