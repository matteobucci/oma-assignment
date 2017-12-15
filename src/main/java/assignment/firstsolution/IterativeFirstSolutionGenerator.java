package main.java.assignment.firstsolution;

import main.java.assignment.model.IModelWrapper;

import java.util.*;

public class IterativeFirstSolutionGenerator implements IFirstSolutionGenerator{

    private IModelWrapper model;

    public IterativeFirstSolutionGenerator(IModelWrapper model){
        this.model = model;
    }

    @Override
    public void generateFirstSolution() {
        model.clearExamsMatrix();

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
            while(model.estimateNumberOfConflictOfExam(actualTimeSlot, actualExam) != 0){

                actualTimeSlot = (actualTimeSlot+1)% model.getTimeslotsNumber();

                attemp++;
                if(attemp == model.getTimeslotsNumber()){
                    int min = Integer.MAX_VALUE;
                    int ind = -1;
                    for(int j=0; j<model.getTimeslotsNumber(); j++){
                        int tsConf = model.estimateNumberOfConflictOfExam(j, actualExam);
                        if(tsConf < min){
                            ind = j;
                            min = tsConf;
                        }
                    }
                    if(ind != -1) actualTimeSlot = ind;
                    break;
                }
            }
            model.assignExams(actualTimeSlot, actualExam, true);
        }

    }

}
