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
        for(int i=0; i<getConflictMatrix().length; i++){
            System.arraycopy(this.getConflictMatrix()[i], 0, model.getConflictMatrix()[i], 0, getConflictMatrix().length);
        }

        for(Integer key: getEnrolledStudents().keySet()){
            model.getEnrolledStudents().put(key, this.getEnrolledStudents().get(key));
        }

        return model;
    }

    //ORDINA VETTORE ESAMI IN CONFLITT0
    public int[] orderConflictMatrix (){

        int[] vectorConflictingExams = new int[this.getConflictMatrix().length] ;
        int[] orderedVector = new int[this.conflictMatrix.length];

        for (int i =0 ; i < conflictMatrix.length ; i++){
            for (int j=0 ; j < conflictMatrix.length ; j++){
                if (this.getConflictMatrix()[i][j] != 0)

                    vectorConflictingExams[i]++;
            }
        }

        //STAMPA DI DEBUG
        // for (int i =0 ; i < examMatrix.length ; i++) {
        //    System.out.print(vectorConflictingExams[i] + " ");
        //}



        //Ordino vettore
        orderedVector = ordinaIndici(vectorConflictingExams);

        return orderedVector;
    }

    public int[] ordinaIndici (int[] array){
        int[] vettoreIndiciOrdinato = new int[array.length];
        int max=0;
        int i, j;
        for (j = 0; j< array.length;j++){
            for ( i = 0; i< array.length ; i++) {
                if (array[i] >= array[max] && array[i] >=0)
                    max = i;
            }

            vettoreIndiciOrdinato[j]=max;
            array[max]=-1;
            max = 0;


        }

        return vettoreIndiciOrdinato;
    }


}
