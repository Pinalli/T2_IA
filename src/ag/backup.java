// package src.ag;

// import java.util.Scanner;
// import java.util.Random;
// import java.util.Arrays;
// import java.awt.Color;

// import src.nn.NeuralNetwork;
// import src.utils.Constants;
// import src.utils.Logger;
// import src.ui.Table;
// import src.App;

// public class GeneticAlgorith {

// 	private int mutationRate;
// 	private int generation;

// 	private static Logger logger;

// 	private static int entrance;
// 	private static int exit;

//     private static NeuralNetwork neuralNetwork;
// 	private double[][] newGeneration;
// 	private double[][] population;

// 	private static Table table;
// 	private static Random rnd;
// 	private int roadStones;

// 	public GeneticAlgorith(Table table, int generations, int populationSize, int mutation) {
//         this.neuralNetwork = new NeuralNetwork(Constants.ENTRIES, Constants.HIDDEN_LAYER_SIZE, Constants.OUTPUT_LAYER_SIZE);
// 		this.roadStones = table.getRoad().size();
// 		this.rnd = new Random();
// 		this.table = table;

// 		if(!Constants.DISPLAY) {
// 			logger = Logger.getInstance();
// 			logger.initFile("result");
// 			logger.publishLog("Generations: " + generation + "\nPopulation: " + populationSize + "\nMutation rate: " +mutation+"\n");
// 		}

// 		entrance = table.getEntrance();
// 		exit = table.getExit();
		
// 		if(generations == 0)
// 			generations = 1000000000;
		
// 		this.generation = generations;
// 		this.mutationRate = mutation;

//         // 6 = 5 entradas + bias
//         // 4 neuronios na camada oculta
//         // 4 na camada de saída
// 		population 	  = new double[populationSize][Constants.CHROMOSOME_SIZE];
// 		newGeneration = new double[populationSize][Constants.CHROMOSOME_SIZE];

// 		/*
// 		 *	Generate a random population
// 		 */
// 		for(int i = 0; i < population.length; i++)
// 			for(int j = 0; j < population[0].length; j++)
// 				population[i][j] = getRandom();

// 		/*
// 		 *	Run over the generations
// 		 */
// 		for(int g = 1; g <= generation && quit; g++) {
// 			fitness(population, g, entrance, exit);
// 			transfer(population, eletism(population), newGeneration, 0);
// 			crossover(newGeneration, population);
			
// 			if(rnd.nextInt(100) < mutationRate)
// 				mutation(newGeneration, mutationRate);

// 			population = newGeneration;
// 		}

// 		if(!Constants.DISPLAY) {
// 			logger.close();
// 		}
// 	}

// 	/*
// 	 *	Used to get the most scored chromosome
// 	 */
// 	public static int eletism(double[][] population) {
// 		int score = population[0].length - 1;
// 		double smallest = population[0][score];
// 		int lowest = 0;

// 		for(int i = 0; i < population.length - 1; i++) {
// 			if(smallest > population[i][score]) {
// 				smallest = population[i][score];
// 				lowest = i;
// 			}
// 		}

// 		return lowest;
// 	}

// 	/*
// 	 *	Use to randomly change a gene on a chromosome
// 	 */
// 	public static void mutation(double[][] population, int mutationRate) {
// 		int i = rnd.nextInt((population.length * mutationRate) / 100);
// 		do { i--;
// 			population
// 				[rnd.nextInt(population.length)]
// 				[rnd.nextInt(population[0].length - 2)]
// 					= getRandom();
// 		} while( i > 0 );
// 	}

// 	/*
// 	 *	Encapsulate the walk through the genes of the population
// 	 */
// 	private static void fitness(double[][] population, int generation, int entrance, int exit) {
// 		int score = population[0].length - 1;
// 		for(int i = 0; i < population.length && quit; i++){
// 			population[i][score] = fitness(population[i], generation, entrance, exit);
// 		}
// 	}

// 	/*
// 	 *	Logic to calculate how fitness a chromosome (score)
// 	 */
// 	private static Integer hit = Integer.MAX_VALUE;
// 	private static Integer fit = Integer.MAX_VALUE;
// 	private static boolean hited = false;
// 	private static double fitness(double[] chromosome, int generation, int entrance, int exit) {
//         neuralNetwork.setNetworkWeight(chromosome);
//         table.setNetwork(neuralNetwork);
// 		int lastStep, fitness, gene;
// 		Color color = Color.BLUE;
// 		Integer pos = entrance;
// 		String path = "";
// 		hited = false;
// 		hit = 0;


		
// 		fitness = lastStep = gene = 0;

//         neuralNetwork.setNetworkWeight(chromosome);


        
//         double[] entries = table.lookAround(entrance);
//         entries[4] = manhattan(entrance); //table.nearestObjective(entrance, manhattan(entrance));


//         table.setInputLayer(entries);
//         table.setNetwork(neuralNetwork);
// 		System.out.println(neuralNetwork);

//         double[] saida = neuralNetwork.propagation(entries);
//         System.out.println("Rede Neural - Camada de Saida: Valor de Y");
//         for(int i=0; i<saida.length; i++) {
//             System.out.println("Neuronio " + i + " : " + saida[i]);
//         }
        
