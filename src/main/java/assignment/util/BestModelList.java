package main.java.assignment.util;

import java.util.LinkedList;

public class BestModelList extends LinkedList<ModelPair> {

    private int limit;
    private double worstUntilNow;

    public BestModelList(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(ModelPair o) {
        if(worstUntilNow < o.getScore() && size() > limit){
            return false;
        }else{
            //Aggiungo alla lista un model peggiore di quelli all'interno
            if(worstUntilNow < o.getScore()){
                worstUntilNow = o.getScore();
            }
            boolean added = super.add(o);
            while (added && size() > limit) {
                worstUntilNow = updateWorst(worstUntilNow);
            }
            return added;
        }
    }

    private double updateWorst(double worstUntilNow) {
        for(int i=0; i<size(); i++){
            //Ho trovato il peggiore
            if(get(i).getScore() == worstUntilNow){
                remove(i);
                break;
            }
        }

        double newWorstUntilNow = 0;

        //Ora trovo il nuovo peggiore
        for(int i=0; i<size(); i++){
            if(get(i).getScore() > newWorstUntilNow) newWorstUntilNow = get(i).getScore();
        }
        return newWorstUntilNow;
    }

    public boolean shouldAdd(double score){
        return size() < limit || score < worstUntilNow;
    }

}