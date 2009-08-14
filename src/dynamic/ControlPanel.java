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
import javax.swing.event.*; /*
 * Created by JFormDesigner on Tue Mar 10 21:28:05 CST 2009
 */
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.lang.reflect.Method;
import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.ui.editors.continuous.ContinuousMappingEditorPanel;

import java.util.Arrays;

/**
 * @author PITTACUS
 */
public class ControlPanel extends JPanel {
	Perturbation perturbation;
	public ControlPanel(Perturbation p) {
		perturbation=p;
		initComponents();
		advancedOptionPanel.remove(panel16);
		quickLabel.setText("<html><FONT COLOR=BLUE>Quick Start</FONT></html>");

		quickLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if (evt.getClickCount() > 0) {
					openURL("http://biotech.bmi.ac.cn/PerturbationAnalyzer/tutorial.php");
				}
			}
		});

		helpLabel.setText("<html><FONT COLOR=BLUE>Help</FONT></html>");
		helpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if (evt.getClickCount() > 0) {
					openURL("http://biotech.bmi.ac.cn/PerturbationAnalyzer/help.php");
				}
			}
		});
		iterativeCriteria.getDocument().addDocumentListener(
				new DocumentListener() {
					public void changedUpdate(DocumentEvent e) {
						f();
					}

					public void insertUpdate(DocumentEvent e) {
						f();
					}

					public void removeUpdate(DocumentEvent e) {
						f();
					}

					void f() {
						try {
							Double.parseDouble(iterativeCriteria.getText());
							iterativeCriteria.setBackground(Color.WHITE);
						} catch (Exception e) {
							iterativeCriteria.setBackground(Color.YELLOW);
						}
					}
				});
		changeFoldChoice.getEditor().getEditorComponent().addKeyListener(
				new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent event) {
						try {
							Double.parseDouble(changeFoldChoice.getEditor()
									.getItem().toString());
							changeFoldChoice.setBackground(Color.WHITE);
						} catch (Exception e) {
							changeFoldChoice.setBackground(Color.YELLOW);
						}
					}
				});
		dissociationConstant2.getEditor().getEditorComponent().addKeyListener(
				new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent event) {
						try {
							Double.parseDouble(changeFoldChoice.getEditor()
									.getItem().toString());
							changeFoldChoice.setBackground(Color.WHITE);
						} catch (Exception e) {
							changeFoldChoice.setBackground(Color.YELLOW);
						}
					}
				});
		dissociationConstant.getDocument().addDocumentListener(
				new DocumentListener() {
					public void changedUpdate(DocumentEvent e) {
						f();
					}

					public void insertUpdate(DocumentEvent e) {
						f();
					}

					public void removeUpdate(DocumentEvent e) {
						f();
					}

					void f() {
						try {
							if (dissociationConstant.getText().compareTo(
									Config.DEFAULT_DISSOCIATION_CONSTANT) != 0)
								Double.parseDouble(dissociationConstant
										.getText());
							dissociationConstant.setBackground(Color.WHITE);
						} catch (Exception e) {
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

	public int getPerturbationType() {
		if (singleMode.isSelected()) {
			return Config.PERTURBATION_SINGLE;
		}
		return Config.PERTURBATION_BATCH;
	}

	public String getResultName() {
		if (resultName.getText().compareTo(Config.DEFAULT_RESULT_NAME) == 0) {
			return "Perturbation.result." + Perturbation.nowForFile(); 
		}
		else
			return resultName.getText();
	}

	//return 0 means MAX(Ci, Cj)/20
	public double getDissociationConstant() {
		Double[] map={0D,1D,10D,100D,1000D};
		int k= dissociationConstant2.getSelectedIndex();
		if(k<0)
			return Double.parseDouble(dissociationConstant2
				.getEditor().getItem().toString());
		else
			return map[k];
	}
	
	private void advancedOptionStateChanged(ChangeEvent e) {
		boolean show = advancedOption.isSelected();
		advancedOptionPanel.setVisible(show);
		Icon icon = (Icon) (show ? UIManager.get("Tree.expandedIcon")
				: UIManager.get("Tree.collapsedIcon"));
		advancedOption.setIcon(icon);
	}

	private void dissociationConstantFocusGained(FocusEvent e) {
		if (dissociationConstant.getText().compareTo(
				Config.DEFAULT_DISSOCIATION_CONSTANT) == 0) {
			dissociationConstant.setText("");
			dissociationConstant.setForeground(Color.BLACK);
		} else {
			dissociationConstant.selectAll();
		}
	}

	private void dissociationConstantFocusLost(FocusEvent e) {
		if (dissociationConstant.getText().trim().length() == 0) {
			dissociationConstant.setText(Config.DEFAULT_DISSOCIATION_CONSTANT);
			dissociationConstant.setForeground(Color.LIGHT_GRAY);
		}
	}

	private void resultNameFocusGained(FocusEvent e) {
		if (resultName.getText().compareTo(Config.DEFAULT_RESULT_NAME) == 0) {
			resultName.setText("");
			resultName.setForeground(Color.BLACK);
		} else {
			resultName.selectAll();
		}
	}

	private void resultNameFocusLost(FocusEvent e) {
		if (resultName.getText().trim().length() == 0) {
			resultName.setText(Config.DEFAULT_RESULT_NAME);
			resultName.setForeground(Color.LIGHT_GRAY);
		}
	}

	// The following openURL code from http://www.centerkey.com/java/browser/
	// Thanks, Bare Bones.
	static final String[] browsers = { "firefox", "opera", "konqueror",
			"epiphany", "seamonkey", "galeon", "kazehakase", "mozilla",
			"netscape" };

	private void openURL(String url) {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			} else if (osName.startsWith("Windows"))
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url);
			else { // assume Unix or Linux
				boolean found = false;
				for (String browser : browsers)
					if (!found) {
						found = Runtime.getRuntime().exec(
								new String[] { "which", browser }).waitFor() == 0;
						if (found)
							Runtime.getRuntime().exec(
									new String[] { browser, url });
					}
				if (!found)
					throw new Exception(Arrays.toString(browsers));
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Error attempting to launch web browser\n" + e.toString());
		}
	}

	private void legendActionPerformed(ActionEvent event) {
		try {
			VisualPropertyType.NODE_FILL_COLOR.showContinuousEditor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		perturbation.OnViewResult();
	}

	private void dissociationConstant2ItemStateChanged(ItemEvent e) {
//	       System.out.println("Selected: " + dissociationConstant2.getSelectedItem());
//	       System.out.println(", Position: " + dissociationConstant2.getSelectedIndex());
	 	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
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
		panel20 = new JPanel();
		dissociationConstant2 = new JComboBox();
		panel16 = new JPanel();
		dissociationConstant = new JTextField();
		label13 = new JLabel();
		panel15 = new JPanel();
		iterativeCriteria = new JTextField();
		label16 = new JLabel();
		panel19 = new JPanel();
		disturbedThreshold = new JTextField();
		JPanel configPanel2 = new JPanel();
		JLabel label2 = new JLabel();
		panel7 = new JPanel();
		startCalculate = new JButton();
		JPanel configPanel3 = new JPanel();
		panel9 = new JPanel();
		legend = new JButton();
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
		textArea2.setText("All selected nodes perturbated at the same time");
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
		textArea3.setText("One selected node perturbated at a time");
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

		label9.setText("Select total concentration");
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

		label10.setText("Analysis result name");
		label10.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label10.setHorizontalAlignment(SwingConstants.LEFT);
		label10.setAlignmentX(0.5F);
		label10.setFont(UIManager.getFont("Label.font"));
		label10.setToolTipText("The result is stored as network attribute and node attributes, which can be exported by Cytoscape");
		advancedOptionPanel.add(label10);

		panel12.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel12.setLayout(new GridLayout());

		resultName.setText("Perturbation.result.{CURRENT DATETIME}");
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

		label14.setText("Dissociation constant K (nM) ");
		label14.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label14.setHorizontalAlignment(SwingConstants.LEFT);
		label14.setAlignmentX(0.5F);
		label14.setToolTipText("The default value is suitable in most common cases");
		label14.setFont(UIManager.getFont("Label.font"));
		advancedOptionPanel.add(label14);

		panel20.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel20.setLayout(new GridLayout());

		dissociationConstant2.setForeground(Color.lightGray);
		dissociationConstant2.setFont(UIManager.getFont("Label.font"));
		dissociationConstant2.setToolTipText("The default value is suitable in most common cases");
		dissociationConstant2.setModel(new DefaultComboBoxModel(new String[] {
			"MAX(Ci, Cj)/20",
			"1",
			"10",
			"100",
			"1000"
		}));
		dissociationConstant2.setEditable(true);
		dissociationConstant2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				dissociationConstant2ItemStateChanged(e);
			}
		});
		panel20.add(dissociationConstant2);
		advancedOptionPanel.add(panel20);

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

		label16.setText("Subgroup threshold (%)");
		label16.setIcon(UIManager.getIcon("Tree.leafIcon"));
		label16.setHorizontalAlignment(SwingConstants.LEFT);
		label16.setAlignmentX(0.5F);
		label16.setToolTipText("These nodes with change rate > threshold are considered disturbed");
		label16.setFont(UIManager.getFont("Label.font"));
		advancedOptionPanel.add(label16);

		panel19.setBorder(new EmptyBorder(0, 15, 0, 0));
		panel19.setLayout(new GridLayout());

		disturbedThreshold.setText("20");
		disturbedThreshold.setFont(UIManager.getFont("Label.font"));
		disturbedThreshold.setToolTipText("Stop iteration when the residuals of the equations less than the iterative criteria");
		panel19.add(disturbedThreshold);
		advancedOptionPanel.add(panel19);
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

		startCalculate.setText("Start Analysis");
		startCalculate.setAlignmentX(0.5F);
		startCalculate.setFont(UIManager.getFont("Button.font"));
		panel7.add(startCalculate);
		configPanel2.add(panel7);
		add(configPanel2);

		configPanel3.setBorder(new CompoundBorder(
			new TitledBorder("Color Mapping"),
			new EmptyBorder(5, 5, 5, 5)));
		configPanel3.setFont(UIManager.getFont("Label.font"));
		configPanel3.setLayout(new GridLayout(0, 1, 5, 5));

		panel9.setBorder(BorderFactory.createEmptyBorder());
		panel9.setLayout(new GridLayout());

		legend.setAlignmentX(0.5F);
		legend.setFont(UIManager.getFont("Button.font"));
		legend.setMinimumSize(new Dimension(32, 32));
		legend.setMaximumSize(new Dimension(32, 32));
		legend.setPreferredSize(new Dimension(32, 64));
		legend.setMargin(new Insets(0, 0, 0, 0));
		legend.setBorder(null);
		legend.setBorderPainted(false);
		legend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				legendActionPerformed(e);
			}
		});
		panel9.add(legend);
		configPanel3.add(panel9);
		add(configPanel3);

		panel1.setLayout(new BorderLayout());


		infoText.setEditable(false);
		infoText.setFont(UIManager.getFont("Label.font"));
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
		// //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
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
	private JPanel panel20;
	public JComboBox dissociationConstant2;
	private JPanel panel16;
	public JTextField dissociationConstant;
	private JLabel label13;
	private JPanel panel15;
	public JTextField iterativeCriteria;
	private JLabel label16;
	private JPanel panel19;
	public JTextField disturbedThreshold;
	private JPanel panel7;
	public JButton startCalculate;
	private JPanel panel9;
	public JButton legend;
	private JPanel panel1;
	private JScrollPane scrollPane1;
	public JTextArea infoText;
	private JPanel panel6;
	private JLabel helpLabel;
	private JLabel quickLabel;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
