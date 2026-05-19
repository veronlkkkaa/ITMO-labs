package veronika.lab5.commands;

import veronika.lab5.State;
import veronika.lab5.models.Ticket;
import veronika.lab5.parsing.InterractiveObjectParser;

import java.util.Collection;
import java.util.Scanner;

public class AddCommand implements Command {
    private Ticket ticket;

    @Override
    public Result execute(State state) {
        Collection<Ticket> ticketCollection = state.getCollection();
//        получаем коллекцию из State
        ticket.setId(state.generateID());
        ticket.setCreationDate(state.generateCreationDate());
        state.generateEventId();
//        генерируем айдишку для event
        ticketCollection.add(ticket);
        return Result.success("Элемент с id " + ticket.getId() + " был успешно добавлен в коллекцию");
    }

    @Override
    public void parse(Scanner scanner) {
        ticket = InterractiveObjectParser.createObj(Ticket.class, scanner);
    }
}
