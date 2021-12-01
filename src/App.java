package src;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

import src.ag.GeneticAlgorith;
import src.utils.Constants;
import src.ui.TablePanel;
import src.ui.ListPanel;
import src.ui.Table;

public class App {

	public static final int delay_f	= 500;	//ms
	public static final int begin 	= 1500; //ms
	public static final int between = 3000; //ms
    public static volatile boolean DEBUG;
	public static int DELAY; //ms


	private static JFrame frame;
	public static void main(String[] args) throws IOException, InterruptedException {
		String instructions = "exec.exe <maze file> <number of generations> <population size> <mutation rate>";
		Table table = null;
		
		DELAY = Constants.DELAY_DEFAULT;
		DEBUG = Constants.DEBUG;
			
		/*  Magic happens here ------------------------ */
		try {
			close();
			//Throws exception if needed
			instructions = args[3];
			table = init(args[0], "Genetic Algorith + Neural Network");
			Thread.sleep(begin);
			new GeneticAlgorith(
				table,
		 		Integer.parseInt(args[1]),
		 		Integer.parseInt(args[2]),
		 		Integer.parseInt(args[3])
		 	);
		} catch (ArrayIndexOutOfBoundsException e) {
			if(args.length != 1) {
				System.out.println(e.getMessage());
				System.out.println(instructions);
				e.printStackTrace();
			}
		}
	}

	private static Table init(String tableFile, String windowName) {
		Table table = Table.newInstance(tableFile);
		if(!Constants.DISPLAY) return table;

		frame = new JFrame(windowName);
		TablePanel panel = new TablePanel();
		ListPanel panel2 = ListPanel.getInstance();
		panel2.setSize(new Dimension(300, 299));
		JScrollPane scrollPane = new JScrollPane(panel);
		JScrollPane scrollPane2 = new JScrollPane(panel2);
		scrollPane2.setPreferredSize(new Dimension(300, 300));
		frame.setSize(table.getXLength(), table.getYLength());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.add(scrollPane2, BorderLayout.EAST);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		return table;
	}

	private static void close() {
		try {
			frame.dispatchEvent(
				new WindowEvent(
					frame,
					WindowEvent.WINDOW_CLOSING
				)
			);
		} catch (Exception ignore) {}
	}
}