package main.java.assignment.model;

import java.util.HashMap;
import java.util.Set;

public class AssignmentModel {

    private boolean[][] examMatrix;
    private int[][] conflictMatrix;

    private HashMap<Integer, Set<Integer>> enrolledStudents;

    public AssignmentModel(int slotsNumber, int examsNumber){
        examMatrix = new boolean[slotsNumber][examsNumber];
        conflictMatrix = new int[examsNumber][examsNumber];
        enrolledStudents = new HashMap<>();
    }

    public boolean[][] getExamMatrix() {
        return examMatrix;
    }

    public int[][] getConflictMatrix() {
        return conflictMatrix;
    }

    public HashMap<Integer, Set<Integer>> getEnrolledStudents() {
        return enrolledStudents;
    }

}
