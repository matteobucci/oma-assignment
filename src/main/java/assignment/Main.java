package main.java.assignment;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import main.java.assignment.euristic.IEuristic;
import main.java.assignment.euristic.StandardEuristic;
import main.java.assignment.model.AssignmentModel;
import main.java.assignment.model.IModelWrapper;
import main.java.assignment.model.ModelWrapper;
import main.java.assignment.scorecalculator.DeltaScoreCalculator;
import main.java.assignment.scorecalculator.IDeltaScoreCalculator;
import main.java.assignment.scorecalculator.IScoreCalculator;
import main.java.assignment.scorecalculator.ScoreCalculator;
import main.java.assignment.view.CanvasViewer;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Main extends Application {

    private static final int CANVAS_WIDTH = 800;     //Larghezza finestra
    private static final int CANVAS_HEIGHT = 900;    //Altezza finestra
    private static final int SEC_RUNNING = 60 * 5;  //Tempo di esecuzione

    private static String prefix = null;
    private static String exmPath = null;
    private static String sloPath = null;
    private static String stuPath = null;

    private static File exmFile = null;
    private static File sloFile = null;
    private static File stuFile = null;

    private static boolean running = true;

    public static void main(String[] args) throws IOException {

        //Lettura delle istanze da linea di comando
        Options options = new Options();

        Option insOption = new Option("i", "ist", true, "instance files prefix");
        insOption.setRequired(true);
        options.addOption(insOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {

            cmd = parser.parse(options, args);

            //Mi costruisco i tre file che andrò a leggere
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

        //Faccio partire il programma
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        int timeSlotNumber = 0, examNumber;

        //Lettura TIMESLOT
        Scanner scannerSlo = new Scanner(sloFile);
        while(scannerSlo.hasNextInt()){
            timeSlotNumber = scannerSlo.nextInt();
        }
        scannerSlo.close();

        System.out.println("Numero timeslot: " + timeSlotNumber);

        //Lettura NUMERO ESAMI
        LineNumberReader reader  = new LineNumberReader(new FileReader(exmFile));
        while (reader.readLine() != null) {}
        examNumber = reader.getLineNumber();
        reader.close();

        System.out.println("Numero esami: " + examNumber);

        //Calcolatore di punteggio completo
        IScoreCalculator calculator = new ScoreCalculator();
        //Calcolatore della differenza di punteggi con una mossa
        IDeltaScoreCalculator deltaCalculator = new DeltaScoreCalculator(false); //Il flag fa stampare o meno i risultati del calculator

        //Creo il modello
        IModelWrapper model = new ModelWrapper(timeSlotNumber, examNumber, calculator, deltaCalculator);

        //Lettura ESAMI DI OGNI STUDENTE
        Scanner scannerStu = new Scanner(stuFile);
        while(scannerStu.hasNext()){
            int stuId = Integer.parseInt(scannerStu.next().substring(1));
            int exId = scannerStu.nextInt() -1;
            model.addEnrolledStudent(exId, stuId);
        }
        scannerStu.close();

        //Avvio la finestra
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        setupStage(primaryStage, canvas);
        CanvasViewer canvasViewer = new CanvasViewer(canvas);

        //Visualizzazione a schermo dei risultati
        ModelPresenter presenter = new ModelPresenter(canvasViewer, model);         //Griglia degli esami
        ScorePresenter scorePresenter = new ScorePresenter(canvasViewer, model);    //Testo con punteggi

        //Non stampo il processo di generazione di una soluzione
        model.setStampaSoloSoluzioniComplete(true);

        //Questa classe gestisce il comportamento delle azioni
        IEuristic euristic = new StandardEuristic(model);


        //Thread che porta al blocco della soluzione dopo il timeout
        //TODO: INSERIRE LA LOGICA DI STAMPA DEI RISULTATI
        new Thread(() -> {
            try {
                sleep(SEC_RUNNING * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            running = false;
        }).start();

        //Esecuzione tramite invio dell'euristica
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode()== KeyCode.ENTER) {
                euristic.iterate();
            }
        });

        //Esecuzione automatica dell'euristica.
        new Thread(() -> {
            //Togliere !model.isSolutionValid() se si vuole eseguire indeterminatamente (fino al timeout)
            while(running && !model.isSolutionValid()){
               euristic.iterate();
            }
            model.stampa(); //Stampa la soluzione a schermo
        }).start();

    }

    private void setupStage(Stage primaryStage, Canvas canvas) {
        primaryStage.setTitle("OMA Asignment");
        Group root = new Group();
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


}
