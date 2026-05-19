package veronika.lab5.commands;

import veronika.lab5.State;

import java.util.List;
import java.util.Scanner;

//вывести последние 14 команд (без их аргументов)
public class HistoryCommand implements Command {
    @Override
    public Result execute(State state) {
        List<String> history = state.getCommandManager().getHistory();
        return Result.success("Последние 14 команд, которые вы использовали: " + String.join(",", history));
    }

    @Override
    public void parse(Scanner scanner) {

    }
}
