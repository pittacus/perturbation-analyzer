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

import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;

import giny.model.Node;
import giny.view.NodeView;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.view.CyNetworkView;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.data.Semantics;
import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMap;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.ui.JTask;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Calendar;
import java.text.*;
import java.math.*;


class Config {
	public static final String NAME = "Perturbation";
	public static final String OUTPUT = "Perturbation Analysis";
	public static final String SUFFIX_FC_BEFORE=".freeConcentrationBefore";
	public static final String SUFFIX_FC_AFTER=".freeConcentrationAfter";
	public static final String SUFFIX_FOLD=".fold";
	public static final String SUFFIX_SUBNET_SIZE=".subnetSize";
	public static final String SUFFIX_SELECT=".select";
	public static final String PREFIX_PARAMETER="perturbationParameter.";
	
	public static final String PARAMETER_TC="proteinAbundance";
	public static final String PARAMETER_RESULT="result";
	public static final String PARAMETER_TYPE="perturbationType";
	public static final String PARAMETER_FOLD="changeFold";
	public static final String PARAMETER_CRITERIA="iterativeCriteria";
	
	public static final int PERTURBATION_ALL=0;
	public static final int PERTURBATION_EACH=1;
	
	public static final String DEFAULT_RESULT_NAME="perturbationResult";
}
class Parameter{
	String proteinAbundance;
	String result;
	int perturbationType;
	double changeFold;
	double iterativeCriteria;
	
	void parseMap(Map parameter){
		proteinAbundance = (String) parameter.get(Config.PARAMETER_TC);
		result = (String) parameter.get(Config.PARAMETER_RESULT);
		perturbationType = Integer.parseInt((String)parameter.get(Config.PARAMETER_TYPE));
		changeFold = Double.parseDouble((String)parameter.get(Config.PARAMETER_FOLD));
		iterativeCriteria = Double.parseDouble((String)parameter.get(Config.PARAMETER_CRITERIA));	
	}
	Map toMap(){
		Map parameter = new HashMap();
		parameter.put(Config.PARAMETER_TC, proteinAbundance);
		parameter.put(Config.PARAMETER_RESULT, result);
		parameter.put(Config.PARAMETER_TYPE, String.valueOf(perturbationType));
		parameter.put(Config.PARAMETER_FOLD, String.valueOf(changeFold));
		parameter.put(Config.PARAMETER_CRITERIA, String.valueOf(iterativeCriteria));
		return parameter;
	}
	boolean checkResult(){
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		if(perturbationType==Config.PERTURBATION_ALL){
			return attributes.getType(result + Config.SUFFIX_FC_BEFORE) == attributes.TYPE_FLOATING
					&& attributes.getType(result + Config.SUFFIX_FC_AFTER) == attributes.TYPE_FLOATING
					&& attributes.getType(result + Config.SUFFIX_FOLD) == attributes.TYPE_FLOATING
					&& attributes.getType(result + Config.SUFFIX_SELECT) == attributes.TYPE_BOOLEAN;		
		}
		else if(perturbationType==Config.PERTURBATION_EACH){
			return attributes.getType(result + ".select") == attributes.TYPE_BOOLEAN
			&& attributes.getType(result + ".subnetSize") == attributes.TYPE_INTEGER;
		}
		else{
			return false;
		}
	}
}


/**
 * This is a Cytoscape plugin using maslov method to modeling network
 * perturbation.
 */
public class Perturbation extends CytoscapePlugin implements PropertyChangeListener{
	ControlPanel control = new ControlPanel();
	OutputPanel output = new OutputPanel();
	/**
	 * This constructor creates 1. an action and adds it to the Plugins menu, 2.
	 * a panel and adds it to the west panel.
	 */
	public Perturbation() {
		// JOptionPane.showMessageDialog(Cytoscape.getDesktop(), Config.NAME);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
		.addPropertyChangeListener(this);

		Cytoscape.getPropertyChangeSupport()
		.addPropertyChangeListener(this);

		PerturbationAction action = new PerturbationAction();
		action.setPreferredMenu("Plugins");
		Cytoscape.getDesktop().getCyMenus().addAction(action);

		CytoPanel westPanel = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.WEST);
		westPanel.add(Config.NAME, null, control, Config.NAME);
		westPanel.setSelectedIndex(westPanel.indexOfComponent(Config.NAME));

