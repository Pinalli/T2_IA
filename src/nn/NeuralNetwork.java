package src.nn;

import java.util.Arrays;

public class NeuralNetwork {

    private Neuron[] hiddenLayer;
    private Neuron[] outputLayer;
    private double[] hidden;
    private double[] output;
    private int      entries;
    private int      bias;

	/*
	 *	Premature optimization is
	 *	the root of all evil.
	 *				-Donald Knuth
	 */
    public NeuralNetwork(int entries, int bias, int hiddenNeurons, int outputNeurons) {
        if(hiddenNeurons <= 0 || outputNeurons <= 0) {
            hiddenNeurons = 8;
            outputNeurons = 4;
        }
        hiddenLayer = new Neuron[hiddenNeurons];
        outputLayer = new Neuron[outputNeurons];
        this.entries = entries;
        this.bias = bias;
    }
    
    public void setNetworkWeight(double[] weights) {
        int w1 = this.entries + this.bias, o = 0, t = w1;
        
        for(int i = 0; i < hiddenLayer.length; i++, o = t, t += w1)
            hiddenLayer[i] = new Neuron(Arrays.copyOfRange(weights, o, t));

        t -= w1;
        t += hiddenLayer.length + this.bias;
        
        for(int i = 0; i < outputLayer.length; i++, o = t, t += hiddenLayer.length + this.bias)
            outputLayer[i] = new Neuron(Arrays.copyOfRange(weights, o, t));
    }
    
    public double[] propagation(double[] x) {
        if(x == null) return null;
        
        this.hidden = new double[hiddenLayer.length];
        this.output = new double[outputLayer.length];
        
        for(int i=0; i < hiddenLayer.length; i++)
            hidden[i] = hiddenLayer[i].f(x);

        for(int i=0; i<outputLayer.length; i++)
            output[i] = outputLayer[i].f(hidden);
        
        return output;
    }

    public double[][] getHiddenWeight() {
        return getNeuronWeight(hiddenLayer);
    }
    public double[][] getOutputWeight() {
        return getNeuronWeight(outputLayer);
    }

    private static double[][] getNeuronWeight(Neuron[] layer) {
        double[][] weights = new double[layer.length][layer[0].length()];
        for(int i = 0; i < layer.length; i++)
            weights[i] = Arrays.copyOf(layer[i].getWeights(), layer[i].length());

        return weights;
    }
    
    public String toString(){
        String msg = "Pesos da rede\n";
        msg = msg + "Camada Oculta\n";
        for(int i=0;i<hiddenLayer.length; i++) {
            msg = msg + "Neuron " + i + ": " + hiddenLayer[i] + "\n";
        }
        msg = msg + "Camada Saida\n";
        for(int i=0;i<outputLayer.length; i++){
            msg = msg + "Neuron " + i + ": " + outputLayer[i] + "\n";
        }
        return msg;
    }
}