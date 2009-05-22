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
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
/*
 * Created by JFormDesigner on Thu Mar 12 15:15:39 CST 2009
 */ 



/**
 * @author PITTACUS
 */
public class ResultPanel extends JPanel {
	Perturbation perturbation;
	String freeConcentrations;
	Parameter parameter;
	
	public ResultPanel(Perturbation p, Parameter para) {
		initComponents();
		perturbation = p;
		parameter = para;
		setName(parameter.result);
		cutoffSliderStateChanged(null);
	}

	private void cutoffSliderStateChanged(ChangeEvent e) {
		DefaultTableModel model = (DefaultTableModel) subnetInfo.getModel();
		perturbation.highlightSubnetwork(model, parameter, cutoffSlider.getValue()/100.0D, false);
	}

	private void highlightButtonActionPerformed(ActionEvent e) {
		DefaultTableModel model = (DefaultTableModel) subnetInfo.getModel();
		perturbation.highlightSubnetwork(model, parameter, cutoffSlider.getValue()/100.0D, true);
	}

	private void unhighlightButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		splitPane = new JSplitPane();
		panel1 = new JPanel();
		panel2 = new JPanel();
		panel4 = new JPanel();
		label1 = new JLabel();
		panel3 = new JPanel();
		highlightButton = new JButton();
		unhighlightButton = new JButton();
		cutoffSlider = new JSlider();
		scrollPane1 = new JScrollPane();
		subnetInfo = new JTable();
		rightPane = new JTabbedPane();

		setLayout(new BorderLayout());


		panel1.setBorder(new TitledBorder("Sub Network Creator"));
		panel1.setLayout(new BorderLayout(0, 5));

		panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));

		panel4.setLayout(new BorderLayout(10, 0));

		label1.setText("Drag the slider to set cutoff (%)");
		label1.setLabelFor(cutoffSlider);
		panel4.add(label1, BorderLayout.CENTER);

		panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

		highlightButton.setText("Highlight");
		highlightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				highlightButtonActionPerformed(e);
			}
		});
		panel3.add(highlightButton);

		unhighlightButton.setText("Unhighlight");
		unhighlightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				unhighlightButtonActionPerformed(e);
			}
		});
		panel3.add(unhighlightButton);
		panel4.add(panel3, BorderLayout.EAST);
		panel2.add(panel4);

		cutoffSlider.setMinorTickSpacing(1);
		cutoffSlider.setPaintTicks(true);
		cutoffSlider.setPaintLabels(true);
		cutoffSlider.setMajorTickSpacing(10);
		cutoffSlider.setValue(20);
		cutoffSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				cutoffSliderStateChanged(e);
			}
		});
		panel2.add(cutoffSlider);


		subnetInfo.setModel(new DefaultTableModel(
			new Object[][] {
				{"Cutoff", null},
				{"Network Size", "2"},
				{"Network Diameter", "4"},
				{"Average Change Rate", "1.23"},
				{"--- Protein List ---", "--- Fold ---"},
			},
			new String[] {
				"Property", "Value"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class
			};
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		{
			TableColumnModel cm = subnetInfo.getColumnModel();
			cm.getColumn(0).setPreferredWidth(140);
		}
		scrollPane1.setViewportView(subnetInfo);
		panel2.add(scrollPane1);
		panel1.add(panel2, BorderLayout.CENTER);
		splitPane.setLeftComponent(panel1);

		rightPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		splitPane.setRightComponent(rightPane);
		add(splitPane, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	public JSplitPane splitPane;
	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel4;
	private JLabel label1;
	private JPanel panel3;
	private JButton highlightButton;
	private JButton unhighlightButton;
	private JSlider cutoffSlider;
	private JScrollPane scrollPane1;
	private JTable subnetInfo;
	public JTabbedPane rightPane;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
