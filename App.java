import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class App {

	public static final int delay 	= 2; 	//ms
	public static final int delay_f	= 500;	//ms
	public static final int begin 	= 1500; //ms
	public static final int between = 3000; //ms

	public static final boolean PDM = false;
	public static final boolean DISPLAY = false;
	public static final boolean LEFT_WHEN_FIND_FIRST = true;

	private static JFrame frame;
	public static void main(String[] args) throws IOException, InterruptedException {
		String instructions = "exec.exe <maze file> <number of generations> <population size> <mutation rate>";
		Table table = null;
			
		/*  Magic happens here ------------------------ */
		if(false)
			try {
				table = init(args[0], "Path finder - A*");
				Thread.sleep(begin);
				new PathFinderAStar(table)
						.findPath();
									
				Thread.sleep(between);
			} catch (ArrayIndexOutOfBoundsException aioobe) {
				System.out.println(instructions);
				System.exit(1);
			} catch (Exception e) {
				e.printStackTrace();
			}

		if(true)
			try {
				close();
				//Throws exception if needed
				instructions = args[3];
				table = init(args[0], "Genetic Algorith");
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
		if(!DISPLAY) return table;

		frame = new JFrame(windowName);
		TablePanel panel = new TablePanel();
		JScrollPane scrollPane = new JScrollPane(panel);

		frame.setSize(table.getXLength(), table.getYLength());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.add(scrollPane, BorderLayout.CENTER);
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

class TablePanel extends JPanel {
	private Table table;

	public TablePanel() {
		this.table = Table.instance(new String());
		table.setPanel(this);
	}

	public void paintComponent(Graphics page) {
		this.setPreferredSize(table.windowSize());
		super.paintComponent(page);		
		setBackground(Color.white);
		table.draw(page);
	}
}
