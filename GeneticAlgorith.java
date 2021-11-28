import java.util.Scanner;
import java.util.Random;
import java.awt.Color;

class GeneticAlgorith {

    /**/public static final int GENES   = 4;
	/*-Possible genes--------------------------*/
    /**/ public static final int NORTH 	= 0; /**/
    /**/ public static final int SOUTH 	= 2; /**/
    /**/ public static final int EAST 	= 1; /**/
    /**/ public static final int WEST   = 3; /**/
    /*-----------------------------------------*/


	private int mutationRate;
	private int generation;

	private static Logger logger;

	private static int entrance;
	private static int exit;

	private int[][] population;
	private int[][] newGeneration;

	private static Table table;
	private static Random rnd;
	private int roadStones;

	public GeneticAlgorith(Table table, int generations, int populationSize, int mutation) {
		this.roadStones = table.getRoad().size();
		this.table = table;
		rnd = new Random();

		if(!App.DISPLAY) {
			logger = Logger.getInstance();
			logger.initFile("result");
			logger.publishLog("Generations: " + generation + "\nPopulation: " + populationSize + "\nMutation rate: " +mutation+"\n");
		}

		entrance = table.getEntrance();
		exit = table.getExit();
		
		if(generations == 0)
			generations = 1000000000;
		mutationRate = mutation;
		this.generation = generations;

		
		population 	  = new int[populationSize][roadStones + 1];
		newGeneration = new int[populationSize][roadStones + 1];

		/*
		 *	Generate a random population
		 */
		for(int i = 0; i < population.length; i++)
			for(int j = 0; j < roadStones - 1; j++)
				population[i][j] = rnd.nextInt(GENES);

		/*
		 *	Run over the generations
		 */
		for(int g = 1; g <= generation && quit; g++) {
			fitness(population, g, entrance, exit);
			transfer(population, eletism(population), newGeneration, 0);
			crossover(newGeneration, population);
			
			if(rnd.nextInt(100) < mutationRate)
				mutation(newGeneration, mutationRate);

			population = newGeneration;
		}

		if(!App.DISPLAY) {
			logger.close();
		}
	}

	/*
	 *	Used to get the most scored chromosome
	 */
	public static int eletism(int[][] population) {
		int score = population[0].length - 1;
		int smallest = population[0][score];
		int lowest = 0;

		for(int i = 0; i < population.length - 1; i++) {
			if(smallest > population[i][score]) {
				smallest = population[i][score];
				lowest = i;
			}
		}

		return lowest;
	}

	/*
	 *	Use to randomly change a gene on a chromosome
	 */
	public static void mutation(int[][] population, int mutationRate) {
		int i, size;
		i = rnd.nextInt((population.length * mutationRate) / 100);
		do {
			i--;

			int line = rnd.nextInt(population.length);
			int column = rnd.nextInt(population[0].length - 2);
			int gene = rnd.nextInt(GENES);

			if(population[line][column] == gene)
				gene = rnd.nextInt(GENES);

			population[line][column] = gene;
		} while( i > 0 );
	}

	/*
	 *	Encapsulate the walk through the genes of the population
	 */
	private static void fitness(int[][] population, int generation, int entrance, int exit) {
		int score = population[0].length - 1;
		for(int i = 0; i < population.length && quit; i++)
			population[i][score] = fitness(population[i], generation, entrance, exit);
	}

