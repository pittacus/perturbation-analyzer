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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
/*
 * Created by JFormDesigner on Thu Mar 12 15:15:39 CST 2009
 */ 

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.util.CyNetworkNaming;
import cytoscape.view.CyNetworkView;



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
		cutoffSlider.setValue((int)(parameter.subgroupThreshold*100));
		cutoffSliderStateChanged(null);
		subnetInfo.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						if (e.getValueIsAdjusting())
							return;
						DefaultTableModel model = (DefaultTableModel) subnetInfo
								.getModel();
						int first = e.getFirstIndex();
						int last = e.getLastIndex();
//						System.out.printf("%s %d %d\n", e.toString(), first,
//								last);
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
						perturbation.OnViewResult();						
					}
				});
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
	}

	private void createSubgroupButtonActionPerformed(ActionEvent e) {
		highlightButtonActionPerformed(e);
		(new cytoscape.actions.NewWindowSelectedNodesOnlyAction()).actionPerformed(e);
//		String vs = Cytoscape.getCurrentNetworkView().getVisualStyle().getName();
//		CyNetwork network = Cytoscape.getCurrentNetwork();
//
//		Set nodes = network.getSelectedNodes();
//		Set edges = new HashSet();
//		Iterator iterator = network.edgesIterator();
//		while (iterator.hasNext()) {
//			CyEdge edge = (CyEdge) iterator.next();
//			if (nodes.contains(edge.getSource())
//					&& nodes.contains(edge.getTarget()))
//				edges.add(edge);
//		}
//		CyNetwork newNetwork = Cytoscape.createNetwork(nodes, edges,
//				CyNetworkNaming.getSuggestedSubnetworkTitle(network),
//				network);
//		CyNetworkView newView = Cytoscape.createNetworkView(newNetwork, " subgroup ["+cutoffSlider.getValue()/100.0D+"]");
//		newView.setVisualStyle(vs);
//		newView.redrawGraph(false, true);
//		CyLayoutAlgorithm lyaout = CyLayouts.getLayout("organic");
//		for(CyLayoutAlgorithm layout:CyLayouts.getAllLayouts()){			
//			System.out.println(layout);
//		}
//		CyLayouts.getLayout("organic").doLayout(Cytoscape.getNetworkView(subnewNetwork.getIdentifier()));
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
		hSpacer1 = new JPanel(null);
		createSubgroupButton = new JButton();
		cutoffSlider = new JSlider();
		scrollPane1 = new JScrollPane();
		subnetInfo = new JTable();
		rightPane = new JPanel();

		setLayout(new BorderLayout());


		panel1.setBorder(new TitledBorder("Perturbed subgroup"));
		panel1.setLayout(new BorderLayout(0, 5));

		panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));

		panel4.setLayout(new BorderLayout(10, 0));

		label1.setText("Subgroup threshold (%)");
		label1.setLabelFor(cutoffSlider);
		label1.setToolTipText("Filter the nodes by the change rates > threshold");
		panel4.add(label1, BorderLayout.CENTER);

		panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

		highlightButton.setText("Highlight all nodes in subgroup");
		highlightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				highlightButtonActionPerformed(e);
			}
		});
		panel3.add(highlightButton);
		panel3.add(hSpacer1);

		createSubgroupButton.setText("Create subgroup view");
		createSubgroupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createSubgroupButtonActionPerformed(e);
			}
		});
		panel3.add(createSubgroupButton);
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
				{"Threshold of perturbed subgroup", null},
				{"Perturbed subgroup size", "2"},
				{"Average of change ratios", "1.23"},
				{"Maximum of change ratios", null},
				{"Minimum of change ratios", null},
				{"--- Protein list ---", "--- Change ratio ---"},
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

		rightPane.setBorder(new TitledBorder("Distribution of Change Ratios of Perturbed Proteins [Source Excluded]"));
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
	private JLabel label1;
	private JPanel panel3;
	private JButton highlightButton;
	private JPanel hSpacer1;
	private JButton createSubgroupButton;
	private JSlider cutoffSlider;
	private JScrollPane scrollPane1;
	private JTable subnetInfo;
	public JPanel rightPane;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
