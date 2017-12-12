package main.java.assignment;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Pair;
import main.java.assignment.model.ModelWrapper;
import main.java.assignment.util.BestRandomFirstSolutionGenerator;
import main.java.assignment.util.IFirstSolutionGenerator;
import main.java.assignment.util.RandomFirstSolutionGenerator;
import main.java.assignment.util.SimpleFirstSolutionGenerator;
import main.java.assignment.view.CanvasViewer;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Main extends Application {

    public static final int CANVAS_WIDTH = 800;
    public static final int CANVAS_HEIGHT = 900;
    private static final int SEC_RUNNING = 60 * 5;

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

        Option exmOption = new Option("e", "exm", true, "exm file");
        exmOption.setRequired(true);
        options.addOption(exmOption);

        Option sloOption = new Option("l", "slo", true, "slo file");
        sloOption.setRequired(true);
        options.addOption(sloOption);

        Option stuOption = new Option("s", "stu", true, "stu file");
        stuOption.setRequired(true);
        options.addOption(stuOption);

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
            if (cmd.hasOption(stuOption.getOpt())) {
                stuPath = cmd.getOptionValue(stuOption.getOpt());
                stuFile = new File(stuPath);
                if (stuFile.isDirectory() || !stuFile.exists()) {
                    System.err.println("Student file not valid");
                    System.exit(1);
                    return;
                }
            }

            //Check output to file
            if (cmd.hasOption(exmOption.getOpt())) {
                exmPath = cmd.getOptionValue(exmOption.getOpt());
                exmFile = new File(exmPath);
                if (exmFile.isDirectory() || !exmFile.exists()) {
                    System.err.println("Exam file not valid");
                    System.exit(1);
                    return;
                }
            }

            //Check output to file
            if (cmd.hasOption(sloOption.getOpt())) {
                sloPath = cmd.getOptionValue(sloOption.getOpt());
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


        LineNumberReader reader  = new LineNumberReader(new FileReader(exmFile));
        while (reader.readLine() != null) {}
        examNumber = reader.getLineNumber();
        reader.close();

        ModelWrapper model = new ModelWrapper(timeSlotNumber, examNumber);

        Scanner scannerStu = new Scanner(stuFile);
        List<Pair<Integer, Integer>> enrollments = new ArrayList<>();
        while(scannerStu.hasNext()){
            int stuId = Integer.parseInt(scannerStu.next().substring(1));
            int exId = scannerStu.nextInt() -1;
            enrollments.add(new Pair<>(stuId, exId));
            model.addEnrolledStudent(exId, stuId);
            if(debug) System.out.println("Lo studente " + stuId + " è coinvolto nell'esame " + exId);
        }
        scannerStu.close();


        IFirstSolutionGenerator generator = new SimpleFirstSolutionGenerator();
        generator.generateFirstSolution(model);

        //Setup stage
        primaryStage.setTitle("OMA Asignment");
        Group root = new Group();
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        CanvasViewer canvasViewer = new CanvasViewer(canvas);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();


        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode()== KeyCode.ENTER) {
                BestRandomFirstSolutionGenerator randomGenerator =  new BestRandomFirstSolutionGenerator();
                randomGenerator.generateFirstSolution(model);
            }
        });

        ModelPresenter presenter = new ModelPresenter(canvasViewer, model);
        ScorePresenter scorePresenter = new ScorePresenter(canvasViewer, model);

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
               // RandomFirstSolutionGenerator randomGenerator =  new RandomFirstSolutionGenerator();
               // randomGenerator.generateFirstSolution(model);
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }



}
