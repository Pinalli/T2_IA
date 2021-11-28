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

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.util.Arrays;
import java.util.List;


public class Table {
	public static final Font font = new Font("Arial", Font.PLAIN, 20);

	public static final int CELL_WIDTH = 20; // table square size
	public static final int DOT_MARGIN = 5; // space between wall and dot
	public static final int DOT_SIZE = 10; // size of table solution dot
	public static final int MARGIN = 50; // buffer between window edge and table
	
	public final int X_LENGTH; // buffer between window edge and table
	public final int Y_LENGTH; // buffer between window edge and table
	
	private Block[] blocks; // array containing all the blocks in the table	
	private Block[] blank;	

    private int tableSize;
    
    private ArrayList<Integer> road;
    private Integer entrance;
    private Integer exit;

    private String message;

	private BufferedImage img;
	private TablePanel panel;

	public void setPanel(TablePanel panel) {
		this.panel = panel;
	}

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

	private Table(String filename) {
		ArrayList<String[]> fileArray = new ArrayList<String[]>();
		File file = new File("Tables/" + filename);
		road = new ArrayList<Integer>();
		entrance = exit = null;
		FileReader fileReader;
		BufferedReader reader;
		message = "";
		String line;

		try {
			fileReader = new FileReader(file);
			reader = new BufferedReader(fileReader);
			
			try {
				img = ImageIO.read(new File("dot.jpg"));
				while((line = reader.readLine()) != null)
					fileArray.add(line.split(" "));

				reader.close();
			} catch (IOException e) {
				System.out.println("ERRO ao ler o aquivo");
				System.exit(0);
			}
		} catch (FileNotFoundException e) {
			System.out.println("ERRO ao ler o aquivo");
			System.exit(0);
		}

		X_LENGTH = fileArray.get(0).length;
		Y_LENGTH = fileArray.size();
		tableSize = X_LENGTH * Y_LENGTH;
		String[] matriz = new String[tableSize];

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
		blocks = copyArray(blank);
	}


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

