package veronika.lab5.models;


import veronika.lab5.parsing.annotations.DescriptionAndValidate;
import veronika.lab5.parsing.annotations.IgnoreInput;
import veronika.lab5.parsing.annotations.NoNull;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Event implements Comparable<Event> {
    @IgnoreInput
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    @NoNull
    @DescriptionAndValidate(name = "имя", validation = "непустая строка")
    private String name; //Поле не может быть null, Строка не может быть пустой
    @NoNull
    @DescriptionAndValidate(name = "дата", validation = "в формате: dd.MM.yyyy HH:mm:ss ")
    private java.time.LocalDateTime date; //Поле не может быть null
    @NoNull
    @DescriptionAndValidate(name = "тип мероприятия")
    private EventType eventType; //Поле не может быть null

//    пустой конструктор нужен для рефлексии. для создания объекта.
    public Event(){

    }
    public Event(String name, LocalDateTime date, EventType eventType) {
        this.name = name;
        this.date = date;
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", eventType=" + eventType +
                '}';
    }

    @Override
    public int compareTo(Event o) {
        return name.compareTo(o.getName());
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
