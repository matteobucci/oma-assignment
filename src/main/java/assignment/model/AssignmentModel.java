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

    public AssignmentModel clone() {
        AssignmentModel model = new AssignmentModel(this.examMatrix.length, this.examMatrix[0].length);
        for(int i=0; i<getExamMatrix().length; i++){
            System.arraycopy(this.getExamMatrix()[i], 0, model.getExamMatrix()[i], 0, getExamMatrix()[0].length);
        }
        for(int i=0; i<getExamMatrix().length; i++){
            System.arraycopy(this.getConflictMatrix()[i], 0, model.getConflictMatrix()[i], 0, getExamMatrix().length);
        }

        for(Integer key: getEnrolledStudents().keySet()){
            model.getEnrolledStudents().put(key, this.getEnrolledStudents().get(key));
        }

        return model;
    }

}
