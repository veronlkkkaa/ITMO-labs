package veronika.lab5.commands;

import veronika.lab5.State;

import java.util.Map;
import java.util.Scanner;

public class HelpCommand implements Command {
    @Override
    public Result execute(State state) {
        Map<String, String> helpMap = state.getCommandManager().getHelpMap();
        String result = "";
        for (String key : helpMap.keySet()) {
            for (String value : helpMap.values()
            ) {
                result += key + value + "\n";
                break;
            }
        }
        return Result.success(result);
    }

    @Override
    public void parse(Scanner scanner) {

    }
}
