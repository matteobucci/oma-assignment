package main.java.assignment.util;

import main.java.assignment.model.ModelWrapper;

import java.util.Random;

public class BestRandomFirstSolutionGenerator extends TimedSolutionGenerator implements IFirstSolutionGenerator{

    Random random = new Random(System.currentTimeMillis());
    int tentativi = 0;

    private static final int MAX_ATTEMPT = 20;

    int bestActualIndex = -1;
    int bestActualValue = Integer.MAX_VALUE;

    @Override
    public void generateFirstSolution(ModelWrapper model) {
        startTiming();
        model.clearExamsIfDone();
        for(int i=0; i<model.getExamsNumber(); i++){
            tentativi = 0;
            int actualTimeSlot = random.nextInt(model.getTimeslotsNumber());
            while(!model.canIAssignExamHere(actualTimeSlot, i) && tentativi < MAX_ATTEMPT){ //Si potrebbe tentare ogni slot una volta sola

                int value = model.howManyConflictAnExamHave(actualTimeSlot, i);
                if(value < bestActualValue){
                    bestActualIndex = actualTimeSlot;
                    bestActualValue = value;
                }

                actualTimeSlot = random.nextInt(model.getTimeslotsNumber());
                tentativi++;
            }

            if(tentativi == MAX_ATTEMPT){
                model.assignExams(bestActualIndex, i, true);
                bestActualValue = Integer.MAX_VALUE;
                bestActualIndex = -1;
                model.addConflict();
            }else{
                model.assignExams(actualTimeSlot, i, true);

            }
        }
        model.setAsDone();
        stopTiming();
    }

}
