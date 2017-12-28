package main.java.assignment.improvement;

import main.java.assignment.model.IModelWrapper;
import main.java.assignment.solution.RandomSolutionGenerator;
import main.java.assignment.util.LimitedQueue;

import java.util.*;

public class TabuSearchImprovator implements ISolutionImprovator {

    /* Parametri della TABU SEARCH */

    //Dimensione lista
    private static final int LIST_SIZE = 1000;
    //Percentuale di miglioramento oltre la quale ignoro che una soluzione sia scartata
    private static final float PERC_IMPROV = 1f;

    private boolean accettaTutto = false; //Se false accetta solo solo soluzioni migliori

    private IModelWrapper model;


    LimitedQueue<Move> queue = new LimitedQueue(LIST_SIZE);


    public TabuSearchImprovator(IModelWrapper model){
        this.model = model;
    }


    @Override
    public void iterate() {

        //Il vicinato generato
        List<Move> vicinato = new ArrayList<>();
        double startScore = model.getActualScore();  //Il punteggio della soluzione iniziale

        //Variabile temporale che tiene traccia del punteggio attuale
        double actualScore = 0;

        //Variabili che indicano l'esame corrente e l'esame migliore
        int ex = 0; //Esame corrente
        int exTimeSlot = 0; //Timeslot esame corrente

        /*
         --- GENERAZIONE VICINATO ---
         Considero come vicinato ogni mossa di timeslot per ogni esame del modello
        */
        for(ex = 0; ex < model.getExamsNumber(); ex++){ //Considero ogni esame
            exTimeSlot = model.getExamTimeslot(ex);

            for(int i=0; i<model.getTimeslotsNumber(); i++){ //Considero ogni timeslot
                //Escludo il timeslot corrente dal calcolo

                if(i != exTimeSlot){
                    //Escludo tutte le mosse che mi generano conflitti
                    if(model.estimateNumberOfConflictOfExam(i, ex) == 0){


                        //MOSSA VALIDA! CALCOLO IL PUNTEGGIO
                        actualScore = model.getScoreOfAMove(ex, exTimeSlot, i);

                        if(accettaTutto || startScore - actualScore > 0){
                            Move move = new Move();
                            move.exam = ex;
                            move.from = exTimeSlot;
                            move.to = i;
                            move.deltaMove = startScore - actualScore;

                            vicinato.add(move);
                        }

                    }

                }

            }//Ciclo sui timeslot
        }//Ciclo sugli esami

        //Controllo di avere un vicinato con mosse valide
        if(vicinato.isEmpty()){
            System.out.println("Il vicinato non ha soluzioni valide o migliori");
            return;
        }else{
            System.out.println("Vicinato con " + vicinato.size() + " elementi");
        }



        /*
         --- CONTROLLO VICINATO ---
        */

        vicinato.sort((move, t1) -> {
            if (move.deltaMove == t1.deltaMove) return 0;
            return move.deltaMove < t1.deltaMove ? 1 : -1;
        });



        for(Move move : vicinato){
            if(!queue.contains(move) || move.deltaMove > ((startScore / 100) * PERC_IMPROV)){
                model.assignExams(move.from, move.exam, false);
                model.assignExams(move.to, move.exam, true);
                queue.add(move);
                return;
            }else{
             //   System.out.println("Mossa non accettata");
            }
        }

        System.out.println("Nessuna mossa papabile");

    }

    class Move{
        int from;
        int to;
        int exam;
        double deltaMove;

        @Override
        public boolean equals(Object o) {
            if(o instanceof Move){
                Move move = (Move) o;
                //return move.exam == this.exam && move.from == this.to && move.to == this.from;
                return move.exam == this.exam &&  move.to == this.from;
            }
            return false;
        }


    }



}
