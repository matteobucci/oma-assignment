package main.java.assignment.model;

import javafx.util.Pair;
import main.java.assignment.IDeltaScoreCalculator;
import main.java.assignment.IScoreCalculator;

import java.util.*;

public class ModelWrapper {

    private boolean done = false;
    private double scoreCache = 0;
    private boolean isScoreValid = false;

    public interface ModelListener{
        void onModelChanged();
    }

    private List<ModelListener> listeners = new ArrayList<>();
    private AssignmentModel model;
    private List<Pair<Integer, Integer>> conflicts = new ArrayList<>();
    private IScoreCalculator calculator;
    private IDeltaScoreCalculator deltaCalculator;

    public ModelWrapper(int slotsNumber, int examsNumber, IScoreCalculator calculator, IDeltaScoreCalculator deltaCalculator){
        model = new AssignmentModel(slotsNumber,examsNumber);
        this.calculator = calculator;
        this.deltaCalculator = deltaCalculator;
    }

    public AssignmentModel getAssignmentModel() {
        return model;
    }

    public void setListener(ModelListener listener){
        listeners.add(listener);
    }

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

    public int getTimeslotsNumber(){
        return model.getExamMatrix().length;
    }

    public int getExamsNumber(){
        return model.getExamMatrix()[0].length;
    }

    public boolean isExamAssigned(int timeSlot, int exam){
        return model.getExamMatrix()[timeSlot][exam];
    }

    public void assignExams(int timeSlot, int exam, boolean value){
        model.getExamMatrix()[timeSlot][exam] = value;
        if(done){
            callListeners();
        }
        isScoreValid = false;
    }

    private void callListeners() {
        for(ModelListener listener: listeners){
            listener.onModelChanged();
        }
    }

    public int getStudentNumber(){
        return model.getEnrolledStudents().size();
    }

    public boolean doesExamConflict(int exam1, int exam2){
        return model.getConflictMatrix()[exam2][exam1] != 0;
    }

    public Set<Integer> getExamAssignedToTimeSlot(int timeslot){
        Set<Integer> result = new HashSet<>();

        for(int i=0; i<model.getExamMatrix()[timeslot].length; i++){
            if(model.getExamMatrix()[timeslot][i]) result.add(i);
        }

        return result;
    }

    public void clearExamsIfDone(){
       if(done){
           clearExams();
       }
    }

    public void clearExams() {
        for(int i=0; i<model.getExamMatrix().length; i++){
            for(int j=0; j<model.getExamMatrix()[i].length; j++){
                model.getExamMatrix()[i][j] = false;
            }
        }
        conflicts.clear();
    }

    public void setAsDone(){
        done = true;
        callListeners();
    }

    public boolean canIAssignWithoutAnyConflict(int timeslot, int exam){

        for(Integer actualExam : getExamAssignedToTimeSlot(timeslot)){
            if(doesExamConflict(actualExam, exam)){
                return false;
            }
        }
        return true;

    }

    public void addConflict(int timeSlot, int exam) {
        conflicts.add(new Pair<>(timeSlot, exam));
    }

    public Pair<Integer, Integer> getConflict(){
        if(conflicts.isEmpty()){
            return null;
        }else{
            return conflicts.remove(0);
        }
    }


    public int getNumberOfConflicts() {
        return conflicts.size();
    }

    public int howManyConflictAnExamHave(int timeslot, int exam){

        int result = 0;

        for(Integer actualExam : getExamAssignedToTimeSlot(timeslot)){
            if(doesExamConflict(actualExam, exam)){
               result++;
            }
        }
        return result;
    }

    public double getActualScore(){
        if(!isScoreValid){
            scoreCache = calculator.getScore(model, getNumberOfConflicts());
            isScoreValid = true;
        }
        return scoreCache;
    }

    public boolean isDone(){
        return done;
    }

    //TODO: SWAP UTILITIES
    //TODO: CONSTRAINT CHECK WITH EXCEPTION

}
