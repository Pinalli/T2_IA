package src.utils;

import java.awt.Color;

public class Constants {


    // Runtime configuration
    public static final boolean LEFT_WHEN_FIND_FIRST = false; // Will finish the application when reachs the exit
    public static final boolean DEBUG = true;


    // JFrame draw parameters
    public static final int NETWORK_X_DRAW_OFSET = 370;
    public static final int NETWORK_Y_DRAW_OFSET = 30;
    /*--------------------------------------------------------------------------------*/
    public static final boolean DISPLAY = true; // Creates JFRAME or just print on log
    public static Integer WINDOW_HEIGHT;        // table square size
	public static Integer WINDOW_WIDTH;	        // table square size
    /*--------------------------------------------------------------------------------*/
    public static final int CELL_WIDTH  = 20;   // table square size
	public static final int DOT_MARGIN 	= 5; 	// space between wall and dot
	public static final int DOT_SIZE 	= 10;	// size of table solution dot
	public static final int MARGIN 		= 50;	// buffer between window edge and table
    /*--------------------------------------------------------------------------------*/
    public static final Color NETWORK_AXONIO_NEGATIVE = Color.CYAN;   // buffer between window edge and table
    public static final Color NETWORK_AXONIO_POSITIVE = Color.BLUE;   // buffer between window edge and table
    public static final Color NETWORK_AXONIO_DEFAULT = Color.BLACK;   // buffer between window edge and table


    // Neural Network configuration
    public static final int BIAS	          = 1;  // UP, RIGHT, DOWN, LEFT, OBJECTIVE
    public static final int ENTRIES           = 5;  // UP, RIGHT, DOWN, LEFT, OBJECTIVE
    public static final int HIDDEN_LAYER_SIZE = 5;  // Number of neurons on the network
    public static final int OUTPUT_LAYER_SIZE = 4;  // Number of neurons on the network
    public static final int CHROMOSOME_SIZE   =
        ((ENTRIES + BIAS) * HIDDEN_LAYER_SIZE) + ((HIDDEN_LAYER_SIZE + BIAS) * OUTPUT_LAYER_SIZE);


    // Slider configuration
    public static final int DELAY_DEFAULT = 0;
    public static final int DELAY_MAX     = 500; 
    public static final int DELAY_MIN     = 0; 


    // Array interpretation
    public static final int NORTH   = 0;
    public static final int SOUTH 	= 2;
    public static final int EAST 	= 1;
    public static final int WEST    = 3;
    public static final int TARGET  = 4;


    // Maze interpretation
    public static final int FREE 	=   7;
    public static final int WALL 	=  -3;
    public static final int COIN 	=  10;
    public static final int EXIT 	=  15;
    public static final int OUT    	= -10;


}