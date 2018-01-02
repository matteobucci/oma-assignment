package main.java.assignment.improvement;

import main.java.assignment.model.IModelWrapper;
import main.java.assignment.solution.TabuSearchSolutionGenerator;
import main.java.assignment.util.LimitedQueue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class SwapSolutionImprovator implements ISolutionImprovator {

    private IModelWrapper model;

    private LimitedQueue<Move> listaUltimeMosse = new LimitedQueue<>(10);
    private Random random = new Random();

    public SwapSolutionImprovator(IModelWrapper model){
        this.model = model;
    }


    @Override
    public void iterate() {

        int timeSlot1;
        int timeSlot2;

        List<Move> vicinato = new ArrayList<>();

        for (int i = 0; i < model.getExamsNumber(); i++) {
            for (int j = 0; j < model.getExamsNumber(); j++) {
                timeSlot1 = model.getExamTimeslot(i);
                timeSlot2 = model.getExamTimeslot(j);

                if (model.estimateNumberOfConflictOfExam(timeSlot2, i) == 0
                        && model.estimateNumberOfConflictOfExam(timeSlot1, j) == 0) {
                    //Swap possibile
                    double deltaScore =(model.getScoreOfAMove(i, timeSlot1, timeSlot2) + model.getScoreOfAMove(j, timeSlot1, timeSlot2) - (model.getActualScore()*2));

                    Move move = new Move(i, j, timeSlot1, timeSlot2, deltaScore);
                    vicinato.add(move);

                }
            }
        }

        vicinato.sort((move, t1) -> {
            if (move.deltaMove == t1.deltaMove) return 0;
            return move.deltaMove < t1.deltaMove ? 1 : -1;
        });



        int i=0;
        for(Move move: vicinato){
            i++;
            if(!listaUltimeMosse.contains(move)){
                model.assignExams(move.from, move.exam1, false);
                model.assignExams(move.to, move.exam1, true);
                model.assignExams(move.to, move.exam2, false);
                model.assignExams(move.from, move.exam2, true);
                listaUltimeMosse.add(move);
                System.out.println("Assegno esame " + i);
                return;
            }
        }

        System.out.println("Finito la lista di swap possibili");

    }

    class Move{
        int from;
        int to;
        int exam1;
        int exam2;
        double deltaMove;

        public Move(int exam1, int exam2, int from, int to, double delta) {
            this.exam1 = exam1;
            this.exam2 = exam2;
            this.from = from;
            this.to = to;
            this.deltaMove = delta;
        }


        @Override
        public boolean equals(Object o) {
            if(o instanceof Move){
                Move move = (Move) o;
                return (move.exam2 == exam1 || move.exam1 == exam2) &&
                        (move.from == to || move.to == from || move.from == from || move.to == to);
                //return move.exam == this.exam &&  move.to == this.from;
            }
            return false;
        }


    }



}
