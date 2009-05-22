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
import javax.swing.event.*;
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
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if(evt.getClickCount() > 0){
					try {
						Runtime.getRuntime().exec("cmd.exe /c start http://www.baidu.com");
					} catch (IOException ex) {
						System.out.println(ex.getMessage());
						System.out.println();
					}
				}
			}
		});
		
		helpLabel.setText("<html><FONT COLOR=BLUE>Help</FONT></html>");
		helpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if(evt.getClickCount() > 0){
					try {
						Runtime.getRuntime().exec("cmd.exe /c start http://www.google.com");
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
        changeFoldChoice.getEditor().getEditorComponent().addKeyListener(
            	new  KeyAdapter() {
    		        @Override
					public void keyReleased(KeyEvent event){
    		        	try{
    		        		Double.parseDouble(changeFoldChoice.getEditor().getItem().toString());
    		        		changeFoldChoice.setBackground(Color.WHITE);
    		        	}catch(Exception e){
    		        		changeFoldChoice.setBackground(Color.YELLOW);
    		        	}
    		        }
    	    });
        dissociationConstant.getDocument().addDocumentListener(
            	new  DocumentListener() {
    		        public void changedUpdate(DocumentEvent e) {f();}
    		        public void insertUpdate(DocumentEvent e) {f();}
    		        public void removeUpdate(DocumentEvent e) {f();}
    		        void f() {
    		        	try{
    		        		if(dissociationConstant.getText().compareTo(Config.DEFAULT_DISSOCIATION_CONSTANT)!=0)
    		        			Double.parseDouble(dissociationConstant.getText());
    		        		dissociationConstant.setBackground(Color.WHITE);
    		        	}catch(Exception e){
    		        		dissociationConstant.setBackground(Color.YELLOW);
    		        	}
    		        }
    	    });
        ButtonGroup group = new ButtonGroup();
        group.add(singleMode);
        group.add(batchMode);
        
        advancedOptionStateChanged(null);
        resultNameFocusLost(null);
  	}
	public int getPerturbationType(){
		if(singleMode.isSelected()){
			return Config.PERTURBATION_SINGLE;
		}
		return Config.PERTURBATION_BATCH;
	}

	public String getResultName(){
		return "Perturbation.result."+Perturbation.now();
	}
	
	private void advancedOptionStateChanged(ChangeEvent e) {
		boolean show = advancedOption.isSelected();
		advancedOptionPanel.setVisible(show);
		Icon icon = (Icon)(show?UIManager.get("Tree.expandedIcon"):UIManager.get("Tree.collapsedIcon"));
		advancedOption.setIcon(icon);
	}

	private void dissociationConstantFocusGained(FocusEvent e) {
		if(dissociationConstant.getText().compareTo(Config.DEFAULT_DISSOCIATION_CONSTANT)==0){
			dissociationConstant.setText("");
			dissociationConstant.setForeground(Color.BLACK);
		}
		else {
			dissociationConstant.selectAll();
		}
	}

	private void dissociationConstantFocusLost(FocusEvent e) {
		if(dissociationConstant.getText().trim().length()==0){
			dissociationConstant.setText(Config.DEFAULT_DISSOCIATION_CONSTANT);
			dissociationConstant.setForeground(Color.LIGHT_GRAY);
		}
	}

	private void resultNameFocusGained(FocusEvent e) {
		if(resultName.getText().compareTo(Config.DEFAULT_RESULT_NAME)==0){
			resultName.setText("");
			resultName.setForeground(Color.BLACK);
		}
		else {
			resultName.selectAll();
		}
	}

	private void resultNameFocusLost(FocusEvent e) {
		if(resultName.getText().trim().length()==0){
			resultName.setText(Config.DEFAULT_RESULT_NAME);
			resultName.setForeground(Color.LIGHT_GRAY);
		}
	}
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		JPanel configPanel = new JPanel();
		panel2 = new JPanel();
		singleMode = new JRadioButton();
		panel18 = new JPanel();
		textArea2 = new JTextArea();
		panel5 = new JPanel();
		batchMode = new JRadioButton();
		panel17 = new JPanel();
		textArea3 = new JTextArea();
		configPanel4 = new JPanel();
		panel3 = new JPanel();
		JLabel label9 = new JLabel();
		panel8 = new JPanel();
		proteinAbundanceChoice = new JComboBox();
		panel4 = new JPanel();
		advancedOption = new JCheckBox();
		hSpacer1 = new JPanel(null);
		advancedOptionPanel = new JPanel();
		JLabel label10 = new JLabel();
		panel12 = new JPanel();
		resultName = new JTextField();
		label12 = new JLabel();
		panel14 = new JPanel();
		changeFoldChoice = new JComboBox();
		label14 = new JLabel();
		panel16 = new JPanel();
		dissociationConstant = new JTextField();
		label13 = new JLabel();
		panel15 = new JPanel();
		iterativeCriteria = new JTextField();
		JPanel configPanel2 = new JPanel();
		JLabel label2 = new JLabel();
		panel7 = new JPanel();
		startCalculate = new JButton();
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
			new TitledBorder("Perturbation Type"),
			new EmptyBorder(5, 5, 5, 5)));
		configPanel.setFont(UIManager.getFont("Label.font"));
		configPanel.setLayout(new GridLayout(0, 1, 5, 0));

		panel2.setLayout(new BorderLayout());

		singleMode.setText("Single Perturbation Mode");
		singleMode.setSelected(true);
		singleMode.setFont(UIManager.getFont("Label.font"));
		panel2.add(singleMode, BorderLayout.NORTH);

		panel18.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel18.setLayout(new GridLayout());

		textArea2.setEditable(false);
		textArea2.setEnabled(false);
		textArea2.setOpaque(false);
		textArea2.setText("1. Perturbate all proteins in same time\n2. Propagation analysis in one perturbation");
		textArea2.setFont(UIManager.getFont("Label.font"));
		panel18.add(textArea2);
		panel2.add(panel18, BorderLayout.SOUTH);
		configPanel.add(panel2);

		panel5.setLayout(new BorderLayout());

		batchMode.setText("Batch  Perturbation Mode");
		batchMode.setFont(UIManager.getFont("Label.font"));
		panel5.add(batchMode, BorderLayout.NORTH);

		panel17.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel17.setLayout(new GridLayout());

		textArea3.setEditable(false);
		textArea3.setEnabled(false);
		textArea3.setOpaque(false);
		textArea3.setText("1. Perturbate one protein at a time\n2. Propagation analysis for many perturbations");
		textArea3.setFont(UIManager.getFont("Label.font"));
		panel17.add(textArea3);
		panel5.add(panel17, BorderLayout.SOUTH);
		configPanel.add(panel5);
		add(configPanel);

		configPanel4.setBorder(new CompoundBorder(
			new TitledBorder("Model Configuration"),
			new EmptyBorder(5, 5, 5, 5)));
		configPanel4.setFont(UIManager.getFont("Label.font"));
		configPanel4.setLayout(new BoxLayout(configPanel4, BoxLayout.PAGE_AXIS));

		panel3.setLayout(new GridLayout(0, 1));

		label9.setText("Select total protein concentration");
		label9.setLabelFor(proteinAbundanceChoice);
		label9.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label9.setHorizontalAlignment(SwingConstants.LEFT);
		label9.setAlignmentX(0.5F);
		label9.setFont(UIManager.getFont("Label.font"));
		label9.setToolTipText("It must be a double typed node attribute. You can import it from text file or excel by Cytoscape");
		panel3.add(label9);

		panel8.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel8.setLayout(new GridLayout());

		proteinAbundanceChoice.setMinimumSize(new Dimension(10, 21));
		proteinAbundanceChoice.setMaximumSize(new Dimension(600, 21));
		proteinAbundanceChoice.setFont(UIManager.getFont("Label.font"));
		panel8.add(proteinAbundanceChoice);
		panel3.add(panel8);
		configPanel4.add(panel3);

		panel4.setLayout(new BoxLayout(panel4, BoxLayout.X_AXIS));

		advancedOption.setText("Advanced Options");
		advancedOption.setIcon(UIManager.getIcon("Tree.collapsedIcon"));
		advancedOption.setFont(UIManager.getFont("Label.font"));
		advancedOption.setToolTipText("Click to expand or collapse advanced options");
		advancedOption.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				advancedOptionStateChanged(e);
			}
		});
		panel4.add(advancedOption);
		panel4.add(hSpacer1);
		configPanel4.add(panel4);

		advancedOptionPanel.setLayout(new GridLayout(0, 1));

		label10.setText("Save analysis results as");
		label10.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label10.setHorizontalAlignment(SwingConstants.LEFT);
		label10.setAlignmentX(0.5F);
		label10.setFont(UIManager.getFont("Label.font"));
		label10.setToolTipText("The result is stored as network attribute and node attributes, which can be exported by Cytoscape");
		advancedOptionPanel.add(label10);

		panel12.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel12.setLayout(new GridLayout());

		resultName.setText("Auto Result.{CURRENT DATETIME}");
		resultName.setForeground(Color.lightGray);
		resultName.setFont(UIManager.getFont("Label.font"));
		resultName.setToolTipText("The result is stored as network attribute and node attributes, which can be exported by Cytoscape");
		resultName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				resultNameFocusGained(e);
			}
			@Override
			public void focusLost(FocusEvent e) {
				resultNameFocusLost(e);
			}
		});
		panel12.add(resultName);
		advancedOptionPanel.add(panel12);

		label12.setText("Perturbation intensity (Change fold)");
		label12.setLabelFor(changeFoldChoice);
		label12.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label12.setHorizontalAlignment(SwingConstants.LEFT);
		label12.setAlignmentX(0.5F);
		label12.setFont(UIManager.getFont("Label.font"));
		label12.setToolTipText("2 means double abundance change, and -2 means one half abundance change");
		advancedOptionPanel.add(label12);

		panel14.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel14.setLayout(new GridLayout());

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
		changeFoldChoice.setSelectedIndex(8);
		changeFoldChoice.setEditable(true);
		changeFoldChoice.setFont(UIManager.getFont("Label.font"));
		changeFoldChoice.setToolTipText("2 means double abundance change, and -2 means one half abundance change");
		panel14.add(changeFoldChoice);
		advancedOptionPanel.add(panel14);

		label14.setText("Dissociation constant (K, um/L) ");
		label14.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label14.setHorizontalAlignment(SwingConstants.LEFT);
		label14.setAlignmentX(0.5F);
		label14.setToolTipText("The default value is suitable in most common cases");
		label14.setFont(UIManager.getFont("Label.font"));
		advancedOptionPanel.add(label14);

		panel16.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel16.setLayout(new GridLayout());

		dissociationConstant.setText("MAX(Ci,Cj)/20");
		dissociationConstant.setForeground(Color.lightGray);
		dissociationConstant.setFont(UIManager.getFont("Label.font"));
		dissociationConstant.setToolTipText("The default value is suitable in most common cases");
		dissociationConstant.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				dissociationConstantFocusGained(e);
			}
			@Override
			public void focusLost(FocusEvent e) {
				dissociationConstantFocusLost(e);
			}
		});
		panel16.add(dissociationConstant);
		advancedOptionPanel.add(panel16);

		label13.setText("Iterative criteria (EPS)");
		label13.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label13.setHorizontalAlignment(SwingConstants.LEFT);
		label13.setAlignmentX(0.5F);
		label13.setToolTipText("Stop iteration when the residuals of the equations less than the iterative criteria");
		label13.setFont(UIManager.getFont("Label.font"));
		advancedOptionPanel.add(label13);

		panel15.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel15.setLayout(new GridLayout());

		iterativeCriteria.setText("1e-10");
		iterativeCriteria.setFont(UIManager.getFont("Label.font"));
		iterativeCriteria.setToolTipText("Stop iteration when the residuals of the equations less than the iterative criteria");
		panel15.add(iterativeCriteria);
		advancedOptionPanel.add(panel15);
		configPanel4.add(advancedOptionPanel);
		add(configPanel4);

		configPanel2.setBorder(new CompoundBorder(
			new TitledBorder("Model Calculation"),
			new EmptyBorder(5, 5, 5, 5)));
		configPanel2.setFont(UIManager.getFont("Label.font"));
		configPanel2.setLayout(new GridLayout(0, 1, 5, 5));

		label2.setText("Start calculation for selected protein(s) ...");
		label2.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label2.setHorizontalAlignment(SwingConstants.LEFT);
		label2.setAlignmentX(0.5F);
		label2.setFont(UIManager.getFont("Label.font"));
		configPanel2.add(label2);

		panel7.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel7.setLayout(new GridLayout());

		startCalculate.setText("Calculate");
		startCalculate.setAlignmentX(0.5F);
		startCalculate.setFont(UIManager.getFont("Button.font"));
		panel7.add(startCalculate);
		configPanel2.add(panel7);
		add(configPanel2);

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
	public JRadioButton singleMode;
	private JPanel panel18;
	private JTextArea textArea2;
	private JPanel panel5;
	private JRadioButton batchMode;
	private JPanel panel17;
	private JTextArea textArea3;
	private JPanel configPanel4;
	private JPanel panel3;
	private JPanel panel8;
	public JComboBox proteinAbundanceChoice;
	private JPanel panel4;
	private JCheckBox advancedOption;
	private JPanel hSpacer1;
	private JPanel advancedOptionPanel;
	private JPanel panel12;
	private JTextField resultName;
	private JLabel label12;
	private JPanel panel14;
	public JComboBox changeFoldChoice;
	private JLabel label14;
	private JPanel panel16;
	public JTextField dissociationConstant;
	private JLabel label13;
	private JPanel panel15;
	public JTextField iterativeCriteria;
	private JPanel panel7;
	public JButton startCalculate;
	private JPanel panel1;
	private JScrollPane scrollPane1;
	public JTextArea infoText;
	private JPanel panel6;
	private JLabel helpLabel;
	private JLabel quickLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
