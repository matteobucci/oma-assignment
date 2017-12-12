package main.java.assignment;

import main.java.assignment.model.AssignmentModel;

public class ScoreCalculator implements IScoreCalculator{

    @Override
    public double getScore(AssignmentModel model) {
        boolean[][] examMatrix = model.getExamMatrix();
        int timeSlotNumber = examMatrix.length;
        int examNumber = examMatrix[0].length;
        double somma = 0;

        for(int t = 0; t < timeSlotNumber; t++){
            for(int e = 0; e < examNumber; e ++){
                if(examMatrix[t][e]){
                    for(int i = 1; i < 5 && (t+i < timeSlotNumber); i++){
                        for(int e2 = 0; e2 < examNumber; e2++){
                            if(t+i >= 140){
                                System.out.println("Debug");
                            }
                            try{
                                if(examMatrix[t+i][e2]){
                                    //Conflitto!
                                    somma += model.getConflictMatrix()[e][e2] * Math.pow(2, 5-i) / model.getEnrolledStudents().size();
                                }
                            }catch (Exception ex){
                                System.out.println("Devug");
                            }

                        }
                    }
                }
            }
        }

        return somma;
    }


}
