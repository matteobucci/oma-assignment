package main.java.assignment.model;

import javafx.util.Pair;

import java.util.*;

public class ModelWrapper {

    private boolean done = false;

    private int numberOfConflict = 0;

    public interface ModelListener{
        void onModelChanged();
    }

    private List<ModelListener> listeners = new ArrayList<>();
    private AssignmentModel model;

    public ModelWrapper(int slotsNumber, int examsNumber){
        model = new AssignmentModel(slotsNumber,examsNumber);
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
        numberOfConflict = 0;
        for(int i=0; i<model.getExamMatrix().length; i++){
            for(int j=0; j<model.getExamMatrix()[i].length; j++){
                model.getExamMatrix()[i][j] = false;
            }
        }
    }

    public void setAsDone(){
        done = true;
        for(ModelListener listener: listeners){
            listener.onModelChanged();
        }
    }

    public boolean canIAssignExamHere(int timeslot, int exam){

        for(Integer actualExam : getExamAssignedToTimeSlot(timeslot)){
            if(doesExamConflict(actualExam, exam)){
                return false;
            }
        }
        return true;

    }

    public void addConflict() {
        numberOfConflict++;
    }

    public double getNumberOfConflicts() {
        return numberOfConflict;
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

}
