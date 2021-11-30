package src.utils;

public class Constants {

    // Will finish the application when reachs the exit
    public static final boolean LEFT_WHEN_FIND_FIRST = true;

    public static final boolean DISPLAY = true; // Creates JFRAME or just print on log
    public static Integer WINDOW_HEIGHT;        // table square size
	public static Integer WINDOW_WIDTH;	        // table square size

    public static final int CELL_WIDTH  = 20;   // table square size
	public static final int DOT_MARGIN 	= 5; 	// space between wall and dot
	public static final int DOT_SIZE 	= 10;	// size of table solution dot
	public static final int MARGIN 		= 50;	// buffer between window edge and table


    public static final int BIAS	          = 1;  // UP, RIGHT, DOWN, LEFT, OBJECTIVE
    public static final int ENTRIES           = 5;  // UP, RIGHT, DOWN, LEFT, OBJECTIVE
    public static final int HIDDEN_LAYER_SIZE = 4;  // Number of neurons on the network
    public static final int OUTPUT_LAYER_SIZE = 4;  // Number of neurons on the network
    public static final int CHROMOSOME_SIZE   =
        ((ENTRIES + BIAS) * HIDDEN_LAYER_SIZE) + ((HIDDEN_LAYER_SIZE + BIAS) * OUTPUT_LAYER_SIZE);

    /*   Given delay by ms   */
    public static final int DELAY_DEFAULT = 1000;
    public static final int DELAY_MAX     = 5000; 
    public static final int DELAY_MIN     = 0; 

    public static final int NORTH   = 0;
    public static final int SOUTH 	= 2;
    public static final int EAST 	= 1;
    public static final int WEST    = 3;


    public static final int FREE 	= 0;
    public static final int WALL 	= 1;
    public static final int COIN 	= 2;
    public static final int EXIT 	= 3;
    public static final int OUT    	=-1;

}