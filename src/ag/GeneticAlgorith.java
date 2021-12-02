package src.ag;

import java.util.Scanner;
import java.util.Random;
import java.util.Arrays;
import java.awt.Color;

import src.nn.NeuralNetwork;
import src.utils.Constants;
import src.utils.Logger;
import src.ui.ListPanel;
import src.ui.Table;
import src.App;

public class GeneticAlgorith {

	private int mutationRate;
	private int generation;

	private static Logger logger;

	private static int entrance;
	private static int exit;

    private static NeuralNetwork neuralNetwork;
	private double[][] newGeneration;
	private double[][] population;

	private static ListPanel listPanel;
	private static Table table;
	private static Random rnd;
	private static int roadStones;

	public GeneticAlgorith(Table table, int generations, int populationSize, int mutation) {
        this.neuralNetwork = new NeuralNetwork(Constants.ENTRIES, Constants.BIAS, Constants.HIDDEN_LAYER_SIZE, Constants.OUTPUT_LAYER_SIZE);
		this.roadStones = table.getRoad().size();
		this.listPanel = ListPanel.getInstance();
		this.rnd = new Random();
		this.table = table;

		if(!Constants.DISPLAY) {
			logger = Logger.getInstance();
			logger.initFile("result");
			logger.publishLog("Generations: " + generation + "\nPopulation: " + populationSize + "\nMutation rate: " +mutation+"\n");
		}

		entrance = table.getEntrance();
		exit = table.getExit();
		
		if(generations == 0)
			generations = Integer.MAX_VALUE;
		
		this.generation = generations;
		this.mutationRate = mutation;

        // 6 = 5 entradas + bias
        // 4 neuronios na camada oculta
        // 4 na camada de saída
		population 	  = new double[populationSize][Constants.CHROMOSOME_SIZE + 1];
		newGeneration = new double[populationSize][Constants.CHROMOSOME_SIZE + 1];

		/*
		 *	Generate a random population
		 */
		listPanel.setSize(populationSize);
		for(int i = 0; i < population.length; i++){
			for(int j = 0; j < population[0].length; j++)
				population[i][j] = getRandom();
			listPanel.add(i, population[i][populationSize-1]);
		}

		/*
		 *	Run over the generations
		 */
		int eletism = 0;
		for(int g = 1; g <= generation && quit; g++) {
			fitness(population, g, entrance);
			eletism = eletismBiggest(population);
			transfer(population, eletism, newGeneration, 0);
			transfer(population, eletism, newGeneration, population.length-1);
			transfer(population, eletism, newGeneration, population.length/2);
			crossover(newGeneration, population);
			
			if(rnd.nextInt(100) < mutationRate)
				mutation(newGeneration, mutationRate);

			population = newGeneration;
		}

		if(!Constants.DISPLAY) {
			logger.close();
		}
	}

	/*
	 *	Used to get the most scored chromosome
	 */
	public static int eletismSmallest(double[][] population) {
		int score = population[0].length - 1;
		double smallest = population[0][score];
		int index = 0;

		for(int i = 0; i < population.length; i++) {
			if(smallest > population[i][score]) {
				smallest = population[i][score];
				index = i;
			}
		}

		return index;
	}

	public static int eletismBiggest(double[][] population) {
		int score = population[0].length - 1;
		double biggest = population[0][score];
		int index = 0;

		for(int i = 0; i < population.length - 1; i++) {
			if(biggest < population[i][score]) {
				biggest = population[i][score];
				index = i;
			}
		}

		return index;
	}

	/*
	 *	Use to randomly change a gene on a chromosome
	 */
	public static void mutation(double[][] population, int mutationRate) {
		int i = rnd.nextInt((population.length * mutationRate) / 100);
		int index = 0, prev = 0;
		
		index = eletismSmallest(population);
		do { i--;
			if(index == prev)
				index = 1 + rnd.nextInt(population.length-2);

			for(int j = 0; j < population[0].length/3; j++)
				population
					[index]
					[rnd.nextInt(population[0].length - 2)]
						= getRandom();
			prev = index;
		} while( i > 0 );
	}

	/*
	 *	Encapsulate the walk through the genes of the population
	 */
	private static void fitness(double[][] population, int generation, int entrance) {
		int score = population[0].length - 1;
		for(int i = 0; i < population.length && quit; i++) {
			listPanel.setActive(i, generation);
			population[i][score] = fitness(i, population[i], entrance);
			listPanel.add(i, population[i][score]);
		}
	}

