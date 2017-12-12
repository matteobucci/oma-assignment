package main.java.assignment.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.java.assignment.model.ModelWrapper;


public class CanvasViewer implements ModelViewer, ModelStatsViewer{

    private Canvas canvas;
    private GraphicsContext gc;


    public CanvasViewer(Canvas canvas){
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.RED);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
        gc.setFont(Font.getDefault());
    }

    @Override
    public void printModel(ModelWrapper model) {
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        drawExams(model);
        drawBaseMatrix(model);
    }

    private void drawBaseMatrix(ModelWrapper wrapper){
        int timeSlotNumber = wrapper.getTimeslotsNumber();
        int examNumber = wrapper.getExamsNumber();


        int widthTS =  (int) canvas.getWidth() / timeSlotNumber;
        int heigtEX =  (int) (canvas.getHeight() - 30) / examNumber;

        for(int i =0; i<=timeSlotNumber; i++){
            gc.strokeLine(widthTS * (i), 0, widthTS * (i), heigtEX*examNumber);
        }

        for(int i =0; i<=examNumber; i++){
            gc.strokeLine(0, heigtEX * (i), widthTS*timeSlotNumber, heigtEX * (i));
        }

    }

    private void drawExams(ModelWrapper wrapper){
        int timeSlotNumber = wrapper.getTimeslotsNumber();
        int examNumber = wrapper.getExamsNumber();


        int widthTS =  (int) canvas.getWidth() / timeSlotNumber;
        int heigtEX =  (int) canvas.getHeight() / examNumber;

        for(int i =0; i<timeSlotNumber; i++){
            for(int j =0; j<examNumber; j++){
                if(wrapper.isExamAssigned(i, j)) gc.fillRect(widthTS* i, heigtEX * j, widthTS, heigtEX);
            }
        }


    }

    @Override
    public void printConflicts(double conflicts) {
        gc.fillText(String.valueOf("Conflicts: " + conflicts), 0, canvas.getHeight() -30);
    }

    @Override
    public void printdScore(double score) {
        gc.fillText(String.valueOf("Score: " + score), canvas.getWidth()/2, canvas.getHeight() - 30);
    }
}
