package main.java.assignment.util;

import java.util.LinkedList;

public class BestList extends LinkedList<ExamPair> {

    private int limit;
    private double worstUntilNow;

    public BestList(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(ExamPair o) {
        System.out.println("Provo aggiungere punteggio " + o.getScore());
        if(worstUntilNow < o.getScore() && size() > limit){
            System.out.println("Non lo aggiungo.");
            return false;
        }else{
            //Aggiungo alla lista un model peggiore di quelli all'interno
            if(worstUntilNow < o.getScore()){
                worstUntilNow = o.getScore();
                System.out.println("Nuovo peggiore: " + worstUntilNow);
            }
            boolean added = super.add(o);
            while (added && size() > limit) {
                System.out.println("Ne tolgo uno");
                worstUntilNow = updateWorst(worstUntilNow);
            }
            return added;
        }
    }

    private double updateWorst(double worstUntilNow) {
        for(int i=0; i<size(); i++){
            //Ho trovato il peggiore
            if(get(i).getScore() == worstUntilNow){
                System.out.println("Tolto");
                remove(i);
                break;
            }
        }

        double newWorstUntilNow = 0;

        //Ora trovo il nuovo peggiore
        for(int i=0; i<size(); i++){
            if(get(i).getScore() > newWorstUntilNow) newWorstUntilNow = get(i).getScore();
        }
        System.out.println("Nuovo peggiore: " + newWorstUntilNow);
        return newWorstUntilNow;
    }

    public boolean shouldAdd(double score){
        return size() < limit || score < worstUntilNow;
    }

}