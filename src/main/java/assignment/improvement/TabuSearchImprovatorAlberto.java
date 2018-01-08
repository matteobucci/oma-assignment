package main.java.assignment.improvement;

import main.java.assignment.model.IModelWrapper;
import main.java.assignment.solution.RandomSolutionGenerator;
import main.java.assignment.util.LimitedQueue;

import java.util.*;

public class TabuSearchImprovatorAlberto implements ISolutionImprovator {

    /* Parametri della TABU SEARCH */

    //Percentuale di miglioramento oltre la quale ignoro che una soluzione sia scartata
    private static final float PERC_IMPROV = 1f;

    private IModelWrapper model;


    LimitedQueue<Move> queue;

    public TabuSearchImprovatorAlberto(IModelWrapper model) {
        this.model = model;
        queue = new LimitedQueue(model.getExamsNumber()*model.getTimeslotsNumber()/75);
    }


    @Override
    public void iterate() {


        //Il vicinato generato
        List<Move> vicinato = new ArrayList<>();
        double startScore = model.getActualScore();  //Il punteggio della soluzione iniziale NON SERVE FORSE

        //Variabile temporale che tiene traccia del punteggio attuale
        double actualScore = 0;

        //Variabili che indicano l'esame corrente e l'esame migliore
        int ex = 0; //Esame corrente
        int exTimeSlot = 0; //Timeslot esame corrente
        double bestScore = 100000;
        int fromTimeslot , toTimeslot , examToChange = 0;
        fromTimeslot=model.getExamTimeslot(examToChange);
        toTimeslot=fromTimeslot;
        /*
         --- GENERAZIONE VICINATO ---
         Considero come vicinato ogni mossa di timeslot per ogni esame del modello
        */

            for (ex = 0; ex < model.getExamsNumber(); ex++) { //Considero ogni esame
                //System.out.println("ex= " + ex + " ");

                exTimeSlot = model.getExamTimeslot(ex);

                for (int i = 0; i < model.getTimeslotsNumber(); i++) { //Considero ogni timeslot
                    //Escludo il timeslot corrente dal calcolo
                    //System.out.println("ts= " + i + " ");

                    if (i != exTimeSlot) {
                        //Escludo tutte le mosse che mi generano conflitti
                        Move mTmp = new Move();
                        mTmp.exam = ex;
                        mTmp.from = exTimeSlot;
                        mTmp.to = i;
                        if (model.estimateNumberOfConflictOfExam(i, ex) == 0 && !queue.contains(mTmp)) {

                            //MOSSA VALIDA! CALCOLO IL PUNTEGGIO
                            actualScore = model.getScoreOfAMove(ex, exTimeSlot, i);

                            if (actualScore < bestScore) {
                                bestScore = actualScore;
                                fromTimeslot = exTimeSlot;
                                toTimeslot = i;
                                examToChange = ex;
                            }


                        }
                    }

                }//Ciclo sui timeslot
            }//Ciclo sugli esami

            // fuori da tutto
            Move move = new Move();
            move.exam = examToChange;
            move.from = fromTimeslot;
            move.to = toTimeslot;



            // inserimento della mossa nella tabu list
            if(move.from != move.to) {
                queue.add(move);
                model.assignExams(move.from, move.exam, false);
                model.assignExams(move.to, move.exam, true);
            }



            bestScore=100000;
        }


    }


    class Move {
        int from;
        int to;
        int exam;
        double deltaMove;

        @Override
        public boolean equals(Object o) {
            if (o instanceof Move) {
                Move move = (Move) o;
                //    return move.exam == this.exam && move.from == this.to && move.to == this.from;
                return move.exam == this.exam && move.to == this.from;
            }
            return false;
        }


    }


