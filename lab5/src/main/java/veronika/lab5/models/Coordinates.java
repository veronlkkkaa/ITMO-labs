package veronika.lab5.models;


import veronika.lab5.parsing.annotations.BiggerThan;
import veronika.lab5.parsing.annotations.DescriptionAndValidate;
import veronika.lab5.parsing.annotations.NoNull;
import veronika.lab5.parsing.annotations.SmallerThan;

public class Coordinates {
    @NoNull
    @DescriptionAndValidate(name = "x", validation = "значение > -493")
    @BiggerThan(value = -493)
    private Double x; //Значение поля должно быть больше -493, Поле не может быть null
    @DescriptionAndValidate(name = "y", validation = "значение <= 797")
    @SmallerThan(value = 797)
    private int y; //Максимальное значение поля: 797

//    пустой конструктор нужен для рефлексии. для создания объекта.
    public Coordinates(){
    }
//    этот конструктор нужен для CSV парсера. Там создается объект
    public Coordinates(Double x, int y) {
        this.x = x;
        this.y = y;
    }


    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
