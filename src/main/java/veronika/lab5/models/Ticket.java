package veronika.lab5.models;


import veronika.lab5.parsing.annotations.*;

import java.time.LocalDateTime;

public class Ticket implements Comparable<Ticket> {
    @IgnoreInput
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    @NoNull
    @DescriptionAndValidate(name = "имя", validation = "непустая строка")
    private String name; //Поле не может быть null, Строка не может быть пустой
    @NoNull
    @DescriptionAndValidate(name = "координаты")
    private Coordinates coordinates; //Поле не может быть null
    @IgnoreInput
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    @DescriptionAndValidate(name = "цена", validation = "значение > 0")
    @BiggerThan(value = 0)
    private float price; //Значение поля должно быть больше 0

    @DescriptionAndValidate(name = "комментарий", validation = "значение может отсутствовать, длина комментария <= 643")
    @LengthBiggerThan(643)
    private String comment; //Длина строки не должна быть больше 643, Поле может быть null
    @NoNull
    @DescriptionAndValidate(name = "тип")
    private TicketType type; //Поле не может быть null
    @NoNull
    @DescriptionAndValidate(name = "событие")
    private Event event; //Поле не может быть null

    public Ticket(){

    }

    public Ticket(String name, Coordinates coordinates, float price, String comment, TicketType type, Event event) {
        this.name = name;
        this.coordinates = coordinates;
        this.price = price;
        this.comment = comment;
        this.type = type;
        this.event = event;
    }


    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", price=" + price +
                ", comment='" + comment + '\'' +
                ", type=" + type +
                ", event=" + event +
                '}';
    }

    @Override
    public int compareTo(Ticket o) {
        return name.compareTo(o.getName());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Event getEvent() {
        return event;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}

