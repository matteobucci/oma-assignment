package main.java.assignment;

public class PassiManager {

    private int passiMiglioramento = 0;
    private int passiGenerazioneSoluzione = 0;

    private long tempoSoluzioneFeasable;
    private long tempoIniziale;

    private double punteggioIniziale = 0;

    public PassiManager() {
        reset();
    }

    public void reset(){
        passiMiglioramento = 0;
        passiGenerazioneSoluzione = 0;
        tempoIniziale = System.currentTimeMillis();
        tempoSoluzioneFeasable = 0;
    }

    public void passoGenerazioneSoluzione(){
        passiGenerazioneSoluzione++;
    }

    public void passoMiglioramento(){
        passiMiglioramento++;
    }

    public void printInfoGenerazioneSoluzione(){

        if(passiGenerazioneSoluzione != 0){
            long currentTime = System.currentTimeMillis();
            long delta = currentTime - tempoIniziale;

            System.out.println("Tempo dall'inizio; " + delta);
            System.out.println("Tempo medio per passo: " +  delta / passiGenerazioneSoluzione);
        }else{
            System.out.println("Controllare che sia iniziata la generazione di una soluzione");
        }

    }

    public void printInfoMiglioramentoSoluzione(){

        if(passiMiglioramento != 0){
            long currentTime = System.currentTimeMillis();
            long delta = currentTime - tempoSoluzioneFeasable;

            System.out.println("Tempo dall'inizio; " + delta);
            System.out.println("Tempo medio per passo: " +  delta / passiMiglioramento);
        }else{
            System.out.println("Controllare che il miglioramento sia iniziato");
        }

    }

    public void segnalaSoluzioneFeasable(double punteggio){
        tempoSoluzioneFeasable = System.currentTimeMillis();
        punteggioIniziale = 0;
    }

    public void printDettagliSoluzioneCompleta(double punteggioFinale){

        System.out.println("########### SOLUZIONE CORRENTE ###########################");
        System.out.println("Passi generazione soluzione: " + passiGenerazioneSoluzione);

        if(passiMiglioramento == 0){
            System.out.println("La soluzione deve ancora essere feasable probabilmente");
        }else{
            System.out.println("La soluzione è feasable.");
            System.out.println("Passi miglioramento soluzione: " + passiMiglioramento);
            System.out.println("Puntegggio attuale: " + punteggioFinale);
            System.out.println("Delta miglioramento: " + (punteggioFinale- punteggioIniziale));
            System.out.println("Miglioramento medio per passo: " + (passiMiglioramento / (punteggioFinale- punteggioIniziale)));
        }

        System.out.println("##########################################################");

    }

    public int getPassiMiglioramento() {
        return passiMiglioramento;
    }

    public int getPassiGenerazioneSoluzione() {
        return passiGenerazioneSoluzione;
    }
}