		CytoPanel southPanel = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.SOUTH);
		southPanel.add(Config.OUTPUT, null, output, Config.OUTPUT);
		southPanel.setSelectedIndex(southPanel.indexOfComponent(Config.OUTPUT));

		control.startCalculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCalculate();
			}
		});
		control.startAnalysis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAnalyse();
			}
		});
//		control.proteinAbundanceChoice.addItemListener(new ItemListener() {
//			public void itemStateChanged(ItemEvent e) {
//				proteinAbundanceChoiceItemStateChanged(e);
//			}
//		});
	}

	protected Calculator createCalculator(String freeConcentrations) {

		ContinuousMapping contMapping = new ContinuousMapping(new Color(204,
				204, 204), ObjectMapping.NODE_MAPPING);
		contMapping.setControllingAttributeName(freeConcentrations+".fold", Cytoscape
				.getCurrentNetwork(), true);
		BoundaryRangeValues brVals;
		brVals = new BoundaryRangeValues();
		// blue
		brVals.lesserValue = new Color(0, 0, 255);
		brVals.equalValue = new Color(0, 0, 255);
		brVals.greaterValue = new Color(0, 0, 255);
		contMapping.addPoint(0.8, brVals);

		// white
		brVals = new BoundaryRangeValues();
		brVals.lesserValue = new Color(255, 255, 255);
		brVals.equalValue = new Color(255, 255, 255);
		brVals.greaterValue = new Color(255, 255, 255);
		contMapping.addPoint(1.0, brVals);

		// red
		brVals = new BoundaryRangeValues();
		brVals.lesserValue = new Color(255, 0, 0);
		brVals.equalValue = new Color(255, 0, 0);
		brVals.greaterValue = new Color(255, 0, 0);
		contMapping.addPoint(1.2, brVals);

		Calculator dynamicCalculator = new BasicCalculator("Node Color Calc",
				                                            contMapping,
                                                    VisualPropertyType.NODE_FILL_COLOR);
		return dynamicCalculator;
	}// createCalculator
	
	protected void onAnalyse() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes attributes = Cytoscape.getNodeAttributes();

		Parameter parameter = new Parameter();
		String result = control.resultAnalyseChoice.getSelectedItem().toString();
		parameter.parseMap(Cytoscape.getNetworkAttributes().getMapAttribute(network.getIdentifier(),
				Config.PREFIX_PARAMETER+result));
		
		if(parameter.checkResult()){
			analyse(parameter);
		}
		else {
			alert(result + " seems NOT to be a saved result!");
		}	
	}
	protected void analyse(Parameter parameter) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		if (network == null || view == null){
			alert("You have to load protein ineraction network first!");
			return;
		}
		
		String name = parameter.result;
		//Process
		log("analyse start ["+network.getTitle()+","+name+"]");

		// init
		Map map=buildMap();
        boolean sel[]=getBooleanAttribute(map, name+Config.SUFFIX_SELECT);

		for (Component c: output.tabbedPane.getComponents())
		{
			if (c.getName().compareTo(name)==0){
				log("Remove : "+c.getName());
				output.tabbedPane.remove(c);
			}
		}
		ResultPanel panel = new ResultPanel(this, name);
		output.tabbedPane.add(parameter.result, panel);

		{
	        double fold[]=getDoubleAttribute(map, name+Config.SUFFIX_FOLD);
	
	        int i, j;
	        for (i = j = 0; j < fold.length; ++j){
	//	          if (Double.compare(Math.abs(fc[j] - 1.0D), 1e-2) > 0 && fc[j]<1.5) fc[i++] = fc[j];
	        	if (sel[j]) fold[i++] = fold[j];
	        }
	        fold = Arrays.copyOf(fold, i);
	        
	        HistogramDataset histogramdataset = new HistogramDataset(); 
	        histogramdataset.addSeries("", fold, 100); 
	
	        JFreeChart jfreechart = ChartFactory.createHistogram(
	        		"Histogram of Source Protein Fold Change",
	        		null, null, 
	        		histogramdataset, PlotOrientation.VERTICAL,
	        		false, false, false); 
	        jfreechart.getXYPlot().setForegroundAlpha(0.75F); 
	
			panel.splitRightPane.setTopComponent(new ChartPanel(jfreechart));
		}
		{
	        double fold[]=getDoubleAttribute(map, name+Config.SUFFIX_FOLD);
	
	        int i, j;
	        for (i = j = 0; j < fold.length; ++j){
	        	if (!sel[j]) fold[i++] = fold[j];
	        }
	        fold = Arrays.copyOf(fold, i);
	        
	        HistogramDataset histogramdataset = new HistogramDataset(); 
	        histogramdataset.addSeries("", fold, 100); 
	
	        JFreeChart jfreechart = ChartFactory.createHistogram(
	        		"Histogram of Other Protein Fold Change",
	        		null, null, 
	        		histogramdataset, PlotOrientation.VERTICAL,
	        		false, false, false); 
	        jfreechart.getXYPlot().setForegroundAlpha(0.75F); 
	        
			panel.splitRightPane.setBottomComponent(new ChartPanel(jfreechart));
		}
		
		panel.splitRightPane.setOneTouchExpandable(true);
		panel.splitRightPane.setResizeWeight(0.5);
		
		panel.splitPane.setOneTouchExpandable(true);
		panel.splitPane.setResizeWeight(0.5);
		
		highlightNodes(map,sel);
		
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		NodeAppearanceCalculator nac = manager.getVisualStyle()
				.getNodeAppearanceCalculator();
		nac.setCalculator(createCalculator(name));
		manager.applyNodeAppearances();
		Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
		
		log("analyse end ["+network.getTitle()+","+name+"]");
}
	
	
	

	class PerturbationAction extends CytoscapeAction {
		public PerturbationAction() {
			super(Config.NAME);
		}

		public void actionPerformed(ActionEvent ae) {
			CytoPanel panel = Cytoscape.getDesktop().getCytoPanel(
					SwingConstants.WEST);
			panel.setSelectedIndex(panel.indexOfComponent(Config.NAME));
			if (true)
				return;
		}
	}

	protected void log(String msg) {
		control.infoText.append(now()+" "+msg+"\n");
		control.infoText.setCaretPosition(control.infoText.getDocument().getLength());
	}



	
	protected boolean confirm(String msg) {
		return JOptionPane.showConfirmDialog(Cytoscape.getCurrentNetworkView()
				.getComponent(), msg, Config.NAME, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.OK_OPTION;
	}
	

	protected String join(AbstractCollection s, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		Iterator iter = s.iterator();
		if (iter.hasNext()) {
			buffer.append(iter.next());
			while (iter.hasNext()) {
				buffer.append(delimiter);
				buffer.append(iter.next());
			}
		}
		return buffer.toString();
	}

	protected String join(String[] s, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		if(s.length>0)
			buffer.append(s[0]);
		for(int k=1;k<s.length;k++){
			buffer.append(delimiter);
			buffer.append(s[k]);
		}
		return buffer.toString();
	}

	private static final String DATE_FORMAT_NOW = "HH:mm:ss";
	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}


