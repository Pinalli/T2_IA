package src.nn;

import java.util.Arrays;

public class Neuron {
	
    private double[] weights;	//Número de pesos = número de entradas + 1
    private int function;   	//0: é a default -> logistica
    
    public Neuron(double[] weights){
        setWeights(weights);
        setFunction(1);
    }


    public void setWeights(double[] weights) {
        this.weights = weights;
    }
    
    public void setFunction(int function) {
        this.function = function;
    }

    public double f(double[] x) {
        double v = 0;
        int i;

        for(i=0; i < x.length; i++) {
            v = v + weights[i] * x[i];
        }

        v = v + weights[i];  //bias
        
        switch(this.function){
            case 1: return hyperbolicTangent(v);
            default: return logistic(v);
        }
    }
    
    public double logistic(double v) {
        return 1 / (1 + Math.exp(-v));
    }
    
    public double hyperbolicTangent(double v) {
        return Math.tanh(v);
    }
    


    public double[] getWeights() {
        return weights;
    }
    public int length() {
        return weights.length;
    }

    
    public String toString() {
        return Arrays.toString(weights);
    }
}