	/*
	 *	Logic to calculate how fitness a chromosome (score)
	 */
	private static double fitness(int id, double[] chromosome, int entrance) {
        neuralNetwork.setNetworkWeight(chromosome);
        table.setNetwork(
        	neuralNetwork.getHiddenWeight(),
        	neuralNetwork.getOutputWeight()
        );

		int coins, pedometer, looper;
		Color color = Color.BLUE;
		Integer pos = entrance;
		String path = "";

		looper = pedometer = coins = 0;
		boolean exit = false;

		double[] entries, output;
		breakpoint();

       	entries = table.lookAround(pos);
        while (pos != null && looper < roadStones) {
			try { Thread.sleep(App.DELAY); } catch (Exception e) {}
			output = neuralNetwork.propagation(entries);
	        int active = activeNeuron(output);

			switch(active) {
				case Constants.NORTH:
					pos = table.moveUpBPos(color, pos, true);
					break;

				case Constants.SOUTH:
					pos = table.moveDownBPos(color, pos, true);
					break;

				case Constants.EAST:
					pos = table.moveRightBPos(color, pos, true);
					break;

				case Constants.WEST:
					pos = table.moveLeftBPos(color, pos, true);
					break;
			}


			if(pos == null) {
				try { Thread.sleep(App.DELAY); } catch (Exception e) {}
				table.clearInputLayer();
				breakpoint();
				continue;
			} 

			if(table.isExit(pos)) exit = true;
			if(table.wasVisited(pos)) pedometer--;
			if(table.collectCoin(pos)) coins++;
			entries = table.lookAround(pos);
			pedometer++;
			looper++;
			breakpoint();
		}

		double fit = fitness(pos, pedometer, coins, exit);
		// if (fit<0) ;
		

		// if(Constants.DISPLAY) {
			table.clear();
		// 	table.setMessage( message(generation, fitness) );
		// } else if (hited)
		// 	logger.publishLog(chromosome, generation, 0, hit);
		

		return fit;
	}


	/*
	 *	Calculates the fitness accordingly of some criterias:
	 *		- Valid moviment: 1 point
	 *		- Reaches some wall: 2 points
	 *		- Falls from table: 10 points
	 */
	// Once quit turn false it closes the program.
	private static boolean quit = true;
	private static double biggest = 0.0;
	private static double fitness(Integer pos, int pedometer, int bag, boolean exit) {
		if(pos == null) return (0.9 * bag) - 3 + pedometer;
		if(exit) {
			if(Constants.LEFT_WHEN_FIND_FIRST)
				quit = !quit;
			double result = 10.0 + (1.0 * bag) + pedometer;
			if(biggest < result) {
				table.setMessage(bag, result);
				biggest = result;
				table.setStop();
			}
			
			breakpoint();
			return result;
		}

		return (0.9 * bag) - (table.manhattan(pos)/100) + pedometer;
	}


	private static int activeNeuron(double[] output) {
        double[] tmp = Arrays.copyOf(output, output.length);
        Arrays.sort(tmp);

		for(int i = 0; i < output.length; i ++)
			if(output[i] == tmp[tmp.length-1])
				return i;

		return -1;
	}

	/*
	 *	Transfer a gene of a population to the new generation
	 */
	private static void transfer(double[][] from, int pos, double[][] to, int ps) {
		to[ps] = Arrays.copyOf(from[pos], from[pos].length);
		// for(int i = 0; i < from[0].length; i++)
		// 	to[ps][i] = from[pos][i];
	}

	/*
	 *	Randomly get two possible fathers and chose the best
	 */
	public static int tournament(double[][] population) {
		int father 	= rnd.nextInt(population.length);
		int dad 	= rnd.nextInt(population.length);
		int score 	= population[0].length - 1;
		if(population[father][score] < population[dad][score]) {
			return father;
		}
		return dad;
	}

	/*
	 *	Cross all population to the next level of generation
	 */
	private static void crossover(double[][] newGeneration, double[][] population) {
		for(int i = 1; i < population.length; i++) {

			int chromosome1 = tournament(population);
			int chromosome2 = tournament(population);

			for(int j=0; j < population[0].length - 1; j++) { 
				newGeneration[i][j] = (population[chromosome1][j] + population[chromosome2][j]) / 2;
			}
		}
	}

	/*
	 *	Generate double random between -1 and 1
	 */
	private static double getRandom() {
		return -1 + (2) * rnd.nextDouble();
	}

	/*
	 *	Just to not let this concatenation on the middle code
	 */
	private static String message(int gene, int generation, int f, int hit) {
		return
			"Reach exit: " + gene + " genes" +
			"\nGeneration: " + generation +
			"\nFitness: " + ((double) f/100) + "\nHIT "+hit;
	}
	private static String message(int g, int f) {
		return
			"Generation: " + g + "\n"+
			"Fitness: " + ( (double) f/100);
	}

	private static void breakpoint() {
		while(App.DEBUG) {}
	}
}