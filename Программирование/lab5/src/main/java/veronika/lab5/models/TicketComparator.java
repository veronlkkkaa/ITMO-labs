package veronika.lab5.models;

import java.util.Comparator;

public class TicketComparator implements Comparator<Ticket> {

    @Override
    public int compare(Ticket o1, Ticket o2) {
        return o1.getComment().length() - o2.getComment().length();
    }
}
