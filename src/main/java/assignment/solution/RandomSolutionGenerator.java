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
    private int noMoves = 0;

    public RandomSolutionGenerator(IModelWrapper model){
        super(model);
        this.model = model;
        lastMoves = new LimitedQueue<>(model.getExamsNumber()/2);
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
        Move move = new Move(); move.exam = exam; move.from = timeSlot; move.to = bestIndex;

       if(bestIndex != -1 && !lastMoves.contains(move)){
           model.assignExams(bestIndex, exam, true);
           lastMoves.add(move);
           noMoves = 0;
       }else{
           if(noMoves > model.getConflictNumber()){
               model.assignExams(random.nextInt(model.getTimeslotsNumber()), exam, true);
               noMoves = 0;
           }else{
               model.assignExams(timeSlot, exam, true);
               noMoves++;
           }
       }


    }

    class Move{
        int from;
        int to;
        int exam;

        @Override
        public boolean equals(Object o) {
            if(o instanceof Move){
                Move move = (Move) o;
                return move.exam == this.exam && move.from == this.to && move.to == this.from;
            }
           return false;
        }


    }


}
