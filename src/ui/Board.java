package src.ui;


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

import src.nn.NeuralNetwork;
import src.nn.Neuron;

import src.utils.Constants;
import src.App;


public abstract Board {

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

	/* Panel ---------------------------------------------------------------- */

	public void setPanel(TablePanel panel) {
		this.panel = panel;
		panelAddFeatures();
	}

	private void panelAddFeatures() {
		this.panel.add(delaySlider, BorderLayout.SOUTH);
		this.panel.add(stopButton, BorderLayout.SOUTH);
	}
	
	public void repaint() {
		panel.repaint();
	}

	/* Instance ------------------------------------------------------------- */

	public Board(String filename) {
		ArrayList<String[]> fileArray = new ArrayList<String[]>();
		File file = new File("Tables/" + filename);
		coinBag = new LinkedList<Integer>();
		road = new ArrayList<Integer>();
		entrance = exit = null;
		FileReader fileReader;
		BufferedReader reader;
		message = "";
		String line;

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

		X_LENGTH = fileArray.get(0).length;
		Y_LENGTH = fileArray.size();
		
		Constants.WINDOW_HEIGHT = 650 + (X_LENGTH * Constants.CELL_WIDTH + Constants.MARGIN * 2);
		Constants.WINDOW_WIDTH = 30 + (Y_LENGTH * Constants.CELL_WIDTH + (Constants.MARGIN * 2) + (Constants.MARGIN / 2));

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
		hidden = new double[Constants.CHROMOSOME_SIZE];
		output = new double[Constants.CHROMOSOME_SIZE];
		Arrays.fill(inputLayer, "   ?");
		Arrays.fill(hidden, 0.0);
		Arrays.fill(output, 0.0);

		blocks = copyArray(blank);
	}

}