//	public void proteinAbundanceChoiceItemStateChanged(ItemEvent e) {
//		if (control.iterativeCriteria.getText().length()>0)
//			return;
//		
//		CyNetwork network = Cytoscape.getCurrentNetwork();
//		CyAttributes attributes = Cytoscape.getNodeAttributes();
//
//		Double minAbundance=null;
//		String id;
//		for (Iterator i = network.nodesIterator(); i.hasNext();) {
//			CyNode node = (CyNode) i.next();
//			id = node.getIdentifier();
//			
//			double abundance = attributes.getAttribute(id, name);
//			if (minAbundance!=null)
//				minAbundance = Math.min(minAbundance, abundance);
//			else
//				minAbundance = abundance;
//		}
//		
//		control.iterativeCriteria.setText(String.valueOf(minAbundance));
//	}
	
	public void propertyChange(PropertyChangeEvent e) {
		//		JOptionPane.showMessageDialog(Cytoscape.getCurrentNetworkView()
		//				.getComponent(), e.getPropertyName());      	
		if (e.getPropertyName().equalsIgnoreCase(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		}
		else if (e.getPropertyName().equalsIgnoreCase(Cytoscape.ATTRIBUTES_CHANGED)) {
			log("Cytoscape.ATTRIBUTES_CHANGED");

			Object originalString1 = control.proteinAbundanceChoice.getSelectedItem();
			Object originalString2 = control.resultChoice.getEditor().getItem();
			Object originalString3 = control.resultAnalyseChoice.getSelectedItem();

			control.proteinAbundanceChoice.removeAllItems();
			control.resultChoice.removeAllItems();
			control.resultAnalyseChoice.removeAllItems();
			
			CyAttributes attributes;
			String[] attrArray;
			
			attributes = Cytoscape.getNodeAttributes();
			attrArray = attributes.getAttributeNames();
			Arrays.sort(attrArray);
			for(String s:attrArray) {
				if(attributes.getType(s) == attributes.TYPE_FLOATING)
					control.proteinAbundanceChoice.addItem(s);
//				
//				if (s.endsWith(".fold")) {
//					control.resultChoice.addItem(s.substring(0,s.length()-5));
//					control.resultAnalyseChoice.addItem(s.substring(0,s.length()-5));
//				}
			}
			
			attributes = Cytoscape.getNetworkAttributes();
			attrArray = attributes.getAttributeNames();
			Arrays.sort(attrArray);
			for(String s:attrArray) {
				if(attributes.getType(s) == attributes.TYPE_SIMPLE_MAP){
					String name = s.substring(Config.PREFIX_PARAMETER.length(),s.length());
					control.resultChoice.addItem(name);
					control.resultAnalyseChoice.addItem(name);
				}	
			}
				
			control.proteinAbundanceChoice.setSelectedItem(originalString1);

			control.resultChoice.getEditor().setItem(originalString2);
			if (control.resultChoice.getEditor().getItem().toString().isEmpty())
				control.resultChoice.getEditor().setItem(Config.DEFAULT_RESULT_NAME);
			control.resultAnalyseChoice.setSelectedItem(originalString3);
		}
	}

//	protected void loadSampleData() {
//		try{
//			Class c=getClass();
//			InputStream inStream = c.getResourceAsStream("/data/maslov.xgmml");
//
//			File temp = File.createTempFile("maslov", ".xgmml");
//			FileOutputStream outStream = new FileOutputStream(temp);
//			byte[] buffer = new byte[8196];
//			int length;
//			int byteread = 0;
//			while ((byteread = inStream.read(buffer)) != -1) {
//				outStream.write(buffer, 0, byteread);
//			}
//			inStream.close();
//
//			Cytoscape.createNetworkFromFile(temp.getAbsolutePath());
//			//Cytoscape.getDesktop().getCytoPanel(SwingConstants.CENTER).setState(CytoPanelState.DOCK);
//			//Cytoscape.getDesktop().pack();
//			control.resultChoice.getEditor().setItem("Perturbation.freeConcentrations");
//			log("Load sample data complete");
//		}catch(Exception e){
//			e.printStackTrace();
//			alert("An error may occur when loading sample data!");
//		}
//	}

	// assume that nodes have Perturbation.totalConcentration attribute
	// generate Perturbation.freeConcentration attribute
	protected void onCalculate() {
		calculate(false);
	}
	protected void calculate(boolean calcAll) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		if (network == null || view == null){
			alert("You have to load protein ineraction network first!");
			return;
		}

		// init
		int nSelectedNode = network.getSelectedNodes().size();
//		if(nSelectedNode>0)
//			alert("You have selected "+nSelectedNode+" proteins, " +
//			"and these proteins will be upregulated 2-fold as sources of perturbation");
		if(nSelectedNode==0){
			info("You must select one or more proteins as perturbation sources, " +
			"and these proteins will be upregulated 2-fold as sources of perturbation");
			return;
		}
		
		Parameter parameter = new Parameter();
		parameter.proteinAbundance = control.proteinAbundanceChoice.getSelectedItem().toString();
		parameter.result = control.resultChoice.getEditor().getItem().toString();
		parameter.perturbationType = control.perturbationType.getSelectedIndex();
		parameter.changeFold = Double.parseDouble(control.changeFoldChoice.getEditor().getItem().toString());
		parameter.iterativeCriteria = Double.parseDouble(control.iterativeCriteria.getText());
		
		String name = parameter.result;
		if (attributes.getMapAttribute(network.getIdentifier(), Config.PREFIX_PARAMETER+name) != null) {
			if (!confirm(name
					+ " seems being used to save previous result, "
					+ "are you sure to clear it?")){
				return;
			}
			else
			{
				attributes.deleteAttribute(name + Config.SUFFIX_SELECT);
				attributes.deleteAttribute(name + Config.SUFFIX_FC_BEFORE);
				attributes.deleteAttribute(name + Config.SUFFIX_FC_AFTER);
				attributes.deleteAttribute(name + Config.SUFFIX_FOLD);
				attributes.deleteAttribute(name + Config.SUFFIX_SUBNET_SIZE);
			}
		}
		
		Cytoscape.getNetworkAttributes().setMapAttribute(network.getIdentifier(),
				Config.PREFIX_PARAMETER+name, parameter.toMap());
		 
		//Process
		log(network.getTitle()+" calculate start");
		Task task = new calculateTask(this, parameter);
		JTaskConfig config = new JTaskConfig();
		config.displayCancelButton(true);
		config.displayStatus(true);
		//config.displayTimeRemaining(true);
		boolean success = TaskManager.executeTask(task, config);

		//		Perturbation.info(Perturbation.join(attributes.getAttributeNames(),"\r\n"));
		//		attributes.setAttribute(id, "Perturbation.changeFold", 1.234);
		//		attributes.setAttribute(id, "Perturbation.freeConcentration", 1.234);
		//		Perturbation.info(Perturbation.join(attributes.getAttributeNames(),"\r\n"));
		log(network.getTitle()+" calculate end");	
		analyse(parameter);
	}
	
	public void highlightSubnetwork(DefaultTableModel model, String freeConcentrations, double cutoff, boolean highlightNetwork) {
		DecimalFormat formatter = new DecimalFormat("0.##");
		
		model.setValueAt(cutoff, 0, 1);

		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		
		int n = network.getNodeCount();
		
		Map map=buildMap();
		double fold[]=getDoubleAttribute(map, freeConcentrations+".fold");
		boolean sel[]=getBooleanAttribute(map, freeConcentrations+".select");
		
		SortedMap subnodes = new TreeMap(); 
		
		boolean highlight[] = new boolean[n];
		String id;
		int idx;
		for (Iterator i = network.nodesIterator(); i.hasNext();) {
			CyNode node = (CyNode) i.next();
			id = node.getIdentifier();
			idx = (Integer) map.get(id);
			if(Math.abs(fold[idx]-1.0D)>cutoff){
				subnodes.put(id, fold[idx]);
				highlight[idx]=true;
			}
		}
		
		if (highlightNetwork) {
			highlightNodes(map,highlight);
			Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
		}
		
		int m = subnodes.keySet().size();
		model.setValueAt(m, 1, 1);

		model.setNumRows(m+5);
	    Iterator iterator = subnodes.keySet().iterator();
	    int k=0;
	    while (iterator.hasNext()) {
	      Object key = iterator.next();
	      model.setValueAt(key.toString(), 5+k, 0);
	      model.setValueAt(subnodes.get(key).toString(), 5+k, 1);
	      k++;
	    }
	}
	
	
	public int[][] getAdjacentMatrix(Map map) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		int n = network.getNodeCount();
		int[][] s = new int[n][n];
		for (Iterator i = network.edgesIterator(); i.hasNext();) {
			CyEdge edge = (CyEdge) i.next();
			int source = (Integer) map.get(((CyNode) (edge.getSource()))
					.getIdentifier());
			int target = (Integer) map.get(((CyNode) (edge.getTarget()))
					.getIdentifier());
			s[source][target] = 1;
			s[target][source] = 1;
		}
		return s;
	}

	public double[] getDoubleAttribute(Map map, String name) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		if (attributes.getType(name) != attributes.TYPE_FLOATING) {
			alert(name + " must be a float node attribute");
			return null;
		}

		int n = network.getNodeCount();
		double[] tc = new double[n];

		String id;
		int idx;
		for (Iterator i = network.nodesIterator(); i.hasNext();) {
			CyNode node = (CyNode) i.next();
			id = node.getIdentifier();
			idx = (Integer) map.get(id);
			tc[idx] = attributes.getDoubleAttribute(id, name);
		}
		return tc;
	}
	public boolean[] getBooleanAttribute(Map map, String name) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		if (attributes.getType(name) != attributes.TYPE_BOOLEAN) {
			alert(name + " must be a float node attribute");
			return null;
		}

		int n = network.getNodeCount();
		boolean[] tc = new boolean[n];

		String id;
		int idx;
		for (Iterator i = network.nodesIterator(); i.hasNext();) {
			CyNode node = (CyNode) i.next();
			id = node.getIdentifier();
			idx = (Integer) map.get(id);
			tc[idx] = attributes.getBooleanAttribute(id, name);
		}
		return tc;
	}

	public void setAttribute(Map map, String name, double[] fc) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		String id;
		int idx;
		for (Iterator i = network.nodesIterator(); i.hasNext();) {
			CyNode node = (CyNode) i.next();
			id = node.getIdentifier();
			idx = (Integer) map.get(id);

			attributes.setAttribute(id, name, fc[idx]);
		}
	}
	public void highlightNodes(Map map, boolean[] sel) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		
		network.unselectAllNodes();
		
		String id;
		int idx;
		for (Iterator i = network.nodesIterator(); i.hasNext();) {
			CyNode node = (CyNode) i.next();
			id = node.getIdentifier();
			idx = (Integer) map.get(id);
			network.setSelectedNodeState(node, sel[idx]);
		}
	}
	
	public void setAttribute(Map map, String name, boolean[] fc) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		String id;
		int idx;
		for (Iterator i = network.nodesIterator(); i.hasNext();) {
			CyNode node = (CyNode) i.next();
			id = node.getIdentifier();
			idx = (Integer) map.get(id);

			attributes.setAttribute(id, name, fc[idx]);
		}
	}

	public Map buildMap() {
		Map map = new HashMap();
		
		String id;
		int idx;

		CyNetwork network = Cytoscape.getCurrentNetwork();
		int n = network.getNodeCount();

		idx = 0;
		for (Iterator i = network.nodesIterator(); i.hasNext();) {
			CyNode node = (CyNode) i.next();
			id = node.getIdentifier();
			map.put(id, idx);
			idx++;
		}
		return map;
	}



	protected void alert(String msg) {
		JOptionPane.showMessageDialog(Cytoscape.getCurrentNetworkView()
				.getComponent(), msg, Config.NAME, JOptionPane.WARNING_MESSAGE);
	}
	
	protected void info(String msg) {
		JOptionPane.showMessageDialog(Cytoscape.getCurrentNetworkView()
				.getComponent(), msg, Config.NAME, JOptionPane.INFORMATION_MESSAGE);
	}

}

