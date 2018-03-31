/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetwork;

import java.io.Serializable;

/**
 *
 * @author user
 */
public class Layer implements Serializable{
    
    /**
     * Массив нейронів шару
     */
    private final Neuron[] neyrons;
    
    /**
     * Нейрон зміщення
     */
    private final double bias;
    
    /**
     * Кількість нейронів в шарі
     */
    private final int neyronCount;
    
    /**
     * Кількість нейронів в попередньому шарі
     */
    private final int prewNeuronCount;

    /**
     * Конструктор шару
     * @param neuCount кількість нейронів в шарі
     * @param prewNeyCount кількість нейронів в попередньому шарі
     */
    public Layer(int neuCount, int prewNeyCount) {
        neyronCount = neuCount;
        prewNeuronCount = prewNeyCount;
        neyrons = new Neuron[neyronCount];
        initiateNeyrons(prewNeyCount);
        bias = Math.random() < 0.5 ? -1.0 : 1.0;
    }
    
    /**
     * Ініціалізація нейронів
     * @param prewNeyCount кількість нейронів в попередньому шарі
     */
    private void initiateNeyrons(int prewNeyCount){
        for (int i = 0; i < neyronCount; i++) {
            //+1 дендрит на нейрон зміщення
            neyrons[i] = new Neuron(prewNeyCount+1);
        }
    }
    
    /**
     * Надсилає сигнали нейронів шару в наступний шар
     * @return сигнали нейронів
     */
    public double[] giveSygnals(){
        double[] sygnals = new double[neyronCount];
        for (int i = 0; i < neyronCount; i++) {
            sygnals[i] = neyrons[i].giveSigmSignal();
        }
        /*int count=0;
        for (double sygnal : sygnals) {
            System.out.println("neyron #"+count+" sygn - "+sygnal);
            count++;
        }*/
        return sygnals;
    }
    
    /**
     * Приймає сигнали від попереднього шару
     * @param sygnals сигнали попереднього шару
     */
    public void acceptSygnals(double[] sygnals){
        //if(sygnals.length!=neyrons.length) throw new NotMatchNeyronSygnCount();
        for (Neuron neyron : neyrons) {
            neyron.takeDendSygnals(sygnals, bias);
        }
    }
    
    /**
     * Приймає помилки
     * @param errs помилки
     */
    public void acceptErrors(double[] errs){
        if(neyrons.length!=errs.length) throw new NotMatchNeyronSygnCount();
        for (int i = 0; i < neyronCount; i++) {
            neyrons[i].takeError(errs[i]);
        }
    }
    
    /**
     * Передає помилки наступному шару
     * @return помилки
     */
    public double[] giveErrors(){
        /*double[][] layErr = new double[neyronCount][];
        for (int i = 0; i < neyronCount; i++) {
            layErr[i]=neyrons[i].giveErrors();
        }
        return layErr;*/
        double[] layErrs = new double[prewNeuronCount];
        for (int i = 0; i < prewNeuronCount; i++) {
            for (int j = 0; j < neyronCount; j++) {
                layErrs[i]+=neyrons[j].giveErrors()[i];
            }
        }
        return layErrs;
    }
    
    /**
     * Виправляє ваги
     * @param learnCoef 
     */
    public void fixWeights(double learnCoef){
        for (Neuron neyron : neyrons) {
            neyron.fixWeight(learnCoef);
        }
    }
    
    /**
     * Метод роздруковує шар
     */
    public void printLayer(){
        System.out.println("neyCount - "+neyronCount);
        System.out.println("bias - "+bias);
        int cnt=0;
        for (Neuron neyron : neyrons) {
            System.out.println("");
            System.out.println("Neyron #"+cnt);
            neyron.printNeyron();
            cnt++;
        }
    }
    
}
