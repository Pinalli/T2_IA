package src.ui;

/*
 * Java Graphics idea from: Irene Alvarado 
 *
 *	moodle.pucrs.br/pluginfile.php/3858618/mod_resource/content/1/NRainhas.java
 *	moodle.pucrs.br/pluginfile.php/3849965/mod_resource/content/1/AG.java
 */

// import java.lang.NoSuchFieldException;

import java.util.stream.Collectors;

import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileReader;
import java.util.Random;
import java.io.File;
import java.awt.*;

import java.applet.*;
import java.awt.event.*;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.event.*;
import javax.swing.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


import src.utils.Constants;
import src.App;

public class Table {

	public static final Font font = new Font("Arial", Font.PLAIN, 20);
	
	public final int X_LENGTH; // buffer between window edge and table
	public final int Y_LENGTH; // buffer between window edge and table
	
	private final JSlider delaySlider;
	private final JButton stopButton;
	
	private Block[] blocks; // array containing all the blocks in the table	
	private Block[] blank;
	private List<Integer> coinBag;

    private int tableSize;
    
    private ArrayList<Integer> road;
    private Integer entrance;
    private Integer exit;

    private String message;

	private BufferedImage coin;
	private TablePanel panel;
	// private JFrame jframe;

	public void setPanel(TablePanel panel) {
		this.panel = panel;
		panelAddFeatures();
	}
	private void panelAddFeatures() {
		this.panel.add(delaySlider, BorderLayout.SOUTH);
		this.panel.add(stopButton, BorderLayout.SOUTH);
	}
	

	/* INSTANCE ------------------------------------------------------------------------------------------------------------------------*/

	private static Table instance;

	public static Table instance(String filename) {
		if(instance == null)
			if(filename.isEmpty())
				instance = new Table(filename);
			else
				throw new InstantiationError("Instance needs a filename");

		return instance;
	}
	public static Table newInstance(String filename) {
		return instance = new Table(filename);
	}

	/* CONSTRUCTOR ------------------------------------------*/
	
