package main.java.assignment.model;

import javafx.util.Pair;
import main.java.assignment.scorecalculator.IScoreCalculator;

import java.util.Set;

public interface IModelWrapper {

    void setListener(ModelListener listener);

    AssignmentModel getAssignmentModel();

    void changeModel(AssignmentModel model);

    void addEnrolledStudent(int examId, int studentId);

    int getTimeslotsNumber();

    int getExamsNumber();

    int getStudentNumber();

    int getRandomConflictedExam();

    boolean isExamAssigned(int timeSlot, int exam);

    void assignExams(int timeSlot, int exam, boolean value);

    void moveExam(int exam, int timeSlotStart, int timeSlotEnd);

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

    void shift();

    void printOnlyCompleteSolutions(boolean stampaSoloSoluzioniComplete);

    void print();

    Set<Integer> getConflicts();

    double getActualScore();

    double getScoreOfAMove(int exam, int from, int to);

    void randomSwapTimeSlot();

    void randomSwapTimeSlot(int i);

    void swapTimeSlot(int t1, int t2);

    double estimateTimeslotSwapValue(int t1, int t2);



    IScoreCalculator getCalculator();

    interface ModelListener{
        void onModelChanged();
    }
}
