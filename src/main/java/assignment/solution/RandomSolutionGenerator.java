package main.java.assignment.solution;

import javafx.util.Pair;
import main.java.assignment.LimitedQueue;
import main.java.assignment.model.IModelWrapper;

import java.util.Queue;
import java.util.Random;

public class RandomSolutionGenerator extends SolutionGeneration {

    IModelWrapper model;

    LimitedQueue<Move> lastMoves;
    Random random = new Random();

    public RandomSolutionGenerator(IModelWrapper model){
        super(model);
        this.model = model;
        lastMoves = new LimitedQueue<>(model.getExamsNumber()*3);
    }

    @Override
    public void iterate() {
       int exam = model.getRandomConflictedExam();
       int timeSlot = model.getExamTimeslot(exam);

       model.assignExams(timeSlot, exam, false);

       int bestValue = Integer.MAX_VALUE;
       int bestIndex = -1;

       for(int i=0; i<model.getTimeslotsNumber(); i++){
            int actualValue = model.estimateNumberOfConflictOfExam(i, exam);
            if(actualValue < bestValue && (i != timeSlot || random.nextInt(3) == 2)){
                bestIndex = i;
                bestValue = actualValue;
            }
       }

       if(bestIndex != -1){
           model.assignExams(bestIndex, exam, true);
           System.out.println("Assegnato");
       }else{
           model.assignExams(random.nextInt(model.getTimeslotsNumber()), exam, true);
           System.out.println("Assegnato a caso");
       }


    }

    class Move{
        int from;
        int to;
        int exam;
    }


}
