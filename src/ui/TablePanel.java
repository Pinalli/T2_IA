package src.ui;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class TablePanel extends JPanel {
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