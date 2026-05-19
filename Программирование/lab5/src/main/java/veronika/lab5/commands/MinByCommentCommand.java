package veronika.lab5.commands;

import veronika.lab5.State;
import veronika.lab5.models.Ticket;
import veronika.lab5.models.TicketComparator;
import veronika.lab5.parsing.InterractiveObjectParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

//вывести любой элемент из коллекции, значение поля comment которого является минимальным
public class MinByCommentCommand implements Command {

    @Override
    public Result execute(State state) {
        Collection<Ticket> ticketCollection = state.getCollection();
        List<Ticket> ticketList = new ArrayList<>(ticketCollection);
        if (ticketList.isEmpty()) {
            return Result.error("Коллекция пуста. Невозможно вывести какой-либо элемент.");
        }
        ticketList.sort(new TicketComparator());
        Ticket minElement = ticketList.get(0);
        String out = InterractiveObjectParser.printOnbject(minElement);
        return Result.success("Элемент из коллекции, значение поля comment которого является минимальным: " + out);
    }

    @Override
    public void parse(Scanner scanner) {

    }
}
