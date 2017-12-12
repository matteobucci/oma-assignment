package main.java.assignment.util;

import main.java.assignment.model.ModelWrapper;

import java.util.Random;

public class RandomFirstSolutionGenerator extends TimedSolutionGenerator implements IFirstSolutionGenerator{

    Random random = new Random(System.currentTimeMillis());
    int tentativi = 0;

    @Override
    public void generateFirstSolution(ModelWrapper model) {
        startTiming();
        model.clearExamsIfDone();
        for(int i=0; i<model.getExamsNumber(); i++){
            tentativi = 0;
            int actualTimeSlot = random.nextInt(model.getTimeslotsNumber());
            while(!model.canIAssignExamHere(actualTimeSlot, i) && tentativi < 100){
                actualTimeSlot = random.nextInt(model.getTimeslotsNumber());
                tentativi++;
            }

            if(tentativi == 100) model.addConflict();

            model.assignExams(actualTimeSlot, i, true);
        }
        model.setAsDone();
        stopTiming();
    }

}