class MainPanel extends JPanel implements PropertyChangeListener {
	JComboBox nodePropertyChoice = new JComboBox();;
	JPanel emptyWarningPanel = new JPanel();
	public MainPanel() {
		JButton btn;

		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(this);
		
		Cytoscape.getPropertyChangeSupport()
				.addPropertyChangeListener(this);

		emptyWarningPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Waring"),
                BorderFactory.createEmptyBorder(2,2,2,2)));
		emptyWarningPanel.setLayout(new BoxLayout(emptyWarningPanel, BoxLayout.Y_AXIS));
		emptyWarningPanel.add(new JLabel(
				"<html>Current network is <FONT COLOR=RED>EMPTY</FONT><BR>"
				+" or have <FONT COLOR=RED>NO node properties</FONT><BR> as initial parameter</html>"));
		
		JPanel calculatePanel = new JPanel();
		JPanel analysisPanel = new JPanel();
		JPanel displayPanel = new JPanel();
		calculatePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Calculate"),
                BorderFactory.createEmptyBorder(2,2,2,2)));
		calculatePanel.setLayout(new BoxLayout(calculatePanel, BoxLayout.Y_AXIS));
		//calculatePanel.add(Box.createRigidArea(new Dimension(0,5)));

		calculatePanel.add(emptyWarningPanel);
		
		calculatePanel.add(new JLabel("Initial Protein Abundance"));
		nodePropertyChoice.setSize(new Dimension(200,10));
		calculatePanel.add(nodePropertyChoice);
		nodePropertyChoice.setToolTipText("Using following widget to choice initial parameter for maslov model");

		btn = new JButton("Select Neighbor");
		//calculatePanel.add(btn);