//         int active = activeNeuron(saida);
//         System.out.print("Selected scenario: " + "Neuronio " + active + " ");
//         System.out.println(": " + saida[active]);

//         while (pos != null) {
// 			switch(active) {
// 				case Constants.NORTH:
// 					path += " U";
// 					pos = table.moveUpBPos(color, pos);
// 					System.out.println("CIMA - Moved to: " + pos);
// 					break;

// 				case Constants.SOUTH:
// 					path += " D";
// 					pos = table.moveDownBPos(color, pos);
// 					System.out.println("BAIXO - Moved to: " + pos);
// 					break;

// 				case Constants.EAST:
// 					path += " R";
// 					pos = table.moveRightBPos(color, pos);
// 					System.out.println("DIREITA - Moved to: " + pos);
// 					break;

// 				case Constants.WEST:
// 					path += " L";
// 					pos = table.moveLeftBPos(color, pos);
// 					System.out.println("ESQUERDA - Moved to: " + pos);
// 					break;
// 			}
// 		}


// 		if(Constants.DISPLAY) {
// 			// table.clear();
// 			table.setMessage( message(generation, fitness) );
// 		} else if (hited)
// 			logger.publishLog(chromosome, generation, gene, hit);
		

// 		return fitness;
// 	}


// 	private static int activeNeuron(double[] output) {
//         double[] tmp = Arrays.copyOf(output, output.length);
//         Arrays.sort(tmp);

// 		for(int i = 0; i < output.length; i ++)
// 			if(output[i] == tmp[tmp.length-1])
// 				return i;

// 		return -1;
// 	}

// 	/*
// 	 *	Calculates the fitness accordingly of some criterias:
// 	 *		- Valid moviment: 1 point
// 	 *		- Reaches some wall: 2 points
// 	 *		- Falls from table: 10 points
// 	 */
// 	private static boolean quit = true;
// 	private static Integer fitness(int fitness, Integer pos, int gene, int generation) {
// 		if(pos == null) {
// 			return table.getTableSize() * 20;
// 		} else {
// 			int fits = manhattan(pos);
// 			if(pos == exit) {
// 				if(hit < fit)
// 					try {
// 						hited = true;
// 						fit = hit;
// 						if(Constants.DISPLAY){
// 							table.setMessage(message(gene, generation, fitness, hit));
// 		        			Thread.sleep(App.delay_f);
// 						}
// 	        		} catch(Exception e) { e.printStackTrace(); }
// 					if(fit == 0) {
// 						System.out.println("REACH SOLUTION: " + gene);
// 						if(Constants.LEFT_WHEN_FIND_FIRST)
// 							quit = false;
// 	        		}
//         		return 0;
// 			} 

// 			if(table.isWall(pos)) {
// 				hit++;
// 				fits += 12 + hit;
// 				return fits;
// 			}

// 			if(table.visited(pos))
// 				return fits + 5;
			
// 			return fits + 1;	
// 		}
// 	}

// 	public static int manhattan(int pos) {
// 		int xExitCoord = table.getExit() % table.X_LENGTH;
//         int yExitCoord = table.getExit() / table.X_LENGTH;
//         return
//             Math.abs((pos % table.X_LENGTH) - xExitCoord) +
//             Math.abs((pos / table.X_LENGTH) - yExitCoord);
//     }

// 	/*
// 	 *	Transfer a gene of a population to the new generation
// 	 */
// 	private static void transfer(double[][] from, int pos, double[][] to, int ps) {
// 		for(int i = 0; i < from[0].length; i++)
// 			to[ps][i] = from[pos][i];
// 	}

// 	/*
// 	 *	Randomly get two possible fathers and chose the best
// 	 */
// 	public static int tournament(double[][] population) {
// 		int father 	= rnd.nextInt(population.length);
// 		int dad 	= rnd.nextInt(population.length);
// 		int score 	= population[0].length - 1;
// 		if(population[father][score] < population[dad][score]) {
// 			return father;
// 		}
// 		return dad;
// 	}

// 	/*
// 	 *	Cross all population to the next level of generation
// 	 */
// 	private static void crossover(double[][] newGeneration, double[][] population) {
// 		for(int i = 1; i < population.length; i++) {

// 			int chromosome1 = tournament(population);
// 			int chromosome2 = tournament(population);

// 			for(int j=0; j < population[0].length - 1; j++) { 
// 					newGeneration[i][j] = (population[chromosome1][j] + population[chromosome2][j]) / 2;
// 			}
// 		}
// 	}

// 	/*
// 	 *	Generate double random between -1 and 1
// 	 */
// 	private static double getRandom() {
// 		return -1 + (2) * rnd.nextDouble();
// 	}

// 	/*
// 	 *	Just to not let this concatenation on the middle code
// 	 */
// 	private static String message(int gene, int generation, int f, int hit) {
// 		return
// 			"Reach exit: " + gene + " genes" +
// 			"\nGeneration: " + generation +
// 			"\nFitness: " + ((double) f/100) + "\nHIT "+hit;
// 	}
// 	private static String message(int g, int f) {
// 		return
// 			"Generation: " + g + "\n"+
// 			"Fitness: " + ( (double) f/100);
// 	}

// 	private static void breakpoint() {
// 		while(Constants.DEBUG) {}
// 	}
// }