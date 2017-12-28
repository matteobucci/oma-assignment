package main.java.assignment.solution;

import main.java.assignment.model.IModelWrapper;
import main.java.assignment.util.LimitedQueue;

import java.util.*;

public class TabuSearchSolutionGenerator extends SolutionGeneration {

    IModelWrapper model;

    private int queueSize = 200;
    LimitedQueue<Move> lastMoves = new LimitedQueue<>(queueSize);
    Random random = new Random();
    private boolean moveGoodExam;


    public TabuSearchSolutionGenerator(IModelWrapper model){
        super(model);
        this.model = model;
    }

    @Override
    public void iterate() {

        List<Move> vicinato = new ArrayList<>();


        if(!moveGoodExam){
            Set<Integer> exams = model.getConflicts();

            int actualConflict = -1;
            int actualTimeSlot = -1;

            for(Integer ex: exams){
                actualTimeSlot = model.getExamTimeslot(ex);
                actualConflict = model.getNumberOfConflictOfExam(ex);

                for(int i=0; i<model.getTimeslotsNumber(); i++){
                    Move move = new Move();
                    move.exam = ex;
                    move.from = actualTimeSlot;
                    move.to = i;
                    move.delta = actualConflict - model.estimateNumberOfConflictOfExam(i, ex);

                    vicinato.add(move);
                }
            }

            if(vicinato.isEmpty()){
                System.out.println("Vicinato vuoto. Rinuncio");
                return;
            }else{
                System.out.println("Dimensione vicinato:  " + vicinato.size());
            }

            //Controllo la dimensione del vicinato
            if(vicinato.size()/20 < queueSize){
                lastMoves.newSize(vicinato.size()/20);
                queueSize = vicinato.size()/20;
                System.out.println("Nuova dimensione vicinato");
            }

            vicinato.sort(Comparator.comparingInt(move -> -move.delta));

            for(Move move : vicinato){
                if(move.delta > 3){
                    model.assignExams(move.from, move.exam, false);
                    model.assignExams(move.to, move.exam, true);
                    lastMoves.add(move);
                    System.out.println("Miglioramento evidente.");
                    return;
                }else if(!lastMoves.contains(move)){
                    model.assignExams(move.from, move.exam, false);
                    model.assignExams(move.to, move.exam, true);
                    lastMoves.add(move);
                    return;
                }
            }

            System.out.println("Nessun movimento fattibile");

            int divisiore = 1;
            if(vicinato.size() > 1000){
                divisiore = 100;
            }else if(vicinato.size() > 100){
                divisiore = 10;
            }
            int selectedIndex = random.nextInt(vicinato.size()/divisiore);
            Move selectedMove = vicinato.get(selectedIndex);
            model.assignExams(selectedMove.from, selectedMove.exam, false);
            model.assignExams(selectedMove.to, selectedMove.exam, true);
            lastMoves.add(selectedMove);

            moveGoodExam = true;
        }else{
            System.out.println("CONSIDERO TUTTI");
            moveGoodExam = false;


            int actualConflict = -1;
            int actualTimeSlot = -1;

            for(int ex = 0; ex < model.getTimeslotsNumber(); ex++){
                actualTimeSlot = model.getExamTimeslot(ex);
                actualConflict = model.getNumberOfConflictOfExam(ex);

                for(int i=0; i<model.getTimeslotsNumber(); i++){
                    Move move = new Move();
                    move.exam = ex;
                    move.from = actualTimeSlot;
                    move.to = i;
                    move.delta = actualConflict - model.estimateNumberOfConflictOfExam(i, ex);

                    vicinato.add(move);
                }
            }



            vicinato.sort(Comparator.comparingInt(move -> -move.delta));

            for(Move move : vicinato){
                if(move.delta > 3){
                    model.assignExams(move.from, move.exam, false);
                    model.assignExams(move.to, move.exam, true);
                    lastMoves.add(move);
                    System.out.println("Miglioramento evidente.");
                    return;
                }else if(!lastMoves.contains(move)){
                    model.assignExams(move.from, move.exam, false);
                    model.assignExams(move.to, move.exam, true);
                    lastMoves.add(move);
                    return;
                }
            }

            System.out.println("Nessun movimento fattibile");

            int divisiore = 1;
            if(vicinato.size() > 1000){
                divisiore = 100;
            }else if(vicinato.size() > 100){
                divisiore = 10;
            }
            int selectedIndex = random.nextInt(vicinato.size()/divisiore);
            Move selectedMove = vicinato.get(selectedIndex);
            model.assignExams(selectedMove.from, selectedMove.exam, false);
            model.assignExams(selectedMove.to, selectedMove.exam, true);
            lastMoves.add(selectedMove);



        }





    }

    class Move{
        int from;
        int to;
        int exam;
        int delta;

        @Override
        public boolean equals(Object o) {
            if(o instanceof Move){
                Move move = (Move) o;
                return move.exam == this.exam && move.to == this.from;
            //    return move.exam == this.exam && move.to == this.from && move.from == this.to;
            }
           return false;
        }


    }


}
