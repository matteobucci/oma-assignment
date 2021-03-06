package main.java.assignment.solution;

import main.java.assignment.model.AssignmentModel;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.util.LimitedQueue;

import java.util.*;

public class TabuSearchSolutionGenerator extends SolutionGeneration {


    private IModelWrapper model;

    private int dimensioneLista = 200;                  //Dimensione della lista (può essere dinamica)
    private int dimensioneListaEsami = 100;             //Dimensione lista esami
    private static final int MIGLIORAMENTO_OTTIMO = 3;  //Valore di conflitti in meno verso il quale assegno direttamente la mossa

    private LimitedQueue<Move> listaUltimeMosse = new LimitedQueue<>(dimensioneLista);
    private LimitedQueue<Integer> ultimiEsamiMossi;
    private Random random = new Random();

    private int bestConflitti = Integer.MAX_VALUE;
    private AssignmentModel bestModel = null;

    //Cerco di muovere solo esami in conflitto con altri
    //Nel caso le mosse non siano più disponibili però (vicinato piccolo o mosse finite
    //Setto questa variabile a true e muovo un esame senza conflitti (possibilimente in un altro posto senza conflitti)
    //Per cercare di variare la situazione
    private int muoviEsamiBuoni = 0;

    public TabuSearchSolutionGenerator(IModelWrapper model){
        super(model);
        this.model = model;
        ultimiEsamiMossi = new LimitedQueue<>(model.getExamsNumber()/10); //DEVE ESSERE DINAMICA A SECONDA DEI CONFLITTI RIMASTI
    }

    @Override
    public void iterate() {

        if(model.getConflictNumber() < bestConflitti){
            bestModel = model.getAssignmentModel().clone();
            bestConflitti = model.getConflictNumber();
        }

        if( (bestConflitti <= 5 && (bestConflitti + 5) < model.getConflictNumber())
            || bestConflitti > 5 && (bestConflitti + 10) < model.getConflictNumber()) {
            //  (bestConflitti*2) < model.getConflictNumber() || (bestConflitti+10) < model.getConflictNumber())
            //Es. Avevo raggiunto 2 conflitti ora ne ho 11
            //Es. Avevo raggiunto 5 conflitti ora ne ho 100

            model.changeModel(bestModel);
            bestModel = bestModel.clone();
          //  model.shift();
        }




        //Lista che ospita il vicinato
        List<Move> vicinato = new ArrayList<>();

        //Scelgo il tipo di vicinato che mi genero
        if(muoviEsamiBuoni > 0){
            popolaVicinatoAll(vicinato); //Da scegliere se il vicinato di mosse tra gli esami in conflitto non soddisfa

        }
        else{
            popolaVicinatoConflitti(vicinato);
        }

        //Di default preferisco muovere gli esami in conflitto
        muoviEsamiBuoni--;

        //Eventualmente cambio la dimensione della tabu list
        aggiustaDimensioneLista(vicinato);

        //Ordino il vicinato in ordine di vantaggio (in questo caso la differenza tra conflitti iniziali e finali di ogni esame
        vicinato.sort(Comparator.comparingInt(move -> -move.delta));


        //Assegno l'esame. Se non viene assegnato continuo l'esecuzione
        if(assegnaEsameDalVicinato(vicinato)) return;

        //Scelgo comunque una delle mosse appartenenti al vicinato.
        eseguiMossaComunque(vicinato);

        //La prossima iterazione comunque muovo anche esami non in conflitto
        muoviEsamiBuoni = 2;

         /*
        Sta cosa mi fa risolvere un sacco di cose (istanza 03 05 04 02) la 06 ancora fa schifo
         */
        if(model.getConflictNumber() <= 3 && model.getConflictNumber() > 0){
            //Devo fare qualcosa per togliere questi due benedetti conflitti
            int examToMove = model.getRandomConflictedExam();
            int timeSlot = model.getExamTimeslot(examToMove);
            int selectedIndex = random.nextInt(model.getTimeslotsNumber());
            model.moveExam(examToMove, timeSlot,selectedIndex);
        }

    }

    /*
    Questo è molto comodo per aggiustare la lista di mosse vietate in funzione del vicinato generato.
    Siccome il vicinato (mosse possibili) varia molto dall'inizio alla fine dell'algoritmo, può essere comodo
    spostare la dimensione della lista.
    Accorciando la lista vengono automaticamente eliminate le mosse vecchie.
     */
    private void aggiustaDimensioneLista(List<Move> vicinato) {
        //Con 10 ho paura sia troppo piccolo ma arriva velocemente a pochi conflitti
        //Con 5 mi sembra molto migliore ma si muovono solo gli esami in cima e non capisco come mai

        if(vicinato.size()/5 < dimensioneLista){
            listaUltimeMosse.newSize(vicinato.size()/5);
            dimensioneLista = vicinato.size()/5;
        }


        if(model.getConflicts().size()/3 < dimensioneListaEsami){
            ultimiEsamiMossi.newSize(model.getConflicts().size()/3);
            dimensioneListaEsami = model.getConflicts().size()/3;
            //Mettere 4 non mi sembra cambi tantissimo
        }
    }

