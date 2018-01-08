package main.java.assignment;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import main.java.assignment.heuristic.*;
import main.java.assignment.model.CachedModelWrapper;
import main.java.assignment.scorecalculator.DeltaScoreCalculator;
import main.java.assignment.scorecalculator.ScoreCalculator;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Main {

    private static final int CANVAS_WIDTH = 800;     //Larghezza finestra
    private static final int CANVAS_HEIGHT = 1040;    //Altezza finestra
    private static int SEC_RUNNING = 60 * 5;  //Tempo di esecuzione

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


        Option timeOption = new Option("t", "time", true, "duration time of the solver (seconds)");
        timeOption.setType(Integer.class);
        timeOption.setRequired(true);
        options.addOption(timeOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {

            cmd = parser.parse(options, args);

            for(String string: cmd.getArgList()){
                System.out.println("Parametro: " + string);
            }

            if(cmd.getArgList().isEmpty()){
                System.err.println("Occorre inserire come parametro il prefisso dell'istanza da utilizzare");
                System.exit(1);
                return;
            }

            //Mi costruisco i tre file che andrò a leggere
            if (cmd.hasOption(timeOption.getOpt())) {
                SEC_RUNNING = Integer.parseInt(cmd.getOptionValue(timeOption.getOpt()));

                prefix = cmd.getArgList().get(0);
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


        int cores = Runtime.getRuntime().availableProcessors();
        CachedModelWrapper[] models = new CachedModelWrapper[cores];
        EuristicThread[] threads = new EuristicThread[cores];

        System.out.println("Numero di thread generati: " + cores);

        for(int i=0; i<cores; i++){
            models[i] = new CachedModelWrapper(timeSlotNumber, examNumber, new ScoreCalculator(), new DeltaScoreCalculator());
        }


        //Lettura ESAMI DI OGNI STUDENTE
        Scanner scannerStu = new Scanner(stuFile);
        while(scannerStu.hasNext()){
            int stuId = Integer.parseInt(scannerStu.next().substring(1));
            int exId = scannerStu.nextInt() -1;
            for(int i=0; i<cores; i++){
                models[i].addEnrolledStudent(exId, stuId);
            }
        }
        scannerStu.close();


        for(int i=0; i<cores; i++){
            IHeuristic euristic = new MultipleSolutionHeuristic(models[i], SEC_RUNNING);
            threads[i] = new EuristicThread(euristic);
            threads[i].start();
            System.out.println("Fatto partire il tread numero " + i);
        }


        //Thread che porta al blocco della soluzione dopo il timeout
        int finalCores = cores;
        new Thread(() -> {
            try {
                sleep(SEC_RUNNING * 1000);
                int bestThread = 0;
                double bestScore = Double.MAX_VALUE;

                for(int i = 0; i< finalCores; i++){
                    models[i].changeModel( threads[i].getBestSolution().clone());
                    System.out.println("Thread " + i + ": trovato un modello con punteggio " + models[i].getActualScore());
                    if(models[i].getActualScore() < bestScore) {
                        bestThread = i;
                        bestScore = models[i].getActualScore();
                    }
                }

                try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(prefix+".sol")))){
                    models[bestThread].changeModel(models[bestThread].getCalculator().getBestUntilNow());
                    for(int i=0; i<models[bestThread].getExamsNumber(); i++){
                        String stringa = (i+1) + " " + (models[bestThread].getExamTimeslot(i)+1) + "\n";
                        writer.write(stringa);
                    }

                    writer.flush();
                    writer.close();

                    System.out.println("Terminata la scrittura del modello");
                    System.out.println("La soluzione ha un punteggio di " + models[bestThread].getActualScore());
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }



                System.exit(0);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            running = false;
        }).start();

    }



        /*

        //Avvio la finestra
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        setupStage(primaryStage, canvas);
        CanvasViewer canvasViewer = new CanvasViewer(canvas);

        //Visualizzazione a schermo dei risultati
        ModelPresenter presenter = new ModelPresenter(canvasViewer, model);         //Griglia degli esami
        ScorePresenter scorePresenter = new ScorePresenter(canvasViewer, model);    //Testo con punteggio


        //Esecuzione automatica dell'euristica.
        new Thread(() -> {
            while(running && threadActive){
               euristic.iterate();
               if(!running){

               }
            }
        }).start();

        */


}
