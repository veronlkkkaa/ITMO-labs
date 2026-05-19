package veronika.lab5.commands;

import veronika.lab5.State;
import veronika.lab5.models.Ticket;
import veronika.lab5.parsing.InterractiveObjectParser;

import java.util.Collection;
import java.util.Scanner;

public class ShowCommand implements Command {

    @Override
    public Result execute(State state) {
        Collection<Ticket> ticketCollection = state.getCollection();
        if (ticketCollection.isEmpty()) {
            return Result.success("Коллекция пустая");
        }
        String out = "";
        for (Ticket s :
                ticketCollection) {
            out += InterractiveObjectParser.printOnbject(s);
        }
        return Result.success("Элементы коллекции: " + "\n" + out);
    }

    @Override
    public void parse(Scanner scanner) {

    }
}
