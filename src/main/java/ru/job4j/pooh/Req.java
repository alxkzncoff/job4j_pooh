package ru.job4j.pooh;

/**
 * Класс для парсинга входяще запроса.
 * @author Aleksandr Kuznetsov.
 * @version 1.0
 */

public class Req {

    private final String httpRequestType;
    private final String poohMode;
    private final String sourceName;
    private final String param;

    public Req(String httpRequestType, String poohMode, String sourceName, String param) {
        this.httpRequestType = httpRequestType;
        this.poohMode = poohMode;
        this.sourceName = sourceName;
        this.param = param;
    }

    public static Req of(String content) {
        String ls = System.lineSeparator();
        String httpRequestType = content.split(ls)[0].split(" ")[0];
        String poohMode = content.split(ls)[0].split("/")[1];
        String sourceName = content.split(ls)[0].split("/")[2].split(" ")[0];
        String param = "";
        if ("POST".equals(httpRequestType)) {
            param = content.split(ls)[7];
        } else if ("GET".equals(httpRequestType) && "topic".equals(poohMode)) {
            param = content.split(ls)[0].split(" ")[1].split("/")[3];
        }
        return new Req(httpRequestType, poohMode, sourceName, param);
    }

    public String httpRequestType() {
        return httpRequestType;
    }

    public String getPoohMode() {
        return poohMode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getParam() {
        return param;
    }
}