    /*
    Riempio il vicinato con tutte le mosse possibili da parte degli esami in conflitto
     */
    private void popolaVicinatoConflitti(List<Move> vicinato) {
        Set<Integer> esamiInConflitto = model.getConflicts(); //Considero solo gli esami che hanno conflitto

        int conflittiAttuali; //Numero conflitti esame selezionato
        int timeSlotAttuale; //Numero timeslot esame selezionato

        for(Integer ex: esamiInConflitto){ //Per ogni esame in conflitto

            timeSlotAttuale = model.getExamTimeslot(ex);             //Trovo il timeslot
            conflittiAttuali = model.getNumberOfConflictOfExam(ex);   //Trovo il numero di conflitti

            for(int i=0; i<model.getTimeslotsNumber(); i++){ //Per ogni timeslot

                //Aggiungo la mossa al vicinato
                int delta = conflittiAttuali - model.estimateNumberOfConflictOfExam(i, ex);
                Move move = new Move(ex, timeSlotAttuale, i, delta);
                vicinato.add(move);
            }
        }
    }

    /*
    Riempio il vicinato con tutte le mosse possibili da parte di tutti gli esami

    Proposta ottimizzazione: visto che comunque la scelta di muovere esami "buoni" verrà effettuata quando gli esami
    in conflitto non riescono a muoversi, perchè non rimuovere questi ultimi dal vicinato?
     */
    private void popolaVicinatoAll(List<Move> vicinato){
        int actualTimeSlot;
        int actualConflicts;
        for (int ex = 0; ex < model.getExamsNumber(); ex++) {
            if(model.getConflicts().contains(ex)) continue; //Potrebbe funzionare
            actualTimeSlot = model.getExamTimeslot(ex);
            actualConflicts = model.getNumberOfConflictOfExam(ex);

            for (int i = 0; i < model.getTimeslotsNumber(); i++) {
                Move move = new Move(ex, actualTimeSlot, i, actualConflicts - model.estimateNumberOfConflictOfExam(i, ex));
                vicinato.add(move);
            }
        }
    }


    /*
    Eseguo una mossa nel caso il miglioramento sia evidente o se la mossa non appartiene alla lista.
    Il problema di avere una lista con le mosse inverse vietate è quello di spostamenti circolari di un esame:
                Esame 1 che va da A -> B -> C -> A
    Questo problema viene risolto con una lista di ultimi esami spostati. Se un esame ama essere spostato può farlo, ma
    prima da l'opportunità ad altri di muoversi. Questo migliora drasticamente l'algoritmo.
     */
    private boolean assegnaEsameDalVicinato(List<Move> vicinato){
        for (Move move : vicinato) {
            if (move.delta > MIGLIORAMENTO_OTTIMO) {
                moveExam(move);
                return true;
            } else if (!listaUltimeMosse.contains(move) && !ultimiEsamiMossi.contains(move.exam)) {
                moveExam(move);
                return true;
            }
        }
        return false;
    }

    /*
    Se per questioni di lista tabu non riesco ad assegnare nessuna mossa,
    assegno comunque una mossa tra le migliori
     */
    private void eseguiMossaComunque(List<Move> vicinato){
        int divisiore = 1;
        if (vicinato.size() > 1000) {
            divisiore = 100;
        } else if (vicinato.size() > 100) {
            divisiore = 10;
        }else if(vicinato.size() == 0){
            return;
        }


        int selectedIndex = random.nextInt(vicinato.size() / divisiore);
        Move selectedMove = vicinato.get(selectedIndex);
        moveExam(selectedMove);
    }

    private void moveExam(Move selectedMove){
        model.moveExam(selectedMove.exam, selectedMove.from, selectedMove.to);
     //   model.assignExams(selectedMove.from, selectedMove.exam, false);
     //   model.assignExams(selectedMove.to, selectedMove.exam, true);
        listaUltimeMosse.add(selectedMove);
        ultimiEsamiMossi.add(selectedMove.exam);
    }


    class Move{
        int from;
        int to;
        int exam;
        int delta;

        public Move(int exam, int from, int to, int delta) {
            this.exam = exam;
            this.from = from;
            this.to = to;
            this.delta = delta;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof Move){
                Move move = (Move) o;
                //   return move.exam == this.exam && move.to == this.from;
                return move.exam == this.exam && move.to == this.from && move.from == this.to;
            }
            return false;
        }
    }

}