//		btn.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				neighbor();
//			}
//		});

		btn = new JButton("Load Maslov Sample Data");
		calculatePanel.add(btn);
//		btn.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				loadSampleData();
//			}
//		});

		btn = new JButton("Calculate Maslov Perturbation");
		calculatePanel.add(btn);
//		btn.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				calculateFreeConcentrations();
//			}
//		});

		//JLabel label = new JLabel("Using following widget to\r\n choice initial parameter for maslov model");
		//calculatePanel.add(label);
		
		//add(calculatePanel);
		
	}
	
	void neighbor() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		if (network == null || view == null) {
			return;
		}

		if (view.getSelectedNodes().size() == 0) {
			JOptionPane.showMessageDialog(view.getComponent(),
					"Please select one or more nodes.");
			return;
		}

		Set nodeViewsToSelect = new HashSet();
		for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext();) {
			NodeView nView = (NodeView) i.next();
			CyNode node = (CyNode) nView.getNode();
			List neighbors = network.neighborsList(node);
			for (Iterator ni = neighbors.iterator(); ni.hasNext();) {
				CyNode neighbor = (CyNode) ni.next();
				NodeView neighborView = view.getNodeView(neighbor);
				nodeViewsToSelect.add(neighborView);
			}
		}
		for (Iterator i = nodeViewsToSelect.iterator(); i.hasNext();) {
			NodeView nView = (NodeView) i.next();
			nView.setSelected(true);
		}
		view.redrawGraph(false, true);
	}

	public void propertyChange(PropertyChangeEvent e) {
//		JOptionPane.showMessageDialog(Cytoscape.getCurrentNetworkView()
//				.getComponent(), e.getPropertyName());      	
		if (e.getPropertyName().equalsIgnoreCase(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
    		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
        }
        else if (e.getPropertyName().equalsIgnoreCase(Cytoscape.ATTRIBUTES_CHANGED)) {
    		CyAttributes attr = Cytoscape.getNodeAttributes();
        	Dimension d = nodePropertyChoice.getSize();
        	nodePropertyChoice.removeAllItems();
        	String[] attrArray=attr.getAttributeNames();
        	Arrays.sort(attrArray);
        	for(String s:attrArray) {
        			nodePropertyChoice.addItem(s);
            }
        	nodePropertyChoice.setSize(d);
       }
	}
}

