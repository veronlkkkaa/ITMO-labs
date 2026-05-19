package veronika.lab5;

import veronika.lab5.models.Event;
import veronika.lab5.models.Ticket;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

public class State {
    private final Collection<Ticket> collection;
    private final Date initializationDate;
    private final CommandManager commandManager;
    private final String saveFileName;
    private int lastId;


    public State(String saveFileName) {
        this.saveFileName = saveFileName;
//        инициализируем файл
        collection = new TreeSet<>();
//        создаем нашу коллекцию
        initializationDate = new Date();
//      генерируется текущаяя дата, вот прям когда запустили
        commandManager = new CommandManager();
//        инициализируем commandManager
    }

    public Collection<Ticket> getCollection() {
        return collection;
    }


    public CommandManager getCommandManager() {
        return commandManager;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    public int generateID() {
        if (collection.isEmpty()) {
            lastId = 0;
            return ++lastId;
        } else {
            return ++lastId;
        }
    }


    public LocalDateTime generateCreationDate() {
        long minTime = LocalDateTime.of(1970, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
        long maxTime = LocalDateTime.of(2053, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
        long randomDay = ThreadLocalRandom.current().nextLong(minTime, maxTime);
        return LocalDateTime.ofEpochSecond(randomDay, 0, ZoneOffset.UTC);
    }

    public void generateEventId() {
        for (Ticket ticket : collection) {
            Event eventElement = ticket.getEvent();
            long leftLimit = 1L;
            long rightLimit = 10L;
            long generatedLong = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
            if (eventElement.getId() == null) {
                eventElement.setId(generatedLong);
            }
            eventElement.setId(generatedLong);
        }
    }



    public Date getInitializationDate() {
        return initializationDate;
    }

}

