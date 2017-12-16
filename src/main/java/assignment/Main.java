package main.java.assignment;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import main.java.assignment.firstsolution.*;
import main.java.assignment.model.AssignmentModel;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.model.ModelWrapper;
import main.java.assignment.solution.RandomSolutionGenerator;
import main.java.assignment.view.CanvasViewer;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Main extends Application {

    public static final int CANVAS_WIDTH = 800;
    public static final int CANVAS_HEIGHT = 900;
    private static final int SEC_RUNNING = 60 * 5;

    static String prefix = null;
    static String exmPath = null;
    static String sloPath = null;
    static String stuPath = null;

    static File exmFile = null;
    static File sloFile = null;
    static File stuFile = null;

    static boolean debug = false;
    static boolean running = true;

    public static void main(String[] args) throws IOException {



        Options options = new Options();

        Option insOption = new Option("i", "ist", true, "istance files prefix");
        insOption.setRequired(true);
        options.addOption(insOption);


        Option debugOption = new Option("d", "debug", false, "debug mode");
        debugOption.setRequired(false);
        options.addOption(debugOption);


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {

            cmd = parser.parse(options, args);

            //Check debug mode
            if (cmd.hasOption(debugOption.getOpt())) {
                debug = true;
                System.out.println("Debug mode enabled");
            }


            //Check output to file
            if (cmd.hasOption(insOption.getOpt())) {
                prefix = cmd.getOptionValue(insOption.getOpt());
                stuPath = prefix + ".stu";
                exmPath = prefix + ".exm";
                sloPath = prefix + ".slo";

                stuFile = new File(stuPath);
                if (stuFile.isDirectory() || !stuFile.exists()) {
                    System.err.println("Student file not valid");
                    System.exit(1);
                    return;
                }

                exmFile = new File(exmPath);
                if (exmFile.isDirectory() || !exmFile.exists()) {
                    System.err.println("Exam file not valid");
                    System.exit(1);
                    return;
                }

                sloFile = new File(sloPath);
                if (sloFile.isDirectory() || !sloFile.exists()) {
                    System.err.println("Slot file not valid");
                    System.exit(1);
                    return;
                }
            }

        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("omaAssignment", options);
            System.exit(1);
            return;
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {


        int timeSlotNumber = 0;
        int examNumber = 0;

        Scanner scannerSlo = new Scanner(sloFile);
        while(scannerSlo.hasNextInt()){
            timeSlotNumber = scannerSlo.nextInt();
        }
        scannerSlo.close();

        System.out.println("Numero timeslot: " + timeSlotNumber);


        LineNumberReader reader  = new LineNumberReader(new FileReader(exmFile));
        while (reader.readLine() != null) {}
        examNumber = reader.getLineNumber();
        reader.close();

        System.out.println("Numero esami: " + examNumber);

        IScoreCalculator calculator = new ScoreCalculator();
        IDeltaScoreCalculator deltaCalculator = new IDeltaScoreCalculator() {
            @Override
            public double getSwapCalculator(AssignmentModel model, int examIndex, int fromTimeSlot, int toTimeSlot) {
                return 0;
            }
        };


        IModelWrapper model = new ModelWrapper(timeSlotNumber, examNumber, calculator, deltaCalculator);

        Scanner scannerStu = new Scanner(stuFile);
        while(scannerStu.hasNext()){
            int stuId = Integer.parseInt(scannerStu.next().substring(1));
            int exId = scannerStu.nextInt() -1;
            model.addEnrolledStudent(exId, stuId);
            if(debug) System.out.println("Lo studente " + stuId + " è coinvolto nell'esame " + exId);
        }
        scannerStu.close();

        //Setup stage
        primaryStage.setTitle("OMA Asignment");
        Group root = new Group();
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        CanvasViewer canvasViewer = new CanvasViewer(canvas);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        ModelPresenter presenter = new ModelPresenter(canvasViewer, model);
    //    ScorePresenter scorePresenter = new ScorePresenter(canvasViewer, model);

        model.setStampaSoloSoluzioniComplete(true);
        IEuristic euristic = new IEuristic(new AlbertoSolutionGenerator(model), new RandomSolutionGenerator(model)) {

            int passi = 0;

            @Override
            public void iterate() {
                if(model.isAssignmentComplete()){
                    if(model.isSolutionValid()){
                        model.stampa();
                        System.out.println("Punteggio raggiunto: " + model.getActualScore());
                        firstSolutionGenerator.generateFirstSolution();
                    }else{
                        solutionGenerator.iterate();
                        passi++;

                        if(passi % 2000 == 0){
                            firstSolutionGenerator.generateFirstSolution();
                            passi = 0;
                        }
                    }
                }else{
                    System.out.println("Soluzione non completa");
                    firstSolutionGenerator.generateFirstSolution();
                }
            }
        };


        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode()== KeyCode.ENTER) {
                //euristic.iterate();
            }
        });


        new Thread(() -> {
            try {
                sleep(SEC_RUNNING * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            running = false;
        }).start();


        new Thread(() -> {
            while(running){
               euristic.iterate();
            }
        }).start();

    }


}
