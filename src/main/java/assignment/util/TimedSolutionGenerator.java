package main.java.assignment.util;

import main.java.assignment.model.IModelWrapper;
import main.java.assignment.solution.SolutionGeneration;

public abstract class TimedSolutionGenerator extends SolutionGeneration{

    private long startTime = 0l;

    public TimedSolutionGenerator(IModelWrapper model) {
        super(model);
    }

    protected void startTiming(){
        startTime = System.currentTimeMillis();
    }

    protected void stopTiming(){
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Soluzione generata in "+ totalTime + "ms");
    }

}
