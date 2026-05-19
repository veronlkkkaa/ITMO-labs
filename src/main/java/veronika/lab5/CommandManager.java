package veronika.lab5;

import veronika.lab5.commands.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

// класс CommandManager нужен нам для создания и хранния команд
public class CommandManager {
    private final static Map<String, Supplier<Command>> commands = new HashMap<>();
//    в качестве ключа лежит строчка, просто название команды
//    в качестве значения лежит
    private final Map<String, String> helpMap = new HashMap<>();
    private final List<String> history = new LinkedList<>();

    //    в круглых скобках параметр, который принимается на вход, а справа то что получаем на выходе
    static {
        commands.put("add", AddCommand::new);
//        среда предлагает другой способ написания
//        можно так, а можно оставить как у меня
        commands.put("help", () -> new HelpCommand());
        commands.put("info", () -> new InfoCommand());
        commands.put("show", () -> new ShowCommand());
        commands.put("update", () -> new UpdateIdCommand());
        commands.put("remove_by_id", () -> new RemoveByIdCommand());
        commands.put("clear", () -> new ClearCommand());
        commands.put("save", () -> new SaveCommand());
        commands.put("execute_script", () -> new ExecuteScriptCommand());
        commands.put("exit", () -> new ExitCommand());
        commands.put("add_if_max", () -> new AddIfMaxCommand());
        commands.put("add_if_min", () -> new AddIfMinCommand());
        commands.put("history", () -> new HistoryCommand());
        commands.put("min_by_comment", () -> new MinByCommentCommand());
        commands.put("count_greater_than_event", () -> new CountGreaterThanEventCommand());
        commands.put("filter_greater_than_event", () -> new FilterGreaterThanEventCommand());
    }

    {
        helpMap.put("help", " вывести справку по доступным командам");
        helpMap.put("info", " вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        helpMap.put("show", " вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        helpMap.put("add {element}", " добавить новый элемент в коллекцию");
        helpMap.put("update id {element}", " обновить значение элемента коллекции, id которого равен заданному");
        helpMap.put("remove_by_id id", " удалить элемент из коллекции по его id");
        helpMap.put("clear", " очистить коллекцию");
        helpMap.put("save", " сохранить коллекцию в файл");
        helpMap.put("execute_script file_name", " считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
        helpMap.put("exit", " завершить программу (без сохранения в файл)");
        helpMap.put("add_if_max {element}", " добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции");
        helpMap.put("add_if_min {element}", " добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции");
        helpMap.put("history", " вывести последние 14 команд (без их аргументов)");
        helpMap.put("min_by_comment", " вывести любой объект из коллекции, значение поля comment которого является минимальным");
        helpMap.put("count_greater_than_event event", " вывести количество элементов, значение поля event которых больше заданного");
        helpMap.put("filter_greater_than_event event", " вывести элементы, значение поля event которых больше заданного");
    }

    public Command createCommand(String name) {
        Supplier<Command> supplier = commands.get(name);
//        получаем объект функционального интерфеса
        if (supplier == null) {
            return null;
        }
        history.add(name);
        if (history.size() >= 15) {
            history.remove(0);
        } else {
            return supplier.get();
        }

        return null;
    }

    public List<String> getHistory() {
        return history;
    }

    public Map<String, String> getHelpMap() {
        return helpMap;
    }

    @Override
    public String toString() {
        return "CommandManager{" +
                "helpMap=" + helpMap +
                '}';
    }
}



