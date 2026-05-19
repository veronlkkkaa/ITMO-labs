package veronika.lab5.parsing;

import veronika.lab5.State;
import veronika.lab5.models.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class CSVparsing {

    State state;

    public CSVparsing(State state) {
        this.state = state;
    }

    public void parseCSVtoString(String file) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file))){
            Scanner reader = new Scanner(inputStreamReader);
            reader.next();
            while (reader.hasNext()) {
                String line = reader.next(); // nextLine?
                String[] row = line.split(";");
                List<String> arguments = Arrays.asList(row);
                createObject(arguments);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Путь к файлу был передан неверно. ");
        }

    }
    private void createObject(List<String> args){
        int i = 0;
        try{
            String name = args.get(i++);
            Double coorX = Double.valueOf(args.get(i++));
            int coorY = Integer.parseInt(args.get(i++));
            Coordinates coordinates = new Coordinates(coorX,coorY);
            float price = Float.parseFloat(args.get(i++));
            String comment = args.get(i++);
            TicketType type = TicketType.valueOf(args.get(i++));
            String event_name = args.get(i++);
            String[] birthday = args.get(i++).split("-");
            LocalDateTime localDateTime = LocalDateTime.of(
                    Integer.parseInt(birthday[2]), Integer.parseInt(birthday[1]),
                    Integer.parseInt(birthday[0]), 0, 0
            );
            EventType eventType = EventType.valueOf(args.get(i++));
            Event event = new Event(event_name,localDateTime,eventType);
            Ticket ticket = new Ticket(name,coordinates,price,comment,type,event);
            Collection<Ticket> ticketCollection = state.getCollection();
            ticketCollection.add(ticket);
        }
        catch (Exception e){
            System.out.println("Возникла ошибка при чтении вашего CSV-файла. Неверная структура CSV-файла.");
        }
    }
}