	/*
	 *	Logic to calculate how fitness a chromosome (score)
	 */
	private static Integer hit = Integer.MAX_VALUE;
	private static Integer fit = Integer.MAX_VALUE;
	private static boolean hited = false;
	private static int fitness(int[] chromosome, int generation, int entrance, int exit) {
		int lastStep, fitness, gene;
		Color color = Color.BLUE;
		Integer pos = entrance;
		String path = "";
		hited = false;
		hit = 0;

		fitness = lastStep = gene = 0;
		for(; gene < chromosome.length - 1 && pos != null && quit; gene++) {
			lastStep = pos;

			switch(chromosome[gene]) {
				case NORTH:
					path += " U";
					pos = table.moveUpPos(color, pos);
					break;

				case SOUTH:
					path += " D";
					pos = table.moveDownPos(color, pos);
					break;

				case EAST:
					path += " R";
					pos = table.moveRightPos(color, pos);
					break;

				case WEST:
					path += " L";
					pos = table.moveLeftPos(color, pos);
					break;
			}

			fitness += fitness(fitness, pos, gene, generation);
		}

		// System.out.println(completePath(chromosome, path, gene, fitness));

		if(App.DISPLAY) {
			table.clear();
			table.setMessage( message(generation, fitness) );
		} else if (hited)
			logger.publishLog(chromosome, generation, gene, hit);
		
		return fitness;
	}

	/*
	 *	Calculates the fitness accordingly of some criterias:
	 *		- Valid moviment: 1 point
	 *		- Reaches some wall: 2 points
	 *		- Falls from table: 10 points
	 */
	private static boolean quit = true;
	private static Integer fitness(int fitness, Integer pos, int gene, int generation) {
		if(pos == null) {
			return table.getTableSize() * 20;
		} else {
			int fits = manhattan(pos);
			if(pos == exit) {
				if(hit < fit)
					try {
						hited = true;
						fit = hit;
						if(App.DISPLAY){
							table.setMessage(message(gene, generation, fitness, hit));
		        			Thread.sleep(App.delay_f);
						}
	        		} catch(Exception e) { e.printStackTrace(); }
					if(fit == 0) {
						System.out.println("REACH SOLUTION: " + gene);
						if(App.LEFT_WHEN_FIND_FIRST)
							quit = false;
	        		}
        		return 0;
			} 

			if(table.isWall(pos)) {
				hit++;
				fits += 12 + hit;
				return fits;
			}

			if(table.visited(pos))
				return fits + 5;
			
			return fits + 1;	
		}
	}

	public static int manhattan(int pos) {
		int xExitCoord = table.getExit() % table.X_LENGTH;
        int yExitCoord = table.getExit() / table.X_LENGTH;
        return
            Math.abs((pos % table.X_LENGTH) - xExitCoord) +
            Math.abs((pos / table.X_LENGTH) - yExitCoord);
    }

	// private static String completePath(int[] chromosome, String path, int gene, int fitness) {
	// 	if(false) {
	// 		int g = gene;
	// 		if(g < chromosome.length - 1)
	// 			path += " |X|";
	// 		for (;g < chromosome.length - 1; g++) {
	// 			switch(chromosome[g]) {
	// 				case NORTH:
	// 					path += " U";
	// 					break;
	// 				case SOUTH:
	// 					path += " D";
	// 					break;
	// 				case EAST:
	// 					path += " R";
	// 					break;
	// 				case WEST:
	// 					path += " L";
	// 					break;
	// 			}
	// 		}
	// 	}
	// 	return path + "\t F: " + fitness;
	// }


	/*
	 *	Transfer a gene of a population to the new generation
	 */
	private static void transfer(int[][] from, int pos, int[][] to, int ps) {
		for(int i = 0; i < from[0].length; i++) {
			to[ps][i] = from[pos][i];
		}
	}

	/*
	 *	Randomly get two possible fathers and chose the best
	 */
	public static int tournament(int[][] population) {
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
	private static void crossover(int[][] newGeneration, int[][] population) {
		for(int i = 1; i < population.length; i+=1) {

			int chromosome1 = tournament(population);
			int chromosome2 = tournament(population);

			for(int j=0; j < population[0].length - 1; j++) {
				if(j < population[0].length / 2) {
					newGeneration[i][j] = population[chromosome1][j];
					if((i+1) < population.length)
						newGeneration[i+1][j] = population[chromosome2][j];
				} else {
					newGeneration[i][j] = population[chromosome2][j];
					if((i+1) < population.length)
						newGeneration[i+1][j] = population[chromosome1][j];
				}
			}
		}
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
}