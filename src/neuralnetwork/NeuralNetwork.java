/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetwork;

import java.io.Serializable;
import java.util.ArrayList;
import javafx.concurrent.Task;

/**
 *
 * @author user
 */
public class NeuralNetwork extends Task<Void> implements Serializable {
    
    private double[][] taskSave;
    
    private double[][] answerSave;
    
    private double learnCoefSave;
    
    private double shurenessSave;
    
    /**
     * Кількість шарів
     */
    private final int layersCount;
    
    /**
     * Кількіст сенсорних дендритів
     */
    private double[] sensors;
    
    /**
     * Массив шарів
     */
    private final Layer[] layers;
    
    //private final Label label;

    /**
     * Конструктор нейронної мережі
     * @param sens кількість сенсорів
     * @param networkMap карта нейронної мережі
     */
    public NeuralNetwork(int sens,int[] networkMap/*, Label lbl*/) {
        sensors=new double[sens];
        layersCount=networkMap.length;
        layers = new Layer[networkMap.length];
        initiateLayers(networkMap);
        //label = lbl;
    }

    /**
     * Ініціює шари
     * @param networkMap карта шарів
     */
    private void initiateLayers(int[] networkMap) {
        layers[0] = new Layer(networkMap[0], sensors.length);//винести окремо змінну?
        for (int i = 1; i < layersCount; i++) {
            layers[i] = new Layer(networkMap[i], networkMap[i-1]);
        }
    }
    
    /**
     * Задає нейонній мережі завдання і отримує відповідь
     * @param task завдання
     * @return відповідь
     */
    public double[] getAnswer(double[] task){
        if(task.length!=sensors.length) throw new NotMatchNeyronSygnCount();
        sensors=task;
        /*for (int i = 0; i < sensors.length; i++) {//винести окремо змінну?
            sensors[i]=task[i];
        }*/
        return getAnswer();
    }
    
    /**
     * Отримує відповідь від нейронної мережі по вже встановленому завданню
     * @return відповідь
     */
    public double[] getAnswer(){
        layers[0].acceptSygnals(sensors);
        for (int i = 1; i < layersCount; i++) {
            layers[i].acceptSygnals(layers[i-1].giveSygnals());
        }
        return layers[layers.length-1].giveSygnals();
    }
    
    /**
     * Тренування нейронної мережі
     * @param task підбірка завдань
     * @param answ підбірка відповідей
     * @param learnCoef коефіціент навчання
     * @param shureness впевненість мережі
     */
    public void trainNeuralNetwork(double[][] task, double[][] answ, double learnCoef, double shureness){
        if(task.length!=answ.length) throw new NotMatchTaskAnswCount();
        boolean glError;
        //int cykles = 0;
        double totalErr;
        do{
            totalErr=0;
            glError=false;
            for (int i = 0; i < task.length; i++) {
                if(task[i].length!=sensors.length) throw new NotMatchTaskAnswCount();
                double[] errors = getErrors(answ[i], getAnswer(task[i]));
                totalErr+=getTotalError(errors);
                if(isError(shureness, errors)){
                    backpropagateAndFix(errors,learnCoef);
                    glError=true;
                }
            }
            System.out.println("total error - "+totalErr);
            updateMessage("number - "+totalErr);
        }while(glError);
    }
    
    /**
     * Встановлює навчальні параметри мережі
     * @param ts завдання
     * @param an відповіді
     * @param sh впевненість
     * @param lc коефіціент навчання
     */
    public void setParameters(double[][] ts, double[][] an, double lc, double sh){
        taskSave = ts;
        answerSave = an;
        shurenessSave = sh;
        learnCoefSave = lc;
    }
    
    /**
     * Починає навчання мережі якщо данні завантажені
     */
    public void startLearn(){
        if(taskSave==null&&answerSave==null)return;// доповнити перевіркою коефіціенту навчання і впевненості !!!!!!!!!!!!!!!!!!!!!!!!!!!!
        trainNeuralNetwork(taskSave, answerSave, learnCoefSave, shurenessSave);
    }
    
    @Override
    protected Void call() {
        startLearn();
        return null;
    }
    /**
     * Повертає помилки
     * @param rigthAnswers правильні відповіді
     * @param answers відповіді мережі
     * @return помилки
     */
    public double[] getErrors(double[] rigthAnswers, double[] answers){
        if(rigthAnswers.length!=answers.length) throw new NotMatchTaskAnswCount();
        double[] errs = new double[rigthAnswers.length];
        for (int i = 0; i < rigthAnswers.length; i++) {
            errs[i] = rigthAnswers[i]-answers[i];
        }
        return errs;
    }
    
    /**
     * Підраховує загальну помилку епохи
     * @param err всі помилки
     * @return загальна помилка
     */
    public double getTotalError(double[] err){
        double totalErr=0;
        for (double d : err) {
            totalErr+=Math.abs(d);
        }
        return totalErr;
    }
    
    /**
     * Визначає чи є помилка у відповіді мережі
     * @param shureness впевненість мережі
     * @param errors помилки
     * @return відповідь чи є помилка
     */
    public boolean isError(double shureness,double[] errors){
        boolean isErr=false;
        for (double error : errors) {
            isErr = isErr ? true : Math.abs(error)>shureness;
        }
        return isErr;
    }
    
    /**
     * Зворотнє розповсюдження помилок
     * @param errs помилки
     */
    private void backPropagateErrors(double[] errs){
        layers[layersCount-1].acceptErrors(errs);
        for (int i = layersCount-1; i > 0; i--) {
            layers[i-1].acceptErrors(layers[i].giveErrors());
        }
    }
    
    /**
     * Виправлення ваги
     * @param learnCoef коефіціент навчання
     */
    private void fixWeights(double learnCoef){
        for (int i = layers.length-1; i > -1; i--) {
            layers[i].fixWeights(learnCoef);
        }
    }
    
    /**
     * Зворотнє розповсюдження помилок і виправлення ваги
     * @param errs помилки
     * @param learnCoef коефіціент навчання
     */
    private void backpropagateAndFix(double[] errs, double learnCoef){
        backPropagateErrors(errs);
        fixWeights(learnCoef);
    }
    
    /**
     * Встановлює мітку яка буде відображати інформацію про загальну помилку
     * @param lbl мітка
     */
    /*public void setLabel(Label lbl){
        label = lbl;
    }*/
    
    /**
     * Роздруковує нейронну мережу
     */
    public void printNN(){
        System.out.println("Layers count - "+layersCount);
        System.out.println("sensors count - "+sensors.length);
        int count = 0;
        for (Layer layer : layers) {
            System.out.println("");
            System.out.println("Layer #"+count);
            layer.printLayer();
            count++;
        }
    }
    
}
