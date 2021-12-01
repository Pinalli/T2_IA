package src.ui;

import javax.swing.border.EtchedBorder;
import javax.swing.border.Border;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

import java.util.Arrays;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;


public class ListPanel extends JPanel {
	private DefaultListModel<String> model;
	private JList list;

	private static ListPanel instance;

	public static ListPanel getInstance() {
		if(instance == null)
			instance = new ListPanel();
		return instance;
	}

	private ListPanel() {
		super(new FlowLayout(FlowLayout.LEFT));
		this.model = new DefaultListModel<String>();
		this.list = new JList<String>(model);

		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setBorder(BorderFactory.createTitledBorder("Generation: -"));
		this.add(list, BorderLayout.WEST);

		Arrays.stream(list.getMouseListeners())
			.forEach(ml -> list.removeMouseListener(ml));
	}

	public void setSize(int size) {
		this.setPreferredSize(new Dimension(280, 2*size));
	}
	public void setActive(int index, int generation) {
		this.setBorder(BorderFactory.createTitledBorder("Generation: " + generation));
		list.setSelectedIndex(index);
	}

	public void add(int index, double e) {
		String line = "Chromosome " + index + ": [ "+e+" ]";
		
		if(this.model.getSize()-1 < index)
			this.model.add(index, line);
		else
			this.model.set(index, line);
		instance.repaint();
	}

	public void paintComponent(Graphics page) {
		super.paintComponent(page);		
		setBackground(Color.white);
	}

}