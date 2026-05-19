package veronika.lab5.commands;

import veronika.lab5.State;
import veronika.lab5.models.Event;
import veronika.lab5.models.Ticket;
import veronika.lab5.parsing.InterractiveObjectParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

//вывести элементы, значение поля event которых больше заданного
public class FilterGreaterThanEventCommand implements Command {
    Event event;

    @Override
    public Result execute(State state) {
        Collection<Ticket> ticketCollection = state.getCollection();
        List<Ticket> ticketList = new ArrayList<>(ticketCollection);
        if (ticketList.isEmpty()) {
            return Result.error("Сорри, но вам не с чем сравнить введенное вами поле event, так как коллекция пустая. ");
        }
        String out = "";
        for (Ticket ticket : ticketList
        ) {
            if (ticket.getEvent().compareTo(event) > 0) {
                out += InterractiveObjectParser.printOnbject(ticket);
            }
        }
        return Result.success("Элементы, значение поля event которых больше заданного: " + "\n" + out);
    }

    @Override
    public void parse(Scanner scanner) {
        event = InterractiveObjectParser.createObj(Event.class, scanner);
    }
}
