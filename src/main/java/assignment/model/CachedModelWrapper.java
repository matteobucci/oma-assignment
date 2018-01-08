package main.java.assignment.model;

import main.java.assignment.scorecalculator.IDeltaScoreCalculator;
import main.java.assignment.scorecalculator.IScoreCalculator;

import java.util.*;

public class CachedModelWrapper implements IModelWrapper {

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

    private Set[] timeslotCache;
    private boolean[] cacheOfTimeSlotValid;
    private Set[] conflictCache;
    private boolean[] cacheOfConflictValid;

    public CachedModelWrapper(int slotsNumber, int examsNumber, IScoreCalculator calculator, IDeltaScoreCalculator deltaCalculator){
        model = new AssignmentModel(slotsNumber,examsNumber);
        this.calculator = calculator;
        this.deltaCalculator = deltaCalculator;
        timeslotCache = new Set[slotsNumber];
        cacheOfTimeSlotValid = new boolean[slotsNumber];
        conflictCache = new Set[slotsNumber];
        cacheOfConflictValid = new boolean[slotsNumber];


        clearExamsMatrix();
    }

    @Override
    public AssignmentModel getAssignmentModel() {
        return model;
    }

    @Override
    public void changeModel(AssignmentModel model) {
        conflictedExams.clear();
        this.model = model;

        for(int i=0; i<getTimeslotsNumber(); i++){
            cacheOfTimeSlotValid[i] = false;
            cacheOfConflictValid[i] = false;
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
    public double estimateTimeslotSwapValue(int t1, int t2) {

        Set<Integer> t1Exams = getTimeslotExams(t1);
        Set<Integer> t2Exams = getTimeslotExams(t2);

        int dist;

        double punteggioT1Init = 0;
        double punteggioT1End = 0;
        double punteggioT2Init = 0;
        double punteggioT2End = 0;

        //Situazione attuale T1
        int startT1 = t1-5;
        if(startT1 < 0) startT1 = 0;
        for(int i=startT1; i<=t1+5 && i < getTimeslotsNumber(); i++){
            if(i == t1) continue; //Non considero me stesso
            dist = Math.abs(t1-i);
            Set<Integer> tempSet = getTimeslotExams(i);
            for(int ex1 : tempSet){
                for(int ex2 : t1Exams){
                    punteggioT1Init += (getAssignmentModel().getConflictMatrix()[ex1][ex2] * Math.pow(2, 5-dist)) / model.getEnrolledStudents().size();
                }
            }
        }

        //System.out.println("T1 start: " + punteggioT1Init);

        //Situazione finale T1
        startT1 = t2-5;
        if(startT1 < 0) startT1 = 0;
        for(int i=startT1; i<=t2+5 && i< getTimeslotsNumber(); i++){
            if(i == t2) continue; //Non considero me stesso
            dist = Math.abs(t2-i);
            Set<Integer> tempSet = getTimeslotExams((i==t1)?t2:i);
            for(int ex1 : tempSet){
                for(int ex2 : t1Exams){
                    punteggioT1End += (getAssignmentModel().getConflictMatrix()[ex1][ex2] * Math.pow(2, 5-dist)) / model.getEnrolledStudents().size();
                }
            }
        }

        //System.out.println("T1 end: " + punteggioT1End);

        //Sistuazione attuale T2
        int startT2 = t2-5;
        if(startT2 < 0) startT2 = 0;
        for(int i=startT2; i<=t2+5 && i< getTimeslotsNumber(); i++){
            if(i == t2) continue;
            dist = Math.abs(t2-i);
            Set<Integer> tempSet = getTimeslotExams(i);
            for(int ex1 : tempSet){
                for(int ex2 : t2Exams){
                    punteggioT2Init += (getAssignmentModel().getConflictMatrix()[ex1][ex2] * Math.pow(2, 5-dist)) / model.getEnrolledStudents().size();
                }
            }
        }

       // System.out.println("T2 start: " + punteggioT2Init);

        //Situazione finale T2
        startT2 = t1-5;
        if(startT2 < 0) startT2 = 0;
        for(int i=startT2; i<=t1+5 && i< getTimeslotsNumber(); i++){
            if(i == t1) continue; //Non considero me stesso
            dist = Math.abs(t1-i);
            Set<Integer> tempSet = getTimeslotExams((i==t2)?t1:i);
            for(int ex1 : tempSet){
                for(int ex2 : t2Exams){
                    punteggioT2End += (getAssignmentModel().getConflictMatrix()[ex1][ex2] * Math.pow(2, 5-dist)) / model.getEnrolledStudents().size();
                }
            }
        }

      //  System.out.println("T2 end: " + punteggioT2End);

        return punteggioT1Init + punteggioT2Init - punteggioT1End - punteggioT2End;
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

        if(value){
            examsToAssign.remove(exam);
            timeslotCache[timeSlot].add(exam);
        }
        else{
            timeslotCache[timeSlot].remove(exam);
            examsToAssign.add(exam);
        }

        isScoreValid = false; //Il punteggio andrà ricalcolato
        processConflict(exam, timeSlot, value);
        cacheOfConflictValid[timeSlot] = false;
        if(!stampaSoloSoluzioniComplete)print();
    }

    @Override
    public void moveExam(int exam, int timeSlotStart, int timeSlotEnd) {
        model.getExamMatrix()[timeSlotStart][exam] = false;
        processConflict(exam, timeSlotStart, false);
        timeslotCache[timeSlotStart].remove(exam);
        model.getExamMatrix()[timeSlotEnd][exam] = true;
        processConflict(exam, timeSlotEnd, true);
        timeslotCache[timeSlotEnd].add(exam);
        cacheOfConflictValid[timeSlotStart] = false;
        cacheOfConflictValid[timeSlotEnd] = false;
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
        for(int i=0; i<getTimeslotsNumber(); i++){
            cacheOfTimeSlotValid[i] = false;
            cacheOfConflictValid[i] = false;
        }
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
        if(!cacheOfTimeSlotValid[timeslot]){
            Set<Integer> result = new HashSet<>();
            for(int i=0; i<getExamsNumber(); i++){
                if(getAssignmentModel().getExamMatrix()[timeslot][i]) result.add(i);
            }
            timeslotCache[timeslot] = result;
            cacheOfTimeSlotValid[timeslot] = true;
        }
        return timeslotCache[timeslot];
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
        int newConflicts = 0;
        Set<Integer> examsOfTimeSlot = getTimeslotExams(timeslot);
        for(Integer index: examsOfTimeSlot){
            if(!index.equals(exam) && areTwoExamsInConflict(exam, index)) newConflicts++;
        }
        return getConflictedExamsOfTimeSlot(timeslot).size() + newConflicts;
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
            if(atLeastAConflict){
                conflictedExams.add(exam);
            }
        }else{
            conflictedExams.remove(exam);
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
                if(!isInConflictNow){
                    conflictedExams.remove(actualIndex);
                }
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
        randomSwapTimeSlot(random.nextInt(getTimeslotsNumber()));
    }

    @Override
    public void randomSwapTimeSlot(int ts) {


        int randomDestination = random.nextInt(getTimeslotsNumber());
        swapTimeSlot(ts, randomDestination);
    }

    @Override
    public void swapTimeSlot(int t1, int t2) {

        boolean temp;
        for(int i=0; i<getExamsNumber(); i++){
            temp = model.getExamMatrix()[t1][i];
            model.getExamMatrix()[t1][i] = model.getExamMatrix()[t2][i];
            model.getExamMatrix()[t2][i] = temp;
        }

        Set<Integer> tempCache = timeslotCache[t1];
        timeslotCache[t1] = timeslotCache[t2];
        timeslotCache[t2] = tempCache;

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
