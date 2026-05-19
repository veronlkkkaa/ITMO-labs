package veronika.lab5.commands;

import veronika.lab5.State;

import java.util.Scanner;

public class ExitCommand implements Command {
    @Override
    public Result execute(State state) {
        System.exit(1);
        return Result.success("Совершен выход из программы");
    }

    @Override
    public void parse(Scanner scanner) {

    }
}