/**
 * Calculate Task, used to illustrate the Task Framework. This tasks counts from
 * 0 to maxValue.
 */
class calculateTask implements Task {
	private TaskMonitor taskMonitor = null;
	private boolean interrupted = false;
	private Perturbation perturbation = null;
	private Parameter parameter;

	String taskTitle;
	double changeFold;
	
	public calculateTask(Perturbation p, Parameter param) {
		perturbation = p;
		parameter=param;
	}

	public double[] maslov(double[] tc, int[][] s) throws InterruptedException {
		int n = tc.length;
		// Calculate
		// function fc=my_free_conc(tc,hprd_s1)
		// Fc=tc;
		double[] fc = tc.clone();
		double[] fc_old = tc.clone();
		// s1=hprd_s1; %s1=1./K;
		// k=340;
		double k = 340;
		// s1=s1+diag(diag(s1));% 邻接矩阵加对角线,能模拟homodimer/heterodimer
		for (int i = 0; i < n; i++)
			s[i][i] += s[i][i];
		// Fc_old=Fc.*0.001;
		// iter=0;
		// while max(abs(Fc-Fc_old)./Fc)>1e-10;
		// iter=iter+1;
		// Fc_old=Fc;
		// Fc=tc./(1+s1*Fc./k);
		// iter
		// end;
		double eps = parameter.iterativeCriteria;
		// double eps = 1e-3;
		int iter = 0;
		for (; !this.interrupted; iter++) {

			double max = 0;
			for (int i = 0; i < n; i++) {
				double sum = 0;
				for (int j = 0; j < n; j++) {
					sum += s[i][j] * fc[j] / k;
				}
				fc[i] = tc[i] / (1 + sum);

				max = Math.max(max, Math.abs(fc_old[i] - fc[i]) / fc[i]);
				fc_old[i] = fc[i];
			}
			DecimalFormat formatter = new DecimalFormat("0.####E0");
//			perturbation.log("[" + iter + "," + formatter.format(max) + ","
//					+ eps + "]");
			this.taskMonitor.setStatus(taskTitle + " " + iter + " / "
					+ formatter.format(max));
			if (max < eps)
				break;
			// Thread.sleep(300);
		}
		return fc;
	}