	private Table(String filename) {
		/* INSTANCIES ----------------------------------------------*/
		ArrayList<String[]> fileArray = new ArrayList<String[]>();
		File file = new File("Tables/" + filename);
		coinBag = new LinkedList<Integer>();
		road = new ArrayList<Integer>();
		entrance = exit = null;
		FileReader fileReader;
		BufferedReader reader;
		message = "";
		String line;

		/* FILE HANDLER --------------------------------------------*/
		
		try {
			coin = ImageIO.read(new File("src/ui/coin.jpg"));
			fileReader = new FileReader(file);
			reader = new BufferedReader(fileReader);			
			try {
				while((line = reader.readLine()) != null)
					fileArray.add(line.split(" "));
				reader.close();
			} catch (IOException e) {
				System.out.println("Inside try: \n" + e.getLocalizedMessage());
				System.exit(0);
			}
		} catch (FileNotFoundException fnfe) {
			System.out.println("ERRO ao ler o aquivo\n"+fnfe.getLocalizedMessage());
			System.exit(0);
		} catch (IOException ioe) {
			System.out.println("IO Exception: \n" + ioe.getLocalizedMessage());
			ioe.printStackTrace();
			System.exit(0);
		}

		/* INSTANCIES ----------------------------------------------*/

		X_LENGTH = fileArray.get(0).length;
		Y_LENGTH = fileArray.size();
		
		Constants.WINDOW_HEIGHT = 650 + (X_LENGTH * Constants.CELL_WIDTH + Constants.MARGIN * 2);
		Constants.WINDOW_WIDTH = 30 + (Y_LENGTH * Constants.CELL_WIDTH + (Constants.MARGIN * 2) + (Constants.MARGIN / 2));

		tableSize = X_LENGTH * Y_LENGTH;
		String[] matriz = new String[tableSize];
		
		/* INTERPRET FILE ------------------------------------------*/

		int count = 0;
		for(String[] s : fileArray) {
			for(String c : s) {
				matriz[count] = c;
				count++;
			}
		}

		blank = new Block[tableSize];
		for (int i = 0; i < tableSize; i++) {
			if(matriz[i].equals("1")) {
				blank[i] = new Block(true);
			} else {
				blank[i] = new Block(false);
				road.add(i);

				if(matriz[i].equals("M")) {
					blank[i].label = matriz[i];
					coinBag.add(i);
				}
				if(matriz[i].equals("E")) {
					blank[i].label = matriz[i];
					entrance = i;
				}
				if(matriz[i].equals("S")) {
					blank[i].label = matriz[i];
					exit = i;
				}
			}
		}

		/* DRAW FEATURES (BUTTON & SLIDER) -------------------------*/

		if(App.DEBUG)stopButton = new JButton("Cont");
		else stopButton = new JButton("Stop");
		stopButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					App.DEBUG = !App.DEBUG;
					if(App.DEBUG) stopButton.setText("Cont");
					else stopButton.setText("Stop");
				}
			}
		);
	    delaySlider = new JSlider(JSlider.HORIZONTAL, Constants.DELAY_MIN, Constants.DELAY_MAX, Constants.DELAY_DEFAULT);
		delaySlider.addChangeListener(
			new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					App.DELAY = delaySlider.getValue();
				}
			}
		);

		inputLayer 	= new String[Constants.ENTRIES];
		Arrays.fill(inputLayer, "   ?");
		activeOutputWall = false;
		activeOutput = -1;

		blocks = copyArray(blank);
	}

	public void setStop() {
		App.DEBUG = true;
		stopButton.setText("Cont");
	}


	/* UTILS ---------------------------------------------------------------------------------------------------------------------------*/

	private Block[] copyArray(Block[] blocks) {
		return Arrays
					.stream(blocks)
					.map(Block::copyOf)
             		.toArray(Block[]::new);
	}

	public Block[] setBlocks(Block[] plain) {
		return blocks = plain;
	}

	public Block[] getBlocks() {
		return copyArray(blocks);
	}
	
	public Block[] getBlank() {
		return copyArray(blank);
	}

	public void clear() {
		blocks = copyArray(blank);
	}
	
	/*----------------------------------------------------------------------------------------------------------------------------------*/

    public int manhattan(int pos) {
		return manhattan(pos, getExit());    	
    }

    public int manhattan(int pos, int to) {
        int xCoord = to % X_LENGTH;
        int yCoord = to / X_LENGTH;
        return
            Math.abs((pos % X_LENGTH) - xCoord) +
            Math.abs((pos / X_LENGTH) - yCoord);
    }
	
	/* MOVES ---------------------------------------------------------------------------------------------------------------------------*/
	
	private Integer move(Color color, int pos, boolean refresh) {
		if(isWall(pos) == true) {
			activeOutputWall = true;
			try {
				if(refresh)
					setWallSpot(Color.RED, pos);
				else
					setWallSpotWR(Color.RED, pos);
			} catch(Exception e) { e.printStackTrace(); }
			return pos;
		}

		try{
			if(refresh)
				setSpot(color, pos);
			else
				setSpotWR(color, pos);
		} catch(Exception e) { e.printStackTrace(); }
		return pos;
	}

	/*
	 *	Move with Blocking positions (returning null) if is a wall
	 */
	public Integer moveUpBPos(Color color, int cur, boolean refresh) {
		activeOutput = Constants.NORTH;
		return validateWallMoviment(moveUpPos(color, cur, refresh));
	}
	public Integer moveDownBPos(Color color, int cur, boolean refresh) {
		activeOutput = Constants.SOUTH;
		return validateWallMoviment(moveDownPos(color, cur, refresh));
	}
	public Integer moveLeftBPos(Color color, int cur, boolean refresh) {
		activeOutput = Constants.EAST;
		return validateWallMoviment(moveLeftPos(color, cur, refresh));
	}
	public Integer moveRightBPos(Color color, int cur, boolean refresh) {
		activeOutput = Constants.WEST;
		return validateWallMoviment(moveRightPos(color, cur, refresh));
	}

	private Integer validateWallMoviment(Integer pos) {
		if(pos == null || isWall(pos)) return null;
		return pos;
	}

	private Integer moveUpPos(Color color, int cur, boolean refresh) {
		int pos = cur - X_LENGTH;
		if( pos < 0 ) return outOfBoard(Constants.NORTH);
		return move(color, pos, refresh);
	}
	private Integer moveDownPos(Color color, int cur, boolean refresh) {
		int pos = cur + X_LENGTH;
		if( pos >= tableSize ) return outOfBoard(Constants.SOUTH);
		return move(color, pos, refresh);
	}
	private Integer moveLeftPos(Color color, int cur, boolean refresh) {
		int pos = cur - 1;
		if( (pos/X_LENGTH) != (cur/X_LENGTH) || pos < 0 ) return outOfBoard(Constants.EAST);
		return move(color, pos, refresh);
	}
	private Integer moveRightPos(Color color, int cur, boolean refresh) {
		int pos = cur + 1;
		if( (pos/X_LENGTH) != (cur/X_LENGTH) || pos >= tableSize ) return outOfBoard(Constants.WEST);

		return move(color, pos, refresh);
	}

	private Integer outOfBoard(int direction) {
		activeOutputWall = true;
		activeOutput = direction;
		panel.repaint();
		return null;
	}

	public double nearestObjective(int pos) {
		int nextCoin = nearestCoin(pos);
		int manhattan = manhattan(pos);
		if(nextCoin < manhattan)
			return nextCoin;

		return - manhattan;
	}

	public int nearestCoin(int pos) {
		int result = Integer.MAX_VALUE;
		// coinBag
		return result;
	}

	public double[] lookAround(int pos) {
		double[] around = new double[Constants.ENTRIES];
		int posu = pos - X_LENGTH;
		int posd = pos + X_LENGTH;
		int posl = pos - 1;
		int posr = pos + 1;

		around[Constants.NORTH] = lookUp(posu, posu < 0);
		around[Constants.SOUTH] = lookUp(posd, posd >= tableSize);
		around[Constants.WEST] 	= lookUp(posl, (posl/X_LENGTH) != (pos/X_LENGTH) || posl < 0);
		around[Constants.EAST] 	= lookUp(posr, (posr/X_LENGTH) != (pos/X_LENGTH) || posr >= tableSize);
		around[Constants.TARGET] = nearestObjective(pos);

		setInputLayer(around);
		return around;
	}

	public double lookUp(int pos, boolean onboard) {
		if 		(  onboard  )	return Constants.OUT;
		else if (pos == exit)	return Constants.EXIT;
		else if (isWall(pos))	return Constants.WALL;
		else if (isCoin(pos))	return Constants.COIN;
		else					return Constants.FREE;
	}

	public Integer getUpPos(int cur) {
		int pos = cur - X_LENGTH;
		if( pos < 0 ) return null;
		return pos;
	}
	public Integer getDownPos(int cur)  {
		int pos = cur + X_LENGTH; 
		if( pos >= tableSize ) return null;
		return pos;
	}
	public Integer getLeftPos(int cur)  {
		int pos = cur - 1;
		if( (pos/X_LENGTH) != (cur/X_LENGTH) ) return null;
		return cur - 1;
	}
	public Integer getRightPos(int cur) {
		int pos = cur + 1;
		if( (pos/X_LENGTH) != (cur/X_LENGTH) ) return null;
		return pos;
	}

	/* VALIDATIONS ---------------------------------------------------------------------------------------------------------------------*/

	public int 	 	getTableSize(){ return tableSize; }
	public Integer 	getExit() 	  { return exit; }
	public Integer 	getEntrance() { return entrance; }
	public List 	getRoad() 	  { return Arrays.asList(road.toArray()); }

	public boolean visited(int pos){
		return blocks[pos].visited;
	}
	public boolean isWall(int pos) { return blocks[pos].wall; }
	public boolean isWall(int x, int y) { return isWall((y * Y_LENGTH) + x); }

	public boolean isExit(int pos) {
		return blocks[pos].label != null && blocks[pos].label.equals("S");
	}
	public boolean isExit(int x, int y) { return isCoin((y * Y_LENGTH) + x); }
	public boolean isCoin(int pos) {
		return blocks[pos].label != null && blocks[pos].label.equals("M");
	}
	public boolean isCoin(int x, int y) { return isCoin((y * Y_LENGTH) + x); }

	public void setMessage(String message) {
		this.message = message;
		panel.repaint();
	}

	public void setMessageWR(String message) {
		this.message = message;
	}

	public void setSpot(Color color, int x, int y) throws NoSuchFieldException {
		try {
			setSpot(color, ((y * Y_LENGTH) + x));
		} catch (NoSuchFieldException e) {
			throw new NoSuchFieldException(e.getMessage() + " X:" + x + " Y:" + y);
		}
	}
	public void setSpot(Color color, int pos) throws NoSuchFieldException {
		setSpotWR(color, pos);
		panel.repaint();
	}

	public boolean wasVisited(int pos) {
		return blocks[pos].visited;
	}
	public boolean collectCoin(int pos) {
		if(blocks[pos].label != null && blocks[pos].label.equals("M")){
			blocks[pos].label = null;
			return true;
		}
		return false;
	}

	/*
	 *	Set spot without repaint
	 */
	public void setSpotWR(Color color, int pos) throws NoSuchFieldException {
		if(blocks[pos].wall == true)
			throw new NoSuchFieldException("You are trying to move inside a wall.\nPOS:" + pos);
		blocks[pos].visited = true;
		blocks[pos].ball = true;
		blocks[pos].color = color;
	}


	public void setWallSpot(Color color, int x, int y) throws NoSuchFieldException {
		try {
			setWallSpot(color, ((y * Y_LENGTH) + x));
		} catch (NoSuchFieldException e) {
			throw new NoSuchFieldException(e.getMessage() + " X:" + x + " Y:" + y);
		}
	}
	public void setWallSpot(Color color, int pos) throws NoSuchFieldException {
		setWallSpotWR(color, pos);
		panel.repaint();
	}
	/*
	 *	Set wall spot without repaint
	 */
	public void setWallSpotWR(Color color, int pos) throws NoSuchFieldException {
		if(blocks[pos].wall == false)
			throw new NoSuchFieldException("You are trying to move a non wall.\nPOS:" + pos);
		blocks[pos].ball = true;
		blocks[pos].color = color;
		blocks[pos].visited = true;
	}


	/*----------------------------------------------------------------------------------------------------------------------------------*/

	public int getXLength() {
		return Constants.WINDOW_HEIGHT;
	}

	public int getYLength() {
		return Constants.WINDOW_WIDTH;
	}

	public Dimension windowSize() {
		return new Dimension(0,0);
	}

	/*----------------------------------------------------------------------------------------------------------------------------------*/

	public void draw(Graphics g) {
		drawMaze(g);
		drawNetwork(g);
	}

	/*----------------------------------------------------------------------------------------------------------------------------------*/

	private void drawMaze(Graphics g) {
		int xCoord, yCoord, xDotBase, yDotBase;
		g.setColor(Color.BLACK);

		for (int i = 0; i < X_LENGTH; i++) {
			int count = i;
			for (int j = 0; j < Y_LENGTH; j++) {
				xCoord = i * Constants.CELL_WIDTH + Constants.MARGIN;
				yCoord = j * Constants.CELL_WIDTH + Constants.MARGIN;
				if (j != 0) { count += X_LENGTH; }
				if (blocks[count].label != null) {
					if(blocks[count].label.equals("M")) {
						xDotBase = i * Constants.CELL_WIDTH + Constants.MARGIN + Constants.DOT_MARGIN;
						yDotBase = j * Constants.CELL_WIDTH + Constants.MARGIN + Constants.DOT_MARGIN;
						g.drawImage(coin, xDotBase-4, yDotBase-4, Constants.DOT_SIZE+10, Constants.DOT_SIZE+10, null);
					} else {
						g.setFont(font);
						g.drawString(blocks[count].label, (i * (Constants.CELL_WIDTH) + (Constants.CELL_WIDTH/3) + Constants.MARGIN), (j * (Constants.CELL_WIDTH) + (Constants.CELL_WIDTH) + Constants.MARGIN));
					} 
				}
				if (blocks[count].ball == true) {
					xDotBase = i * Constants.CELL_WIDTH + Constants.MARGIN + Constants.DOT_MARGIN;
					yDotBase = j * Constants.CELL_WIDTH + Constants.MARGIN + Constants.DOT_MARGIN;
					g.setColor(blocks[count].color);
					g.fillOval(xDotBase, yDotBase, Constants.DOT_SIZE, Constants.DOT_SIZE);
					g.setColor(Color.BLACK);
				}
				if (blocks[count].wall == true) {
					g.drawLine(xCoord, yCoord,
						((i + 1) * Constants.CELL_WIDTH + Constants.MARGIN), (j * Constants.CELL_WIDTH + Constants.MARGIN));
					g.drawLine(xCoord, (j + 1) * Constants.CELL_WIDTH
						+ Constants.MARGIN, (i + 1) * Constants.CELL_WIDTH + Constants.MARGIN, (j + 1) * Constants.CELL_WIDTH
						+ Constants.MARGIN);
					g.drawLine((i + 1) * Constants.CELL_WIDTH + Constants.MARGIN, j * Constants.CELL_WIDTH
						+ Constants.MARGIN, (i + 1) * Constants.CELL_WIDTH + Constants.MARGIN, (j + 1) * Constants.CELL_WIDTH
						+ Constants.MARGIN);
					g.drawLine(xCoord, yCoord, xCoord, (j + 1) * Constants.CELL_WIDTH + Constants.MARGIN);
				}
			}
		}

		g.drawLine(Constants.MARGIN, Constants.MARGIN, (X_LENGTH * Constants.CELL_WIDTH + Constants.MARGIN), (Constants.MARGIN));
		g.drawLine((X_LENGTH * Constants.CELL_WIDTH + Constants.MARGIN), (Y_LENGTH * Constants.CELL_WIDTH + Constants.MARGIN), Constants.MARGIN, (Y_LENGTH * Constants.CELL_WIDTH + (Constants.MARGIN)));
		g.drawLine(Constants.MARGIN, Constants.MARGIN, Constants.MARGIN, (Y_LENGTH * Constants.CELL_WIDTH + (Constants.MARGIN)));
		g.drawLine((X_LENGTH * Constants.CELL_WIDTH + Constants.MARGIN), (Y_LENGTH * Constants.CELL_WIDTH + Constants.MARGIN), (X_LENGTH * Constants.CELL_WIDTH + Constants.MARGIN), (Constants.MARGIN));
	}


	/*----------------------------------------------------------------------------------------------------------------------------------*/

	private void drawNetwork(Graphics g) {
		drawInputLayer(g);
		drawFirstConns(g);
		drawHiddenLayer(g);
		drawSecondConns(g);
		drawOutputLayer(g);
	}


	private int activeOutput;
	private boolean activeOutputWall;
	private String[] inputLayer;
	public void clearInputLayer() {
		Arrays.fill(inputLayer, "   ?");
		activeOutputWall = false;
		activeOutput = -1;
		// panel.repaint();
	}

	public void setInputLayer(double[] entries) {
		for(int i = 0; i < entries.length; i++){
			inputLayer[i] = String.valueOf(entries[i]);
			if(inputLayer[i].length() == 3)
				inputLayer[i] = " " + inputLayer[i];
		}
		panel.repaint();
	}

	private final String[] inputs = new String[] {"U", "R", "D", "L", "T"};
	public void drawInputLayer(Graphics g) {
		int x = Constants.NETWORK_X_DRAW_OFSET + 20, y = 38, d = 10, yOffset = 39;
		

		for (int i = 1; i < 6; i++) {
			g.drawString(inputs[i-1], x-75, y+2+(yOffset*i));
			g.drawString(inputLayer[i-1], x-50, y+(yOffset*i));
			drawCenteredCircle(g, true, x, y + (yOffset*i), d);
		}
	}

	public void drawFirstConns(Graphics g) {
		int x1 = Constants.NETWORK_X_DRAW_OFSET + 90, x2 = x1-75,
			y = 90, yOffset = 43,
			inputs = Constants.HIDDEN_LAYER_SIZE;

		if(hidden != null)
			inputs = hidden.length;

		for(int i = 0; i < inputs; i ++)
			for(int j = -20, k = 0; j < 153; j += yOffset, k++)
				if(hidden != null)
					LineArrow (g, x1, y+(i*yOffset-3), x2, (y + j), hidden[i][k]);
				else
					LineArrow (g, x1, y+(i*yOffset-3), x2, (y + j), -2);
		
	}

	private double[][] hidden;
	private double[][] output;
	public void setNetwork(double[][] hidden, double[][] output) {
		this.hidden = hidden;
		this.output = output;
	}

	public void drawHiddenLayer(Graphics g) {
		int x = Constants.NETWORK_X_DRAW_OFSET + 105, y = 50, d = 30, yOffset = 40;

		for(int i = 1; i <= Constants.HIDDEN_LAYER_SIZE; i++)
			drawCenteredCircle(g, false, x, y + (yOffset*i), d);
	}

	public void drawSecondConns(Graphics g) {
		int x1 = Constants.NETWORK_X_DRAW_OFSET + 185, x2 = x1-73,
			y = 90, yOffset = 40,
			inputs = Constants.OUTPUT_LAYER_SIZE,
			hiddenConnections = Constants.HIDDEN_LAYER_SIZE * yOffset;

		if(hidden != null)
			inputs = output.length;

		for(int i = 0; i < inputs; i ++)
			for(int j = 0, k = 0; j < hiddenConnections; j += yOffset, k++)
				if(output != null)
					LineArrow (g, x1, y+(i*yOffset-3), x2, (y + j), output[i][k]);
				else
					LineArrow (g, x1, y+(i*yOffset-3), x2, (y + j), -2);
	}
	

	public void drawOutputLayer(Graphics g) {
		String[] letters = new String[] {"U", "L", "D", "R"};
		int x = Constants.NETWORK_X_DRAW_OFSET + 195, y = 50, d = 25, yOffset = 40;
		
		for(int i = 0; i < 4; i++) {
			if(i == activeOutput) {
				if (activeOutputWall) g.setColor(Color.RED);
				else g.setColor(Color.GREEN);
				drawCenteredCircle(g, true, x, y + (yOffset*(i+1)), d);
				g.drawString(letters[i], x-5, y + 9 +(yOffset*(i+1)));
				g.setColor(Color.BLACK);
			}
			drawCenteredCircle(g, false, x, y + (yOffset*(i+1)), d);
			g.drawString(letters[i], x-5, y + 9 +(yOffset*(i+1)));
		}
	}

	/*----------------------------------------------------------------------------------------------------------------------------------*/

	public void drawCenteredCircle(Graphics g, boolean fill, int x, int y, int r) {
		Graphics2D g2 = (Graphics2D) g;
		x = x-(r/2);
		y = y-(r/2);
		
		if(fill)
			g2.fillOval(x,y,r,r);
		else
			g2.drawOval(x,y,r,r);
	}

	private static final Polygon ARROW_HEAD = new Polygon();
    
    static {
        ARROW_HEAD.addPoint(0, 0);
        ARROW_HEAD.addPoint(-5, -10);
        ARROW_HEAD.addPoint(5, -10);
    }

    public void LineArrow(Graphics g, int x, int y, int x2, int y2, double range) {
        Graphics2D g2 = (Graphics2D) g;
		AffineTransform defaultTransf = g2.getTransform();
        Stroke  defaultStroke = g2.getStroke();
        Color color = Color.BLACK;
        int thickness = 1;

        if 		(range == -2) color = Constants.NETWORK_AXONIO_DEFAULT;
        else if (range < 0)	  color = Constants.NETWORK_AXONIO_NEGATIVE;
        else 				  color = Constants.NETWORK_AXONIO_POSITIVE;

        double rangex = Math.abs(range);
        if 		(  rangex == 2  ) thickness = 1;
        else if (rangex < 0.3333) thickness = 1;
        else if (rangex < 0.6666) thickness = 2;
    	else 					  thickness = 3;


        

        //prepare variables
        AffineTransform tx2 = (AffineTransform) defaultTransf.clone();
        int endX = x2;
        int endY = y2;
        double angle = Math.atan2(endY - y, endX - x);

        //prepare arrow
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        //draw arrow
        g2.drawLine(x, y, (int) (endX - 10 * Math.cos(angle)), (int) (endY - 10 * Math.sin(angle)));
        // tx2.translate(endX, endY);
        // tx2.rotate(angle - Math.PI / 2);
        g2.setTransform(tx2);
        // g2.fill(ARROW_HEAD);

        //reset formatation
        g2.setTransform(defaultTransf);
        g2.setStroke(defaultStroke);
        g2.setColor(Color.BLACK);
    }
}
