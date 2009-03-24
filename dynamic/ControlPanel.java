//    Authors:   pittacus@gmail.com (Fei Li)
//
//    Copyright 2009, Fei Li. All Rights Reserved.
//
//    This file is part of PerturbationAnalyzer.
//
//    PerturbationAnalyzer is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    PerturbationAnalyzer is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with PerturbationAnalyzer.  If not, see <http://www.gnu.org/licenses/>.

package dynamic;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.*;
/*
 * Created by JFormDesigner on Tue Mar 10 21:28:05 CST 2009
 */
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;



/**
 * @author PITTACUS
 */
public class ControlPanel extends JPanel{
	public ControlPanel() {
		initComponents();

		quickLabel.setText("<html><FONT COLOR=BLUE>Quick Start</FONT></html>");

		quickLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if(evt.getClickCount() > 0){
					try {
						Process pc = Runtime.getRuntime().exec("cmd.exe /c start http://www.baidu.com");
					} catch (IOException ex) {
						System.out.println(ex.getMessage());
						System.out.println();
					}
				}
			}
		});
		
		helpLabel.setText("<html><FONT COLOR=BLUE>Help</FONT></html>");
		helpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if(evt.getClickCount() > 0){
					try {
						Process pc = Runtime.getRuntime().exec("cmd.exe /c start http://www.google.com");
					} catch (IOException ex) {
						System.out.println(ex.getMessage());
						System.out.println();
					}
				}
			}
		});
        iterativeCriteria.getDocument().addDocumentListener(
        	new  DocumentListener() {
		        public void changedUpdate(DocumentEvent e) {f();}
		        public void insertUpdate(DocumentEvent e) {f();}
		        public void removeUpdate(DocumentEvent e) {f();}
		        void f() {
		        	try{
		        		Double.parseDouble(iterativeCriteria.getText());
		        		iterativeCriteria.setBackground(Color.WHITE);
		        	}catch(Exception e){
		        		iterativeCriteria.setBackground(Color.YELLOW);
		        	}
		        }
	    });
        this.changeFoldChoice.getEditor().getEditorComponent().addKeyListener(
            	new  KeyAdapter() {
    		        public void keyReleased(KeyEvent event){
    		        	try{
    		        		Integer.parseInt(changeFoldChoice.getEditor().getItem().toString());
    		        		changeFoldChoice.setBackground(Color.WHITE);
    		        	}catch(Exception e){
    		        		changeFoldChoice.setBackground(Color.YELLOW);
    		        	}
    		        }
    	    });
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		JPanel configPanel = new JPanel();
		JLabel label1 = new JLabel();
		panel2 = new JPanel();
		proteinAbundanceChoice = new JComboBox();
		JLabel label3 = new JLabel();
		panel3 = new JPanel();
		resultChoice = new JComboBox();
		JLabel label7 = new JLabel();
		panel10 = new JPanel();
		perturbationType = new JComboBox();
		JLabel label5 = new JLabel();
		panel4 = new JPanel();
		changeFoldChoice = new JComboBox();
		JLabel label8 = new JLabel();
		panel11 = new JPanel();
		iterativeCriteria = new JTextField();
		JPanel configPanel2 = new JPanel();
		JLabel label2 = new JLabel();
		panel7 = new JPanel();
		startCalculate = new JButton();
		JPanel configPanel3 = new JPanel();
		JLabel label6 = new JLabel();
		panel5 = new JPanel();
		resultAnalyseChoice = new JComboBox();
		JLabel label4 = new JLabel();
		panel9 = new JPanel();
		startAnalysis = new JButton();
		panel1 = new JPanel();
		scrollPane1 = new JScrollPane();
		infoText = new JTextArea();
		panel6 = new JPanel();
		helpLabel = new JLabel();
		quickLabel = new JLabel();

		setAlignmentX(0.0F);
		setAlignmentY(0.0F);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		configPanel.setBorder(new CompoundBorder(
			new TitledBorder("Model Configuration"),
			new EmptyBorder(5, 5, 5, 5)));
		configPanel.setLayout(new GridLayout(0, 1, 5, 5));

		label1.setText("Load as total protein concentration (double type)");
		label1.setLabelFor(proteinAbundanceChoice);
		label1.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label1.setHorizontalAlignment(SwingConstants.LEFT);
		label1.setAlignmentX(0.5F);
		configPanel.add(label1);

		panel2.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel2.setLayout(new GridLayout());

		proteinAbundanceChoice.setMinimumSize(new Dimension(10, 21));
		proteinAbundanceChoice.setMaximumSize(new Dimension(600, 21));
		panel2.add(proteinAbundanceChoice);
		configPanel.add(panel2);

		label3.setText("Save results save as (prefix) ...");
		label3.setLabelFor(resultChoice);
		label3.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label3.setHorizontalAlignment(SwingConstants.LEFT);
		label3.setAlignmentX(0.5F);
		configPanel.add(label3);

		panel3.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel3.setLayout(new GridLayout());

		resultChoice.setMinimumSize(new Dimension(10, 21));
		resultChoice.setMaximumSize(new Dimension(600, 21));
		resultChoice.setEditable(true);
		panel3.add(resultChoice);
		configPanel.add(panel3);

		label7.setText("Perturbation type ...");
		label7.setLabelFor(perturbationType);
		label7.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label7.setHorizontalAlignment(SwingConstants.LEFT);
		label7.setAlignmentX(0.5F);
		configPanel.add(label7);

		panel10.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel10.setLayout(new GridLayout());

		perturbationType.setMinimumSize(new Dimension(10, 21));
		perturbationType.setMaximumSize(new Dimension(600, 21));
		perturbationType.setModel(new DefaultComboBoxModel(new String[] {
			"perturbate all proteins in same time",
			"perturbate each protein at one time"
		}));
		perturbationType.setMaximumRowCount(20);
		panel10.add(perturbationType);
		configPanel.add(panel10);

		label5.setText("Set selected nodes change fold ...");
		label5.setLabelFor(changeFoldChoice);
		label5.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label5.setHorizontalAlignment(SwingConstants.LEFT);
		label5.setAlignmentX(0.5F);
		configPanel.add(label5);

		panel4.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel4.setLayout(new GridLayout());

		changeFoldChoice.setMinimumSize(new Dimension(10, 21));
		changeFoldChoice.setMaximumSize(new Dimension(600, 21));
		changeFoldChoice.setModel(new DefaultComboBoxModel(new String[] {
			"10",
			"9",
			"8",
			"7",
			"6",
			"5",
			"4",
			"3",
			"2",
			"-2",
			"-3",
			"-4",
			"-5",
			"-6",
			"-7",
			"-8",
			"-9",
			"-10"
		}));
		changeFoldChoice.setMaximumRowCount(20);
		changeFoldChoice.setSelectedIndex(9);
		changeFoldChoice.setEditable(true);
		panel4.add(changeFoldChoice);
		configPanel.add(panel4);

		label8.setText("Iterative criteria ...");
		label8.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label8.setHorizontalAlignment(SwingConstants.LEFT);
		label8.setAlignmentX(0.5F);
		label8.setToolTipText("Stop iteration when the residuals of the equations less than the iterative criteria");
		configPanel.add(label8);

		panel11.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel11.setLayout(new GridLayout());

		iterativeCriteria.setText("1e-10");
		panel11.add(iterativeCriteria);
		configPanel.add(panel11);
		add(configPanel);

		configPanel2.setBorder(new CompoundBorder(
			new TitledBorder("Model Calculation"),
			new EmptyBorder(5, 5, 5, 5)));
		configPanel2.setLayout(new GridLayout(0, 1, 5, 5));

		label2.setText("Start calculation for selected protein...");
		label2.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label2.setHorizontalAlignment(SwingConstants.LEFT);
		label2.setAlignmentX(0.5F);
		configPanel2.add(label2);

		panel7.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel7.setLayout(new GridLayout());

		startCalculate.setText("Calculate");
		startCalculate.setAlignmentX(0.5F);
		panel7.add(startCalculate);
		configPanel2.add(panel7);
		add(configPanel2);

		configPanel3.setBorder(new CompoundBorder(
			new TitledBorder("Model Analysis"),
			new EmptyBorder(5, 5, 5, 5)));
		configPanel3.setLayout(new GridLayout(0, 1, 5, 5));

		label6.setText("Saved results (prefix) ...");
		label6.setLabelFor(resultAnalyseChoice);
		label6.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label6.setHorizontalAlignment(SwingConstants.LEFT);
		label6.setAlignmentX(0.5F);
		configPanel3.add(label6);

		panel5.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel5.setLayout(new GridLayout());

		resultAnalyseChoice.setMinimumSize(new Dimension(10, 21));
		resultAnalyseChoice.setMaximumSize(new Dimension(600, 21));
		panel5.add(resultAnalyseChoice);
		configPanel3.add(panel5);

		label4.setText("Start analysis & visualize ...");
		label4.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label4.setHorizontalAlignment(SwingConstants.LEFT);
		label4.setAlignmentX(0.5F);
		configPanel3.add(label4);

		panel9.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel9.setLayout(new GridLayout());

		startAnalysis.setText("Analyse");
		startAnalysis.setAlignmentX(0.5F);
		panel9.add(startAnalysis);
		configPanel3.add(panel9);
		add(configPanel3);

		panel1.setLayout(new BorderLayout());


		infoText.setEditable(false);
		scrollPane1.setViewportView(infoText);
		panel1.add(scrollPane1, BorderLayout.CENTER);

		panel6.setBorder(new EmptyBorder(0, 10, 0, 10));
		panel6.setLayout(new BorderLayout());

		helpLabel.setText("Help");
		panel6.add(helpLabel, BorderLayout.EAST);

		quickLabel.setText("Quick Start");
		panel6.add(quickLabel, BorderLayout.WEST);
		panel1.add(panel6, BorderLayout.SOUTH);
		add(panel1);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JPanel panel2;
	public JComboBox proteinAbundanceChoice;
	private JPanel panel3;
	public JComboBox resultChoice;
	private JPanel panel10;
	public JComboBox perturbationType;
	private JPanel panel4;
	public JComboBox changeFoldChoice;
	private JPanel panel11;
	public JTextField iterativeCriteria;
	private JPanel panel7;
	public JButton startCalculate;
	private JPanel panel5;
	public JComboBox resultAnalyseChoice;
	private JPanel panel9;
	public JButton startAnalysis;
	private JPanel panel1;
	private JScrollPane scrollPane1;
	public JTextArea infoText;
	private JPanel panel6;
	private JLabel helpLabel;
	private JLabel quickLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