	private Integer move(Color color, int pos) {
		if(isWall(pos) == true) {
			try {
				setWallSpotWR(Color.RED, pos);
			} catch(Exception e) { e.printStackTrace(); }
			return pos;
		}

		try{
			setSpotWR(color, pos);
		} catch(Exception e) { e.printStackTrace(); }
		return pos;
	}
	public Integer moveUpPos(Color color, int cur) {
		int pos = cur - X_LENGTH;
		if( pos < 0 ) return null;
		return move(color, pos);
	}
	public Integer moveDownPos(Color color, int cur) {
		int pos = cur + X_LENGTH;
		if( pos >= tableSize ) return null;
		return move(color, pos);
	}
	public Integer moveLeftPos(Color color, int cur) {
		int pos = cur - 1;
		if( (pos/X_LENGTH) != (cur/X_LENGTH) || pos < 0 ) return null;
		return move(color, pos);
	}
	public Integer moveRightPos(Color color, int cur) {
		int pos = cur + 1;
		if( (pos/X_LENGTH) != (cur/X_LENGTH) || pos >= tableSize ) return null;
		return move(color, pos);
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

	public int 	 	getTableSize(){ return tableSize; }
	public Integer 	getExit() 	  { return exit; }
	public Integer 	getEntrance() { return entrance; }
	public List 	getRoad() 	  { return Arrays.asList(road.toArray()); }

	public boolean visited(int pos){
		return blocks[pos].visited;
	}
	public boolean isWall(int pos) { return blocks[pos].wall; }
	public boolean isWall(int x, int y) { return blocks[(y * Y_LENGTH) + x].wall; }

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
	public void setSpotWR(Color color, int pos) throws NoSuchFieldException {
		if(blocks[pos].wall == true)
			throw new NoSuchFieldException("You are trying to move inside a wall.\nPOS:" + pos);
		blocks[pos].ball = true;
		blocks[pos].color = color;
		blocks[pos].visited = true;
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
	public void setWallSpotWR(Color color, int pos) throws NoSuchFieldException {
		if(blocks[pos].wall == false)
			throw new NoSuchFieldException("You are trying to move a non wall.\nPOS:" + pos);
		blocks[pos].ball = true;
		blocks[pos].color = color;
		blocks[pos].visited = true;
	}

	public void draw(Graphics g) {
		int xCoord, yCoord, xDotBase, yDotBase;
		g.setColor(Color.BLACK);

		for (int i = 0; i < X_LENGTH; i++) {
			int count = i;
			for (int j = 0; j < Y_LENGTH; j++) {

				xCoord = i * CELL_WIDTH + MARGIN;
				yCoord = j * CELL_WIDTH + MARGIN;

				if (j != 0) {
					count += X_LENGTH;
				}
				

				if (blocks[count].label != null) {
					g.setFont(font);
					g.drawString(blocks[count].label, (i * (CELL_WIDTH) + (CELL_WIDTH/3) + MARGIN), (j * (CELL_WIDTH) + (CELL_WIDTH) + MARGIN));
				}

				if (blocks[count].ball == true) {
					xDotBase = i * CELL_WIDTH + MARGIN + DOT_MARGIN;
					yDotBase = j * CELL_WIDTH + MARGIN + DOT_MARGIN;
					if(App.PDM == true && PathFinderAStar.FINISH_PATH == blocks[count].color) {
						try {
							g.drawImage(img, xDotBase-5, yDotBase-5, DOT_SIZE+10, DOT_SIZE+10, null);
						} catch (Exception ignored) {}
					} else {
						g.setColor(blocks[count].color);
						g.fillOval(xDotBase, yDotBase, DOT_SIZE, DOT_SIZE);
						g.setColor(Color.BLACK);
					}
				}

				if (blocks[count].wall == true) {
					g.drawLine(xCoord, yCoord,
						((i + 1) * CELL_WIDTH + MARGIN), (j * CELL_WIDTH + MARGIN));
					g.drawLine(xCoord, (j + 1) * CELL_WIDTH
						+ MARGIN, (i + 1) * CELL_WIDTH + MARGIN, (j + 1) * CELL_WIDTH
						+ MARGIN);
					g.drawLine((i + 1) * CELL_WIDTH + MARGIN, j * CELL_WIDTH
						+ MARGIN, (i + 1) * CELL_WIDTH + MARGIN, (j + 1) * CELL_WIDTH
						+ MARGIN);
					g.drawLine(xCoord, yCoord, xCoord, (j + 1) * CELL_WIDTH + MARGIN);
				}
			}

			if (!message.equals("")) {
				int lines = 0;
				for(String msg : message.split("\n")) {
					g.drawString(msg, (MARGIN), ((Y_LENGTH + lines) * (CELL_WIDTH) + (CELL_WIDTH) + MARGIN));
					lines++;
				}
			}


			for (int x = 0; x < X_LENGTH; x++) {
				g.drawLine(MARGIN, MARGIN, (X_LENGTH * CELL_WIDTH + MARGIN), (0 + MARGIN));
				g.drawLine((X_LENGTH * CELL_WIDTH + MARGIN), (Y_LENGTH * CELL_WIDTH + MARGIN), MARGIN, (Y_LENGTH * CELL_WIDTH + MARGIN));
				g.drawLine(MARGIN, MARGIN, MARGIN, (Y_LENGTH * CELL_WIDTH + MARGIN));
				g.drawLine((X_LENGTH * CELL_WIDTH + MARGIN), (Y_LENGTH * CELL_WIDTH + MARGIN), (X_LENGTH * CELL_WIDTH + MARGIN), (MARGIN));
			}
		}
	}

	public int getXLength() {
		return (((X_LENGTH+1) * CELL_WIDTH) + (MARGIN * 2));
	}

	public int getYLength() {
		return (((Y_LENGTH+3) * CELL_WIDTH) + (MARGIN * 3));
	}

	public Dimension windowSize() {
		return new Dimension(X_LENGTH * CELL_WIDTH + MARGIN * 2, Y_LENGTH * CELL_WIDTH + (MARGIN * 2) + (MARGIN / 2));
	}
}