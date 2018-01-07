package main.java.assignment.model;

import main.java.assignment.scorecalculator.IDeltaScoreCalculator;
import main.java.assignment.scorecalculator.IScoreCalculator;

import java.util.*;

public class ModelWrapper implements IModelWrapper {

    private double scoreCache = 0;
    private boolean isScoreValid = false;
    private boolean stampaSoloSoluzioniComplete = false;

    long minDelayPrint = 100; //100ms
    long lastPrint = 0; //L'ultima volta che ho stampato a schermo qualcosa

    Random random = new Random(System.currentTimeMillis());

    private List<ModelListener> listeners = new ArrayList<>();
    private AssignmentModel model;
    private IScoreCalculator calculator;
    private IDeltaScoreCalculator deltaCalculator;
    private Set<Integer> conflictedExams = new HashSet<>();
    private Set<Integer> examsToAssign = new HashSet<>();

    public ModelWrapper(int slotsNumber, int examsNumber, IScoreCalculator calculator, IDeltaScoreCalculator deltaCalculator){
        model = new AssignmentModel(slotsNumber,examsNumber);
        this.calculator = calculator;
        this.deltaCalculator = deltaCalculator;
        clearExamsMatrix();
    }

    public ModelWrapper(AssignmentModel assignmentModel, IScoreCalculator calculator, IDeltaScoreCalculator deltaCalculator){
        System.out.println("Generazione ModelWrapper a partire da soluzione esistente");
        model = assignmentModel;

        this.calculator = calculator;
        this.deltaCalculator = deltaCalculator;

        for(int i = 0; i < model.getExamMatrix().length; i++){
            for(int j = 0; j < model.getExamMatrix()[i].length; j++){
                // i -> Timeslot
                // j -> Esame
                if(model.getExamMatrix()[i][j]){
                    processConflict(j, i, true);
                }
            }
        }

        System.out.println("Generazione completa. Esami in conflitto presenti: " + getConflicts().size());
        scoreCache = calculator.getScore(model, getConflicts().size());
        isScoreValid = true;
        System.out.println("Punteggio soluzione = " + scoreCache);

    }

    @Override
    public AssignmentModel getAssignmentModel() {
        return model;
    }

    @Override
    public void changeModel(AssignmentModel model) {
        conflictedExams.clear();
        this.model = model;

        for(int i = 0; i < model.getExamMatrix().length; i++){
            for(int j = 0; j < model.getExamMatrix()[i].length; j++){
                // i -> Timeslot
                // j -> Esame
                if(model.getExamMatrix()[i][j]){
                    processConflict(j, i, true);
                }
            }
        }

    //    System.out.println("Model sostituito. Esami in conflitto presenti: " + getConflicts().size());
        scoreCache = calculator.getScore(model, getConflicts().size());
        isScoreValid = true;
     //   System.out.println("Punteggio soluzione = " + scoreCache);
    }

    @Override
    public void setListener(ModelListener listener){
        listeners.add(listener);
    }

    @Override
    public void addEnrolledStudent(int examId, int studentId){
        Map<Integer, Set<Integer>> enrolledStudents = model.getEnrolledStudents();
        if(!enrolledStudents.containsKey(studentId)){
            enrolledStudents.put(studentId, new HashSet<>());
        }

        for(Integer actualExam : enrolledStudents.get(studentId)){
            model.getConflictMatrix()[actualExam][examId]++;
            model.getConflictMatrix()[examId][actualExam]++;
        }

        enrolledStudents.get(studentId).add(examId);
    }

    @Override
    public int getTimeslotsNumber() {
        return model.getExamMatrix().length;
    }

    @Override
    public int getExamsNumber() {
        return model.getExamMatrix()[0].length;
    }

    @Override
    public int getStudentNumber() {
        return model.getEnrolledStudents().size();
    }

    @Override
    public int getRandomConflictedExam() {
        int index = random.nextInt(conflictedExams.size()); //Seleziono l'indice da prendere
        Iterator<Integer> iter = conflictedExams.iterator(); //Mi creo l'iteratore
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next(); //Ad un certo punto a caso prendo l'esame in questione
    }

