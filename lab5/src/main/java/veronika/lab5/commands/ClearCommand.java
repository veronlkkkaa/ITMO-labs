package veronika.lab5.commands;

import veronika.lab5.State;
import veronika.lab5.models.Ticket;

import java.util.Collection;
import java.util.Scanner;

public class ClearCommand implements Command {

    @Override
    public Result execute(State state) {
        Collection<Ticket> ticketCollection = state.getCollection();
        ticketCollection.clear();
        return Result.success("Коллекция очищена. Поздравляю! ");
    }

    @Override
    public void parse(Scanner scanner) {

    }
}
