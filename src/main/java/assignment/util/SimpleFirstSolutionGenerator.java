package main.java.assignment.util;

import main.java.assignment.model.ModelWrapper;

public class SimpleFirstSolutionGenerator extends TimedSolutionGenerator implements IFirstSolutionGenerator{

    @Override
    public void generateFirstSolution(ModelWrapper model) {
        startTiming();
        model.clearExamsIfDone();
        for(int i=0; i<model.getExamsNumber(); i++){
            int actualTimeSlot = model.getTimeslotsNumber() -1;
            while(!model.canIAssignExamHere(actualTimeSlot, i)){
                actualTimeSlot--;
            }
            model.assignExams(actualTimeSlot, i, true);
        }
        model.setAsDone();
        stopTiming();
    }

}
