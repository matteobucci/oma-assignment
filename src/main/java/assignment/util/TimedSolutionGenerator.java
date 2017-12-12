package main.java.assignment.util;

import main.java.assignment.model.ModelWrapper;

public abstract class TimedSolutionGenerator{

    private long startTime = 0l;

    protected void startTiming(){
        startTime = System.currentTimeMillis();
    }

    protected void stopTiming(){
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Soluzione generata in "+ totalTime + "ms");
    }

}
