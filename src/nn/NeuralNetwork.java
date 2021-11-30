package src.nn;

import java.util.Arrays;

public class NeuralNetwork {

    private Neuron[] hiddenLayer;
    private Neuron[] outputLayer;
    private double[] hidden;
    private double[] output;
    private int      entries;

	/*
	 *	Premature optimization is
	 *	the root of all evil.
	 *				-Donald Knuth
	 */
    public NeuralNetwork(int entries, int hiddenNeurons, int outputNeurons) {
        if(hiddenNeurons <= 0 || outputNeurons <= 0) {
            hiddenNeurons = 8;
            outputNeurons = 4;
        }
        hiddenLayer = new Neuron[hiddenNeurons];
        outputLayer = new Neuron[outputNeurons];
        this.entries = entries + 1; // + bias
    }
    
    public void setNetworkWeight(double[] weights) {
        int o = 0, t = this.entries;
        
        for(int i = 0; i < hiddenLayer.length; i++, o = t, t += this.entries)
            hiddenLayer[i] = new Neuron(Arrays.copyOfRange(weights, o, t));
        t -= this.entries;
        t += hiddenLayer.length + 1;
        for(int i = 0; i < outputLayer.length; i++, o = t, t += hiddenLayer.length + 1)
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

    public Neuron[] getHiddenLayer() { return hiddenLayer; }
    public Neuron[] getOutputLayer() { return outputLayer; }
    
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