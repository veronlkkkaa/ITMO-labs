package veronika.lab5.commands;

import veronika.lab5.State;
import veronika.lab5.models.Ticket;

import java.util.Collection;
import java.util.Scanner;

//удалить элемент коллекции по его id
public class RemoveByIdCommand implements Command {
    private int id;

    @Override
    public Result execute(State state) {
        Ticket prevValue = null;
        Collection<Ticket> collection = state.getCollection();
        if (collection.isEmpty()) {
            return Result.error("Коллекция пустая, вам не с чем сравнивать элементы по их id");
        }
        for (Ticket s :
                collection) {
            if (s.getId() == id) {
                prevValue = s;
            }
        }
        if (prevValue == null) {
            return Result.error("Элемента с таким индексом не существует.");
        }
        collection.remove(prevValue);;
        return Result.success("Вы удалили элемент с индексом: " + this.id);
    }

    @Override
    public void parse(Scanner scanner) {
        id = scanner.nextInt();
    }
}