    @Override
    public boolean isExamAssigned(int timeSlot, int exam) {
        return model.getExamMatrix()[timeSlot][exam];
    }

    @Override
    public void assignExams(int timeSlot, int exam, boolean value) {
        model.getExamMatrix()[timeSlot][exam] = value;

        if(value)
            examsToAssign.remove(exam);
        else
            examsToAssign.add(exam);

        isScoreValid = false; //Il punteggio andrà ricalcolato
        processConflict(exam, timeSlot, value);
        if(!stampaSoloSoluzioniComplete)print();
    }

    @Override
    public void moveExam(int exam, int timeSlotStart, int timeSlotEnd) {
        model.getExamMatrix()[timeSlotStart][exam] = false;
        processConflict(exam, timeSlotStart, false);
        model.getExamMatrix()[timeSlotEnd][exam] = true;
        processConflict(exam, timeSlotEnd, true);
        isScoreValid = false;
        if(!stampaSoloSoluzioniComplete)print();
    }

    @Override
    public void clearExamsMatrix() {
        for(int i=0; i<model.getExamMatrix().length; i++){
            for(int j=0; j<model.getExamMatrix()[i].length; j++){
                model.getExamMatrix()[i][j] = false;
            }
        }
        for(int i=0; i<getExamsNumber(); i++) examsToAssign.add(i);
        conflictedExams.clear();
    }

    @Override
    public boolean isAssignmentComplete() {
        return examsToAssign.isEmpty();
    }

    @Override
    public boolean isSolutionValid() {
        return conflictedExams.isEmpty() && isAssignmentComplete();
    }

    @Override
    public int getExamTimeslot(int exam) {
        for(int i=0; i<getTimeslotsNumber(); i++){
            if(getAssignmentModel().getExamMatrix()[i][exam]) return i;
        }
        return -1;
    }

    @Override
    public Set<Integer> getTimeslotExams(int timeslot) {
        Set<Integer> result = new HashSet<>();
        for(int i=0; i<getExamsNumber(); i++){
            if(getAssignmentModel().getExamMatrix()[timeslot][i]) result.add(i);
        }
        return result;
    }

    @Override
    public boolean isExamConflicted(int exam) {
        return conflictedExams.contains(exam);
    }

    @Override
    public int getConflictNumber() {
        return conflictedExams.size();
    }

    @Override
    public int estimateNumberOfConflictOfExam(int timeslot, int exam) {
        Set<Integer> conflicts = new HashSet<>();
        Set<Integer> examsOfTimeSlot = getTimeslotExams(timeslot);
        for(Integer index: examsOfTimeSlot){
            if(!index.equals(exam) && areTwoExamsInConflict(exam, index)) conflicts.add(index);
        }
        conflicts.addAll(getConflictedExamsOfTimeSlot(timeslot));

        return conflicts.size();
    }

    @Override
    public int getNumberOfConflictOfExam(int exam) {
        int timeSlot = getExamTimeslot(exam);
        if(timeSlot == -1){
            System.out.println("ERRORE");
        }
        return getConflictedExamsOfTimeSlot(timeSlot).size();
    }

    @Override
    public int[] orderMatrix() {
        return getAssignmentModel().orderConflictMatrix();
    }

    @Override
    public void shift() {
        conflictedExams.clear();
        isScoreValid = false;

        boolean[] last = new boolean[getExamsNumber()];
        //Prima colonna
        for(int i=0; i<getExamsNumber(); i++){
            last[i] = model.getExamMatrix()[getTimeslotsNumber()-1][i];
        }

        //
        for(int i=getTimeslotsNumber()-1; i>0; i--){
            for(int j=0; j<getExamsNumber(); j++){
                model.getExamMatrix()[i][j] = model.getExamMatrix()[i-1][j];
            }
        }

        for(int j=0; j<getExamsNumber(); j++){
            model.getExamMatrix()[0][j] = last[j];
        }

        for(int i = 0; i < model.getExamMatrix().length; i++){
            for(int j = 0; j < model.getExamMatrix()[i].length; j++){
                // i -> Timeslot
                // j -> Esame
                if(model.getExamMatrix()[i][j]){
                    processConflict(j, i, true);
                }
            }
        }

        print();
    }