	public void run() {
		if (this.taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}
		
		perturbation.log("Protein abundance : " + parameter.proteinAbundance);
		perturbation.log("Save result as : " + parameter.result);
		perturbation.log("Change Fold : " + parameter.changeFold);
		
		try {
			
			calculateOnce();

			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null,
					null);
		} catch (InterruptedException e) {
			taskMonitor.setException(e, "Calculating cancelled");
		}
	}

	protected void calculateOnce() throws InterruptedException{
		String id; int idx;

		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		int n = network.getNodeCount();

		String name = parameter.result;
		// init
		Map map=perturbation.buildMap();

		double[] tc = perturbation.getDoubleAttribute(map, parameter.proteinAbundance);
		int[][] s = perturbation.getAdjacentMatrix(map);

		// calc
		taskTitle="Calulate fc before perturbation";
		perturbation.log(taskTitle);
		double[] fc_before = maslov(tc, s);
		
		taskTitle="Calulate fc after perturbation";
		perturbation.log(taskTitle);
		boolean[] sel = new boolean[n];
		for (Object selectedNode : network.getSelectedNodes()) {
			idx = (Integer) map.get(((CyNode) selectedNode).getIdentifier());
			if(changeFold>0)
				tc[idx] *= changeFold;
			else if(changeFold<0)
				tc[idx] /= (-changeFold);
			sel[idx]=true;
			// perturbation.log(selectedNode.getClass().toString());
		}
		perturbation.log("Select Nodes: " + network.getSelectedNodes().size());

		double[] fc_after = maslov(tc, s);

		// write back
		double[] fc_fold = fc_before.clone();
		for (idx = 0; idx < fc_fold.length; idx++) {
			fc_fold[idx] = (fc_before[idx] != 0 ? fc_after[idx]
					/ fc_before[idx] : Double.NaN);
		}
		perturbation.setAttribute(map, name + Config.SUFFIX_FC_BEFORE, fc_before);
		perturbation.setAttribute(map, name + Config.SUFFIX_FC_AFTER, fc_after);
		perturbation.setAttribute(map, name + Config.SUFFIX_FOLD, fc_fold);
		perturbation.setAttribute(map, name + Config.SUFFIX_SELECT, sel);
	}
	
	public void halt() {
		this.interrupted = true;
	}

	public void setTaskMonitor(TaskMonitor taskMonitor) {
		if (this.taskMonitor != null) {
			throw new IllegalStateException("Task Monitor is already set.");
		}
		this.taskMonitor = taskMonitor;
	}

	public String getTitle() {
		return new String("Calculating Perturbation, Please waiting...");
	}
}
