package main.java.assignment.util;

import java.util.LinkedList;

public class LimitedQueue<E> extends LinkedList<E> {

    private int limit;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        boolean added = super.add(o);
        while (added && size() > limit) {
            super.remove();
        }
        return added;
    }

    public void newSize(int i) {
        if(i < limit) {
            for(i=limit; i<this.size(); i++){
                this.remove(i);
            }
        }

        limit = i;
    }
}