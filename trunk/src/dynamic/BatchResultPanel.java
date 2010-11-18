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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
/*
 * Created by JFormDesigner on Sun Aug 09 16:20:33 CST 2009
 */

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;



/**
 * @author SHOCKIE
 */
public class BatchResultPanel extends JPanel {
	Perturbation perturbation;
	String freeConcentrations;
	Parameter parameter;

	public BatchResultPanel(Perturbation p, Parameter para) {
		initComponents();
		perturbation = p;
		parameter = para;
		setName(parameter.result);
		initComponents();
		DefaultTableModel model = (DefaultTableModel) subnetInfo.getModel();
		perturbation.highlightSubnetwork(model, parameter, 1, false);
		subnetInfo.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						if (e.getValueIsAdjusting())
							return;
						DefaultTableModel model = (DefaultTableModel) subnetInfo
								.getModel();
						int first = e.getFirstIndex();
						int last = e.getLastIndex();
						System.out.printf("%s %d %d\n", e.toString(), first,
								last);
						CyNetwork network = Cytoscape.getCurrentNetwork();
						CyAttributes attributes = Cytoscape.getNodeAttributes();
						network.unselectAllNodes();
						
						HashSet s = new HashSet();
						for (int k:subnetInfo.getSelectedRows()) {
							s.add(model.getValueAt(k, 0));
						}
						for (Iterator i = network.nodesIterator(); i.hasNext();) {
							CyNode node = (CyNode) i.next();
							String id = node.getIdentifier();
							if(s.contains(id)){
//								System.out.println(node);
								network.setSelectedNodeState(node, true);			
							}
						}
						Cytoscape.getCurrentNetworkView().redrawGraph(false, false);
					}
				});
		}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		splitPane = new JSplitPane();
		panel1 = new JPanel();
		panel2 = new JPanel();
		panel4 = new JPanel();
		panel3 = new JPanel();
		scrollPane1 = new JScrollPane();
		subnetInfo = new JTable();
		rightPane = new JPanel();

		setLayout(new BorderLayout());


		panel1.setBorder(new TitledBorder("Perturbed subgroup"));
		panel1.setLayout(new BorderLayout(0, 5));

		panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));

		panel4.setLayout(new BorderLayout(10, 0));

		panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));
		panel4.add(panel3, BorderLayout.EAST);
		panel2.add(panel4);


		subnetInfo.setModel(new DefaultTableModel(
			new Object[][] {
				{"Threshold of perturbed subgroup", null},
				{"Total proteins", ""},
				{"Average of subgroup sizes", ""},
				{"Maximum of subgroup sizes", null},
				{"Minimum of subgroup sizes", null},
				{"--- Protein list ---", "--- Subgroup size ---"},
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

		rightPane.setBorder(new TitledBorder("Distribution of Perturbed Subgroup Sizes"));
		rightPane.setLayout(new BorderLayout());
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
	private JPanel panel3;
	private JScrollPane scrollPane1;
	private JTable subnetInfo;
	public JPanel rightPane;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
