package main.java.assignment.model;

import javafx.util.Pair;

import java.util.Set;

public interface IModelWrapper {

    void setListener(ModelListener listener);

    AssignmentModel getAssignmentModel();

    void addEnrolledStudent(int examId, int studentId);

    int getTimeslotsNumber();

    int getExamsNumber();

    int getStudentNumber();

    int getRandomConflictedExam();

    boolean isExamAssigned(int timeSlot, int exam);

    void assignExams(int timeSlot, int exam, boolean value);

    void clearExamsMatrix();

    boolean isAssignmentComplete();

    boolean isSolutionValid();

    int getExamTimeslot(int exam);

    Set<Integer> getTimeslotExams(int timeslot);

    boolean isExamConflicted(int exam);

    int getConflictNumber();

    int estimateNumberOfConflictOfExam(int timeslot, int exam);

    int getNumberOfConflictOfExam(int exam);

    int[] orderMatrix();

    public void setStampaSoloSoluzioniComplete(boolean stampaSoloSoluzioniComplete);

    public void stampa();

    //Metodi da implementare sicuramente
    double getActualScore();

    public interface ModelListener{
        void onModelChanged();
    }
}