    private Set<Integer> getConflictedExamsOfTimeSlot(int timeslot){
        Set<Integer> result = new HashSet<>();
        for(Integer index: getTimeslotExams(timeslot)){
            if(isExamConflicted(index)) result.add(index);
        }
        return result;
    }

    private boolean areTwoExamsInConflict(int a, int b){
        return getAssignmentModel().getConflictMatrix()[a][b] != 0;
    }

    private void processConflict(int exam, int timeslot, boolean value){
        boolean atLeastAConflict = false;
        if(value){
            //Confronto il mio nuovo esame con tutti i suoi compagni di timeslot
            for(Integer actualIndex: getTimeslotExams(timeslot)){
                if(!actualIndex.equals(exam) && areTwoExamsInConflict(exam, actualIndex)){
                    conflictedExams.add(actualIndex);
                    atLeastAConflict = true;
                }
            }
            //Se c'è stato almeno un conflitto anche lui è in conflitto
            if(atLeastAConflict) conflictedExams.add(exam);
        }else{
            conflictedExams.remove(exam); //Rimuovo l'esame rimosso dai conflitti
            boolean isInConflictNow;
            //Per ogni esame che ancora è segnalato come in conflitto all'interno del time slot
            for(Integer actualIndex: getConflictedExamsOfTimeSlot(timeslot)){
                isInConflictNow = false;
                //Confronto ogni altro esame in conflitto e cerco di capire se è in conflitto con i rimanenti
                for(Integer compareIndex: getConflictedExamsOfTimeSlot(timeslot)){
                    if(!Objects.equals(actualIndex, compareIndex) && areTwoExamsInConflict(actualIndex, compareIndex)){
                        isInConflictNow = true;
                        break;
                        //Siccome è in conflitto, rimane nel set degli elementi in conflitto
                    }
                }
                //Siccome non è più in conflitto con nessuno, lo rimuovo dal set dei conflitti
                if(!isInConflictNow) conflictedExams.remove(actualIndex);
            }
        }
    }

    @Override
    public double getActualScore() {

        if(!isScoreValid){
            if(isSolutionValid()){
                scoreCache = calculator.getScore(model, 0); //TODO: INSERIRE IL NUMERO DI CONFLITTI ATTUALI
            }else{
                scoreCache = Integer.MAX_VALUE;
            }
            isScoreValid = true;
        }
        return scoreCache;
    }

    @Override
    public double getScoreOfAMove(int exam, int from, int to) {
        return deltaCalculator.getScore(this, exam, from, to);
    }

    @Override
    public void randomSwapTimeSlot() {
        boolean temp;

        int randomTimeSlot = random.nextInt(getTimeslotsNumber());
        int randomDestination = random.nextInt(getTimeslotsNumber());

        for(int i=0; i<getExamsNumber(); i++){
            temp = model.getExamMatrix()[randomTimeSlot][i];
            model.getExamMatrix()[randomTimeSlot][i] = model.getExamMatrix()[randomDestination][i];
            model.getExamMatrix()[randomDestination][i] = temp;
        }

        isScoreValid = false;
    }

    @Override
    public IScoreCalculator getCalculator() {
        return calculator;
    }

    private void callListeners() {
        for(ModelListener listener: listeners){
            listener.onModelChanged();
        }
    }

    public void printOnlyCompleteSolutions(boolean stampaSoloSoluzioniComplete) {
        this.stampaSoloSoluzioniComplete = stampaSoloSoluzioniComplete;
    }

    public void print(){
        long actual = System.currentTimeMillis();
        if(actual - lastPrint > minDelayPrint){
            lastPrint = actual;
            callListeners();
        }
    }

    @Override
    public Set<Integer> getConflicts() {
        return conflictedExams;
    }
}
