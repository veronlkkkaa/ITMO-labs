package veronika.lab5.commands;

import veronika.lab5.State;
import veronika.lab5.interaction.ArgumentException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ExecuteScriptCommand implements Command {
    private String filename;

    @Override
    public Result execute(State state) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(filename))) {
            Scanner scanner = new Scanner(inputStreamReader);
            String out = "";
            while (scanner.hasNext()) {
                String commandName = scanner.next();
                Command command = state.getCommandManager().createCommand(commandName);
                if (command == null) {
                    out += "Неизвестная команда" + commandName;
                    return Result.error(out);
                }
                try {
                    command.parse(scanner);
                } catch (ArgumentException e) {
                    return Result.error(out + "\nКоманда" + commandName + "имеет некорректный аргумент");
                }
                Result result = command.execute(state);
                out+= result.getMessage() + "\n";
                if (!result.isSuccess()) {
                    return Result.error(out);
                }
            }
            return Result.success(out);
        } catch (IOException e) {
            return Result.error("Не удалось прочитать файл");
        }

    }

    @Override
    public void parse(Scanner scanner) {
        filename = scanner.nextLine().trim();
        if (filename.isEmpty()) {
            throw new ArgumentException();
        }
        this.filename = filename;
    }
}
