package veronika.lab5.commands;

import veronika.lab5.State;
import veronika.lab5.models.Ticket;
import veronika.lab5.parsing.InterractiveObjectParser;
//обновить значение элемента колллекции, id которого равен заданному
import java.util.Collection;
import java.util.Scanner;

public class UpdateIdCommand implements Command {
    private int id;
    Ticket ticket;
    @Override
    public Result execute(State state) {
        Ticket prevValue = null;
        Collection<Ticket> collection = state.getCollection();
        for (Ticket s :
                collection) {
            if (id == s.getId()) {
                prevValue = s;

            }
        }
        if (prevValue == null) {
            return Result.success("Вы пытаетесь обновить то, чего нет. Так делать нельзя!");
        }
        collection.remove(prevValue);
        ticket.setId(id);
//        ticket.setCreationDate(prevValue.getCreationDate());
        collection.add(ticket);
//        state.notifyUpdate();
        return Result.success("Элемент успешно обновлен. ");
    }


    @Override
    public void parse(Scanner scanner) {
        id = scanner.nextInt();
        ticket = InterractiveObjectParser.createObj(Ticket.class,scanner);
    }
}
