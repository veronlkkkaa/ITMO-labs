package veronika.lab5;

import veronika.lab5.commands.Command;
import veronika.lab5.parsing.CSVparsing;
//import veronika.lab5.parsing.CSVparsing;

import java.io.IOException;
import java.util.Scanner;

//05.12.2002 12:12:12
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        State state = new State("D:\\Telegram Desktop\\lab5\\lab5\\src\\main\\java\\veronika\\lab5\\input.txt");
//        В классе State в конструкторе у нас создается объект класса CommandManager
//        а здесь мы присваиваем результат работы метода getCommandManager  в переменную commandManager
        CommandManager commandManager = state.getCommandManager();
        CSVparsing csVparsing = new CSVparsing(state);
        csVparsing.parseCSVtoString(state.getSaveFileName());

        System.out.println("Здравствуйте! Вы находитесь в программе управления коллекцией");
        while (true) {
            try {
                Command command = commandManager.createCommand(scanner.nextLine());
                command.parse(scanner);
                Command.Result result = command.execute(state);
                System.out.println(result.getMessage());
            } catch (NullPointerException e) {
                System.out.println("Такой команды не существует. Для того, чтобы узнать доступные команды напишите help.");
            }
        }
    }
}

