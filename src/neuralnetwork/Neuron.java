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
public class Neuron implements Serializable {
    
    /**
     * Зважена сумма сигналів з дендритів.
     */
    private double e;
    
    /**
     * Вага дендритів
     */
    private final double[] dendritWeights;
    
    /**
     * Кількість дендритів
     */
    private final int dendritCount;
    
    /**
     * Помилка нейрону
     */
    private double error;
    
    /**
     * Збережені сигмоїдні сигнали
     */
    private double[] sigmIn;
    
    /**
     * Вхідний сигнал нейрону зміщення
     */
    private double biasIn;

    /**
     * Створює нейрон
     * @param dendCoun кількість дендритів з врахуванням нейрону зміщення
     */
    public Neuron(int dendCoun) {
        e=0.0;
        dendritCount = dendCoun;
        dendritWeights = new double[dendritCount];
        error=0.0;
        initiateDenritWeights();
    }
    
    /**
     * Ініціює вагу нейронів
     */
    private void initiateDenritWeights(){
        for (int i = 0; i < dendritWeights.length; i++) {
            //dendritWeights[i] = Math.random()<0.5 ? Math.random()*0.3+0.6 : -Math.random()*0.3-0.6;
            dendritWeights[i] = Math.random()<0.5 ? Math.random()*0.3+(15/dendritCount) : -Math.random()*0.3-(15/dendritCount);
        }
    }
    
    /**
     * Отримує сигнали на дендрити з нейронів попереднього шару
     * @param dendSygn сигмоїдні сигнали на дендрити
     * @param bias сигнал нейрону зміщення
     */
    public void takeDendSygnals(double[] dendSygn,double bias){
        //+1 тому що сигнали дендритів надсилаються без врахування нейрону зміщення
        if(dendSygn.length+1!=dendritCount)throw new NotMatchSygnDendCount();
        sigmIn=dendSygn;
        biasIn=bias;
        e=0.0;
        //важливо щоб перебирались вхідні сигнали
        for (int i = 0; i < dendSygn.length; i++) {
            e+=dendSygn[i]*dendritWeights[i];
        }
        e+=bias*dendritWeights[dendritCount-1];
    }
    
    /**
     * Сигмоїдний сигнал
     * @return сигмоїду зваженої сумми сигналів з дендритів
     */
    public double giveSigmSignal(){
        return 1/(1+Math.exp(-e));
    }
    
    /**
     * Приймає помилку
     * @param err 
     */
    public void takeError(double err){
        error=err;
    }
    
    /**
     * Роздає помилки
     * @return помилки
     */
    public double[] giveErrors(){//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //-1 нейрону зміщення не потрібна помилка
        double[] errors = new double[dendritCount-1];
        //-1 нейрону зміщення не потрібна помилка
        for (int i = 0; i < dendritCount-1; i++) {
            errors[i] = error*dendritWeights[i];
        }
        return errors;
    }
    
    /**
     * Виправляє вагу
     * @param learnCoef 
     */
    public void fixWeight(double learnCoef){
        //-1 тому що нейрон зміщення виправляється окремо
        for (int i = 0; i < dendritCount-1; i++) {
            dendritWeights[i]+=sigmIn[i]*learnCoef*giveSigmSignal()*(1-giveSigmSignal())*error;
        }
        dendritWeights[dendritCount-1]+=biasIn*learnCoef*giveSigmSignal()*(1-giveSigmSignal())*error;
    }
    
    
    /**
     * Роздруковує стан дендритів нейрону
     */
    public void printNeyron(){
        System.out.println("Dendrit count - "+dendritCount);
        int cnt = 0;
        for (double dendritWeight : dendritWeights) {
            System.out.println("dnd #"+cnt+" - "+dendritWeight);
            cnt++;
        }
    }
}
