package veronika.lab5.commands;

import veronika.lab5.State;
import veronika.lab5.models.Ticket;
import veronika.lab5.parsing.InterractiveObjectParser;

import java.util.*;

// добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции
public class AddIfMaxCommand implements Command {

    Ticket ticket;

    @Override
    public Result execute(State state) {
        Collection<Ticket> ticketCollection = state.getCollection();
        List<Ticket> ticketList = new ArrayList<>(ticketCollection);
        Collections.sort(ticketList);
        if (ticketList.isEmpty()) {
            return Result.error("Сорри, но вам не с чем сравнить введенный вами элемент коллекции, так как коллекция на данный момент пустая. Элемент не будет добавлен в коллекцию. ");
        } else {
            Ticket lastElemet = ticketList.get(-1);
            if (lastElemet.compareTo(ticket) < 0) {
                ticket.setId(state.generateID());
                ticket.setCreationDate(state.generateCreationDate());
                state.generateEventId();
                ticketCollection.add(ticket);
                return Result.success("Элемент добавлен в коллекцию, так как его значение превышает значение наибольшего элемента этой коллекции.");
            } else
                return Result.error("К сожалению, элемент не был добавлен в коллекцию, так как его значение меньше значения наибольшего элемента этой коллекции. ");
        }
    }

    @Override
    public void parse(Scanner scanner) {
        ticket = InterractiveObjectParser.createObj(Ticket.class, scanner);
    }
}
