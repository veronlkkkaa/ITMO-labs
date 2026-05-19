package veronika.lab5.commands;

import veronika.lab5.State;

import java.util.Scanner;

//вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
public class InfoCommand implements Command {
    @Override
    public Result execute(State state) {
        return Result.success("Информация о коллекции. " + "Тип коллекции: " + state.getCollection().getClass() + " Дата инициализации: " + state.getInitializationDate() +
                " Количество элементов в коллекции: " + state.getCollection().size());
    }

    @Override
    public void parse(Scanner scanner) {

    }
}
