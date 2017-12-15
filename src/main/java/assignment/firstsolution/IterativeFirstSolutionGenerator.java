package main.java.assignment.firstsolution;

import main.java.assignment.model.ModelWrapper;
import main.java.assignment.util.TimedSolutionGenerator;

import java.util.*;

public class IterativeFirstSolutionGenerator implements IFirstSolutionGenerator{

    private ModelWrapper model;

    public IterativeFirstSolutionGenerator(ModelWrapper model){
        this.model = model;
    }

    @Override
    public void generateFirstSolution() {
        model.clearExamsIfDone();

        Random random = new Random();
        int attemp = 0;

        int startingPoint = random.nextInt(model.getTimeslotsNumber());

        List<Integer> examLeft = new ArrayList<>();
        for(int i=0; i<model.getExamsNumber(); i++){
            examLeft.add(i);
        }

        for(int i=0; i<model.getExamsNumber(); i++){
            attemp = 0;
            int actualTimeSlot = startingPoint;
            int chosenExam = random.nextInt(examLeft.size());
            int actualExam = examLeft.get(chosenExam);
            examLeft.remove(chosenExam);
            while(!model.canIAssignExamHere(actualTimeSlot, actualExam)){

                actualTimeSlot = (actualTimeSlot+1)% model.getTimeslotsNumber();

                attemp++;
                if(attemp == model.getTimeslotsNumber()){
                    int min = Integer.MAX_VALUE;
                    int ind = -1;
                    for(int j=0; j<model.getTimeslotsNumber(); j++){
                        int tsConf = model.howManyConflictAnExamHave(j, actualExam);
                        if(tsConf < min){
                            ind = j;
                            min = tsConf;
                        }
                    }
                    if(ind != -1) actualTimeSlot = ind;
                    model.addConflict(actualTimeSlot, actualExam);
                    break;
                }
            }
            model.assignExams(actualTimeSlot, actualExam, true);
        }


        model.setAsDone();
    }

}
