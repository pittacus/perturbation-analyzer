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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.Paint;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.ui.RectangleEdge;

import giny.view.NodeView;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.view.CyNetworkView;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.ui.editors.continuous.C2CMappingEditor;
import cytoscape.visual.ui.editors.continuous.ContinuousMappingEditorPanel;
import cytoscape.visual.ui.editors.continuous.GradientEditorPanel;

import cytoscape.data.CyAttributes;
import cytoscape.ding.CyGraphAllLOD;
import cytoscape.ding.CyGraphLOD;
import cytoscape.ding.DingNetworkView;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;
import java.util.Calendar;
import java.text.*;
import cytoscape.ding.CyGraphLOD;
import cytoscape.ding.CyGraphAllLOD;
import cytoscape.ding.DingNetworkView;


class Config {
	public static final String NAME = "PerturbationAnalyzer";
	public static final String OUTPUT = "Perturbation Analysis";
	public static final String SUFFIX_FC_BEFORE = ".fcBefore";
	public static final String SUFFIX_FC_AFTER = ".fcAfter";
	public static final String SUFFIX_FOLD = ".fold";
	public static final String SUFFIX_SUBNET_SIZE = ".subnetSize";
	public static final String SUFFIX_SELECT = ".select";
	public static final String PREFIX_PARAMETER = "perturbationParameter.";

	public static final String PARAMETER_TC = "proteinAbundance";
	public static final String PARAMETER_RESULT = "result";
	public static final String PARAMETER_TYPE = "perturbationType";
	public static final String PARAMETER_FOLD = "changeFold";
	public static final String PARAMETER_CRITERIA = "iterativeCriteria";
	public static final String PARAMETER_THRESHOLD = "disturbedThreshold";
	public static final String PARAMETER_DISSOCIATION = "dissociationConstant";

	public static final int PERTURBATION_SINGLE = 0;
	public static final int PERTURBATION_BATCH = 1;

	public static final String DEFAULT_RESULT_NAME = "Perturbation.result.{CURRENT DATETIME}";
	public static final String DEFAULT_DISSOCIATION_CONSTANT = "MAX(Ci,Cj)/20";
}

class Parameter {
	String proteinAbundance;
	String result;
	int perturbationType;
	double changeFold;
	double iterativeCriteria;
	double subgroupThreshold;
	double dissociationConstant;
	Parameter(){
		
	}
	Parameter(Map parameter) {
		parseMap(parameter);
	}
	void parseMap(Map parameter) {
		proteinAbundance = (String) parameter.get(Config.PARAMETER_TC);
		result = (String) parameter.get(Config.PARAMETER_RESULT);
		perturbationType = Integer.parseInt((String) parameter
				.get(Config.PARAMETER_TYPE));
		changeFold = Double.parseDouble((String) parameter
				.get(Config.PARAMETER_FOLD));
		iterativeCriteria = Double.parseDouble((String) parameter
				.get(Config.PARAMETER_CRITERIA));
		subgroupThreshold = Double.parseDouble((String) parameter
				.get(Config.PARAMETER_THRESHOLD));
		dissociationConstant = Double.parseDouble((String) parameter
				.get(Config.PARAMETER_DISSOCIATION));
	}

	Map<String, String> toMap() {
		Map parameter = new HashMap();
		parameter.put(Config.PARAMETER_TC, proteinAbundance);
		parameter.put(Config.PARAMETER_RESULT, result);
		parameter.put(Config.PARAMETER_TYPE, String.valueOf(perturbationType));
		parameter.put(Config.PARAMETER_FOLD, String.valueOf(changeFold));
		parameter.put(Config.PARAMETER_CRITERIA, String
				.valueOf(iterativeCriteria));
		parameter.put(Config.PARAMETER_THRESHOLD, String
				.valueOf(subgroupThreshold));
		parameter.put(Config.PARAMETER_DISSOCIATION, String
				.valueOf(dissociationConstant));
		return parameter;
	}

	boolean checkResult() {
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		if (perturbationType == Config.PERTURBATION_SINGLE) {
			return attributes.getType(result + Config.SUFFIX_FC_BEFORE) == CyAttributes.TYPE_FLOATING
					&& attributes.getType(result + Config.SUFFIX_FC_AFTER) == CyAttributes.TYPE_FLOATING
					&& attributes.getType(result + Config.SUFFIX_FOLD) == CyAttributes.TYPE_FLOATING
					&& attributes.getType(result + Config.SUFFIX_SELECT) == CyAttributes.TYPE_BOOLEAN;
		} else if (perturbationType == Config.PERTURBATION_BATCH) {
			return attributes.getType(result + Config.SUFFIX_SELECT) == CyAttributes.TYPE_BOOLEAN
					&& attributes.getType(result + Config.SUFFIX_SUBNET_SIZE) == CyAttributes.TYPE_FLOATING;
		} else {
			return false;
		}
	}
}

/**
 * This is a Cytoscape plugin using maslov method to modeling network
 * perturbation.
 */
public class Perturbation extends CytoscapePlugin implements
		PropertyChangeListener {
	ControlPanel control = new ControlPanel(this);
	JTabbedPane output = new JTabbedPane();

	/**
	 * This constructor creates 1. an action and adds it to the Plugins menu, 2.
	 * a panel and adds it to the west panel.
	 */
	public Perturbation() {
		// JOptionPane.showMessageDialog(Cytoscape.getDesktop(), Config.NAME);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(this);

		System.out.println("Starting Perturbation ...");

		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);

		PerturbationAction action = new PerturbationAction();
		action.setPreferredMenu("Plugins");
		Cytoscape.getDesktop().getCyMenus().addAction(action);

		CytoPanel westPanel = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.WEST);
		westPanel.add(Config.NAME, null, control, Config.NAME);
//		westPanel.setSelectedIndex(westPanel.indexOfComponent(Config.NAME));

		CytoPanel southPanel = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.SOUTH);
		southPanel.add(Config.OUTPUT, null, output, Config.OUTPUT);
		southPanel.setSelectedIndex(southPanel.indexOfComponent(Config.OUTPUT));

		control.startCalculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCalculate();
			}
		});

		JPopupMenu popup = new JPopupMenu();
		JMenuItem itemA = new JMenuItem("Close & Delete");
		itemA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDeleteResult();
			}
		});
		popup.add(itemA);
		output.setComponentPopupMenu(popup);

		output.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				OnViewResult();
			}
		});
	}

	protected void OnViewResult() {
		if(output.getSelectedComponent()==null) return;
		String name = output.getSelectedComponent().getName();
		Parameter parameter = new Parameter(Cytoscape.getNetworkAttributes()
				.getMapAttribute(Cytoscape.getCurrentNetwork().getIdentifier(), Config.PREFIX_PARAMETER+name));

		Map map = buildMap();
		boolean sel[] = getBooleanAttribute(map, name + Config.SUFFIX_SELECT);
		//highlightNodes(map, sel);

		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		NodeAppearanceCalculator nac = manager.getVisualStyle()
				.getNodeAppearanceCalculator();
		//manager.getVisualStyle().getNodeAppearanceCalculator().removeCalculator(VisualPropertyType.NODE_FILL_COLOR);
		nac.setCalculator(createCalculator(parameter));
		manager.applyNodeAppearances();
		manager.fireStateChanged();
		control.legend.setIcon(GradientEditorPanel.getIcon(control.legend.getWidth(), control.legend.getHeight(),
				(VisualPropertyType) VisualPropertyType.NODE_FILL_COLOR));
		Cytoscape.getCurrentNetworkView().redrawGraph(false, true);

		try{
			if(Cytoscape.getCurrentNetworkView()!=null)
				((DingNetworkView)Cytoscape.getCurrentNetworkView()).setGraphLOD(new CyGraphAllLOD());
		}catch(Exception e){
		}
		if (parameter.perturbationType == Config.PERTURBATION_SINGLE)
			highlightSources(map, sel);		
		//highlightNodes(map, sel);
	}

	protected void onDeleteResult() {
		String result = output.getSelectedComponent().getName();
		output.remove(output.getSelectedComponent());
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		attributes.deleteAttribute(result + Config.SUFFIX_FC_BEFORE);
		attributes.deleteAttribute(result + Config.SUFFIX_FC_AFTER);
		attributes.deleteAttribute(result + Config.SUFFIX_FOLD);
		attributes.deleteAttribute(result + Config.SUFFIX_SELECT);
		attributes.deleteAttribute(result + Config.SUFFIX_SELECT);
		attributes.deleteAttribute(result + Config.SUFFIX_SUBNET_SIZE);
		Cytoscape.getNetworkAttributes().deleteAttribute(
				Config.PREFIX_PARAMETER + result);
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}

	protected Calculator createCalculator(Parameter parameter) {
		String index = null;
		if (parameter.perturbationType == Config.PERTURBATION_SINGLE)
			return createCalculatorSingle(parameter);
		else if (parameter.perturbationType == Config.PERTURBATION_BATCH)
			return createCalculatorBatch(parameter);
		else {
			alert("Invalid perturbationType!");
			return null;
		}
	}// createCalculator
	
	protected Calculator createCalculatorSingle(Parameter parameter) {
		String index = null;
		index = parameter.result + Config.SUFFIX_FOLD;
	
		//log("Visual mapping: " + index);
		ContinuousMapping contMapping = new ContinuousMapping(new Color(204,
				204, 204), ObjectMapping.NODE_MAPPING);
		contMapping.setControllingAttributeName(index, Cytoscape
				.getCurrentNetwork(), true);
		BoundaryRangeValues brVals;
		brVals = new BoundaryRangeValues();
		// green
		brVals.lesserValue = new Color(0, 255, 0);
		brVals.equalValue = new Color(0, 128, 0);
		brVals.greaterValue = new Color(0, 128, 0);
		contMapping.addPoint(0.0, brVals);

		brVals.lesserValue = new Color(0, 255, 0);
		brVals.equalValue = new Color(0, 128, 0);
		brVals.greaterValue = new Color(0, 128, 0);
		contMapping.addPoint(1.0-parameter.subgroupThreshold, brVals);

		// white
		brVals = new BoundaryRangeValues();
		brVals.lesserValue = new Color(255, 255, 255);
		brVals.equalValue = new Color(255, 255, 255);
		brVals.greaterValue = new Color(255, 255, 255);
		contMapping.addPoint(1.0, brVals);

		// red
		brVals = new BoundaryRangeValues();
		brVals.lesserValue = new Color(128, 0, 0);
		brVals.equalValue = new Color(128, 0, 0);
		brVals.greaterValue = new Color(128, 0, 0);
		contMapping.addPoint(1.0+parameter.subgroupThreshold, brVals);

		brVals = new BoundaryRangeValues();
		brVals.lesserValue = new Color(255, 0, 0);
		brVals.equalValue = new Color(255, 0, 0);
		brVals.greaterValue = new Color(255, 0, 0);
		contMapping.addPoint(2.0, brVals);
		
		Calculator dynamicCalculator = new BasicCalculator("Node Color Calc",
				contMapping, VisualPropertyType.NODE_FILL_COLOR);
		return dynamicCalculator;
	}// createCalculator

	protected Calculator createCalculatorBatch(Parameter parameter) {
		String index = null;
		index = parameter.result + Config.SUFFIX_SUBNET_SIZE;
		
		//log("Visual mapping: " + index);
		ContinuousMapping contMapping = new ContinuousMapping(new Color(204,
				204, 204), ObjectMapping.NODE_MAPPING);
		contMapping.setControllingAttributeName(index, Cytoscape
				.getCurrentNetwork(), false);
		BoundaryRangeValues brVals;
		brVals = new BoundaryRangeValues();
		
		// white
		brVals = new BoundaryRangeValues();
		brVals.lesserValue = new Color(255, 255, 255);
		brVals.equalValue = new Color(255, 255, 255);
		brVals.greaterValue = new Color(255, 255, 255);
		contMapping.addPoint(0.0, brVals);

		// white
		brVals = new BoundaryRangeValues();
		brVals.lesserValue = new Color(255, 255, 255);
		brVals.equalValue = new Color(255, 255, 255);
		brVals.greaterValue = new Color(255, 255, 255);
		contMapping.addPoint(1.0, brVals);

		// blue
		brVals = new BoundaryRangeValues();
		brVals.lesserValue = new Color(0, 0, 255);
		brVals.equalValue = new Color(0, 0, 255);
		brVals.greaterValue = new Color(0, 0, 255);
		contMapping.addPoint(30.0, brVals);

		// red
		brVals = new BoundaryRangeValues();
		brVals.lesserValue = new Color(128, 0, 0);
		brVals.equalValue = new Color(128, 0, 0);
		brVals.greaterValue = new Color(128, 0, 0);
		contMapping.addPoint(60.0, brVals);
		
		// red
		brVals = new BoundaryRangeValues();
		brVals.lesserValue = new Color(255, 0, 0);
		brVals.equalValue = new Color(255, 0, 0);
		brVals.greaterValue = new Color(255, 0, 0);
		contMapping.addPoint(Cytoscape.getCurrentNetwork().getNodeCount(), brVals);
		
		Calculator dynamicCalculator = new BasicCalculator("Node Color Calc",
				contMapping, VisualPropertyType.NODE_FILL_COLOR);
		return dynamicCalculator;
	}// createCalculator

	protected void setupAnalyse() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes attributes = Cytoscape.getNetworkAttributes();

		Parameter parameter = new Parameter();

		String[] attrArray = attributes.getAttributeNames();
		Arrays.sort(attrArray);
		for (String s : attrArray) {
			if (s.startsWith(Config.PREFIX_PARAMETER)) {
				try {
					parameter.parseMap(Cytoscape.getNetworkAttributes()
							.getMapAttribute(network.getIdentifier(), s));

					if (parameter.checkResult())
						analyse(parameter);
					else {
						log("checkResult failed: " + s);
						log("Maybe the result is calculated by former version.");
					}
				} catch (Exception e) {
					log("Import result failed: " + s);
					log("Maybe the result is calculated by former version.");
					//alert(e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		}
	}

	protected void analyse(Parameter parameter) {
		log("Analyse start: " + parameter.result);
		for (Component c : output.getComponents()) {
			if (c.getName().compareTo(parameter.result) == 0) {
				log("Remove : " + c.getName());
				output.remove(c);
			}
		}
		if (parameter.perturbationType == Config.PERTURBATION_SINGLE)
			analyseSingle(parameter);
		else if (parameter.perturbationType == Config.PERTURBATION_BATCH)
			analyseBatch(parameter);
		for (Component c : output.getComponents()) {
			if (c.getName().compareTo(parameter.result) == 0) {
				output.setSelectedComponent(c);
			}
		}
		log("Analyse end: " + parameter.result);
	}
	double[] Arrays_copyOf(double[] s, int end){
		double[] r=new double [end];
		for(int k=0;k<end;k++){
			r[k]=s[k];
		}
		return r;
	}
	double[] Arrays_copyOf(double[] s, int start, int end){
		double[] r=new double [end-start];
		for(int k=start;k<end;k++){
			r[k-start]=s[k];
		}
		return r;
	}
	protected void analyseBatch(Parameter parameter) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		if (network == null || view == null) {
			alert("You have to load protein ineraction network first!");
			return;
		}

		// init
		String name = parameter.result;
		Map map = buildMap();
		double fold[] = getDoubleAttribute(map, name
				+ Config.SUFFIX_SUBNET_SIZE);
		boolean sel[] = getBooleanAttribute(map, name + Config.SUFFIX_SELECT);

		// Process
		BatchResultPanel panel = new BatchResultPanel(this, parameter);
		output.add(parameter.result, panel);

		int i, j;
		for (i = j = 0; j < fold.length; ++j) {
			if (sel[j]) {
				if (i != j) {
					double t = fold[i];
					fold[i] = fold[j];
					fold[j] = t;
				}
				++i;
			}
		}
		
		double sourceFold[] = Arrays_copyOf(fold, i);
		double disturbedFold[] = Arrays_copyOf(fold, i, fold.length);

		
		HistogramDataset histogramdataset = new HistogramDataset();
		histogramdataset.addSeries("", sourceFold, 100);
		JFreeChart jfreechart = ChartFactory.createHistogram(
				null, null,
				null, histogramdataset, PlotOrientation.VERTICAL, false,
				false, false);
        jfreechart.getXYPlot().getRangeAxis().setLabel("Number of Disturbed Subgroups");
        jfreechart.getXYPlot().getDomainAxis().setLabel("Disturbed Subgroup Size");
		jfreechart.getXYPlot().setForegroundAlpha(0.75F);
		ChartPanel p = new ChartPanel(jfreechart);
		p.setHorizontalAxisTrace(true);
		panel.rightPane.add("Center",p);
		
		panel.splitPane.setOneTouchExpandable(true);
		panel.splitPane.setResizeWeight(0.5);
//		highlightNodes(map, sel);
//
//		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
//		NodeAppearanceCalculator nac = manager.getVisualStyle()
//				.getNodeAppearanceCalculator();
//		nac.setCalculator(createCalculator(parameter));
//		manager.applyNodeAppearances();
//		Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
	}

	protected void analyseSingle(Parameter parameter) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		if (network == null || view == null) {
			alert("You have to load protein ineraction network first!");
			return;
		}

		Map map = buildMap();
		String name = parameter.result;
		boolean sel[] = getBooleanAttribute(map, name + Config.SUFFIX_SELECT);

		double fold[] = getDoubleAttribute(map, name + Config.SUFFIX_FOLD);

		int i, j;
		for (i = j = 0; j < fold.length; ++j) {
			if (sel[j]) {
				if (i != j) {
					double t = fold[i];
					fold[i] = fold[j];
					fold[j] = t;
				}
				++i;
			}
		}
		double sourceFold[] = Arrays_copyOf(fold, i);
		double disturbedFold[] = Arrays_copyOf(fold, i, fold.length);
		for (i = j = 0; j < disturbedFold.length; ++j) {
			if (Math.abs(disturbedFold[j]-1.0D)<1e-3) {
				if (i != j) {
					double t = fold[i];
					fold[i] = fold[j];
					fold[j] = t;
				}
				++i;
			}
		}
		double nonzeroDisturbedFold[] = Arrays_copyOf(disturbedFold, i, disturbedFold.length);
		//System.out.printf("%d %d %d\n", sourceFold.length, disturbedFold.length, nonzeroDisturbedFold.length);

		ResultPanel panel = new ResultPanel(this, parameter);
		output.add(parameter.result, panel);

		String names[] = { "Source", "Disturbed" };
		double folds[][] = { sourceFold, disturbedFold };
		for (int k = 1; k < names.length; k++) {
			HistogramDataset histogramdataset = new HistogramDataset();
			histogramdataset.addSeries("", folds[k], 100);

			JFreeChart jfreechart = ChartFactory.createHistogram(null, null,
					null, histogramdataset, PlotOrientation.VERTICAL, false,
					true, false);
	        jfreechart.getXYPlot().getRangeAxis().setLabel("Number of Disturbed Proteins");
	        jfreechart.getXYPlot().getDomainAxis().setLabel("Change Ratio");
			jfreechart.getXYPlot().setForegroundAlpha(0.75F);
			// LogAxis yAxis = new LogAxis();
			// jfreechart.getXYPlot().setRangeAxis(yAxis);
			ChartPanel p = new ChartPanel(jfreechart);
			p.setHorizontalAxisTrace(true);
			panel.rightPane.add("Center",p);
			//panel.rightPane.add(names[k], p);
		}

		panel.splitPane.setOneTouchExpandable(true);
		panel.splitPane.setResizeWeight(0.5);		
	}

	class PerturbationAction extends CytoscapeAction {
		public PerturbationAction() {
			super(Config.NAME);
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			CytoPanel panel = Cytoscape.getDesktop().getCytoPanel(
					SwingConstants.WEST);
			panel.setSelectedIndex(panel.indexOfComponent(Config.NAME));
			if (true)
				return;
		}
	}

	protected void log(String msg) {
		control.infoText.append(now() + " " + msg + "\n");
		control.infoText.setCaretPosition(control.infoText.getDocument()
				.getLength());
	}

	protected boolean confirm(String msg) {
		return JOptionPane.showConfirmDialog(Cytoscape.getCurrentNetworkView()
				.getComponent(), msg, Config.NAME, JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE) == JOptionPane.OK_OPTION;
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
		if (s.length > 0)
			buffer.append(s[0]);
		for (int k = 1; k < s.length; k++) {
			buffer.append(delimiter);
			buffer.append(s[k]);
		}
		return buffer.toString();
	}

	private static final String DATE_FORMAT_NOW = "yyyy/MM/dd HH:mm:ss";
	private static final String DATE_FORMAT_NOW_FILE = "yyyyMMddHHmmss";

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}
	public static String nowForFile() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW_FILE);
		return sdf.format(cal.getTime());
	}
	
	public static void showNodeMappingEditor() {
		try {
			VisualPropertyType.NODE_FILL_COLOR.showContinuousEditor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//from http://www.javalobby.org/forums/thread.jspa?threadID=16906&tstart=0
	public static String getOrdinalFor(int value) {
		 int hundredRemainder = value % 100;
		 int tenRemainder = value % 10;
		 if(hundredRemainder - tenRemainder == 10) {
		  return "th";
		 }
		 
		 switch (tenRemainder) {
		  case 1:
		   return "st";
		  case 2:
		   return "nd";
		  case 3:
		   return "rd";
		  default:
		   return "th";
		 }
		}
	
	// public void proteinAbundanceChoiceItemStateChanged(ItemEvent e) {
	// if (control.iterativeCriteria.getText().length()>0)
	// return;
	//		
	// CyNetwork network = Cytoscape.getCurrentNetwork();
	// CyAttributes attributes = Cytoscape.getNodeAttributes();
	//
	// Double minAbundance=null;
	// String id;
	// for (Iterator i = network.nodesIterator(); i.hasNext();) {
	// CyNode node = (CyNode) i.next();
	// id = node.getIdentifier();
	//			
	// double abundance = attributes.getAttribute(id, name);
	// if (minAbundance!=null)
	// minAbundance = Math.min(minAbundance, abundance);
	// else
	// minAbundance = abundance;
	// }
	//		
	// control.iterativeCriteria.setText(String.valueOf(minAbundance));
	// }

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		// JOptionPane.showMessageDialog(Cytoscape.getCurrentNetworkView()
		// .getComponent(), e.getPropertyName());
		// log(e.getPropertyName());
		if (e.getPropertyName().equalsIgnoreCase(
				CytoscapeDesktop.NETWORK_VIEW_FOCUS)) {
			log("Loading network");
		}
		if (e.getPropertyName().equalsIgnoreCase("NETWORK_LOADED")) {
			setupAnalyse();
		} else if (e.getPropertyName().equalsIgnoreCase(
				Cytoscape.ATTRIBUTES_CHANGED)) {
			// log("Attributes Changed");

			Object originalString1 = control.proteinAbundanceChoice
					.getSelectedItem();

			control.proteinAbundanceChoice.removeAllItems();

			CyAttributes attributes;
			String[] attrArray;

			attributes = Cytoscape.getNodeAttributes();
			attrArray = attributes.getAttributeNames();
			Arrays.sort(attrArray);
			for (String s : attrArray) {
				if (attributes.getType(s) == CyAttributes.TYPE_FLOATING)
					control.proteinAbundanceChoice.addItem(s);
			}

			control.proteinAbundanceChoice.setSelectedItem(originalString1);
		}
	}

	// protected void loadSampleData() {
	// try{
	// Class c=getClass();
	// InputStream inStream = c.getResourceAsStream("/data/maslov.xgmml");
	//
	// File temp = File.createTempFile("maslov", ".xgmml");
	// FileOutputStream outStream = new FileOutputStream(temp);
	// byte[] buffer = new byte[8196];
	// int length;
	// int byteread = 0;
	// while ((byteread = inStream.read(buffer)) != -1) {
	// outStream.write(buffer, 0, byteread);
	// }
	// inStream.close();
	//
	// Cytoscape.createNetworkFromFile(temp.getAbsolutePath());
	// //Cytoscape.getDesktop().getCytoPanel(SwingConstants.CENTER).setState(CytoPanelState.DOCK);
	// //Cytoscape.getDesktop().pack();
	// control.resultChoice.getEditor().setItem("Perturbation.freeConcentrations");
	// log("Load sample data complete");
	// }catch(Exception e){
	// e.printStackTrace();
	// alert("An error may occur when loading sample data!");
	// }
	// }

	// assume that nodes have Perturbation.totalConcentration attribute
	// generate Perturbation.freeConcentration attribute
	protected void onCalculate() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		if (network == null || view == null || network.getNodeCount() == 0) {
			alert("You have to load protein ineraction network first!");
			return;
		}

		// init
		if (control.proteinAbundanceChoice.getSelectedItem() == null) {
			info("You must select the property of protein abundance as total concentration");
			return;
		}

		int nSelectedNode = network.getSelectedNodes().size();
		// if(nSelectedNode>0)
		// alert("You have selected "+nSelectedNode+" proteins, " +
		// "and these proteins will be upregulated 2-fold as sources of perturbation");
		if (nSelectedNode == 0) {
			info("You must select one or more proteins as perturbation sources, "
					+ "and these proteins will be upregulated 2-fold as sources of perturbation");
			return;
		}

		Parameter parameter = new Parameter();
		parameter.proteinAbundance = control.proteinAbundanceChoice
				.getSelectedItem().toString();
		parameter.result = control.getResultName();
		parameter.perturbationType = control.getPerturbationType();
		parameter.changeFold = Double.parseDouble(control.changeFoldChoice
				.getEditor().getItem().toString());
		parameter.iterativeCriteria = Double
				.parseDouble(control.iterativeCriteria.getText());
		parameter.subgroupThreshold = Double
				.parseDouble(control.disturbedThreshold.getText())/100D;
		parameter.dissociationConstant = control.getDissociationConstant();
		
		String name = parameter.result;
		if (attributes.getMapAttribute(network.getIdentifier(),
				Config.PREFIX_PARAMETER + name) != null) {
			if (!confirm(name + " seems being used to save previous result, "
					+ "are you sure to clear it?")) {
				return;
			} else {
				attributes.deleteAttribute(name + Config.SUFFIX_SELECT);
				attributes.deleteAttribute(name + Config.SUFFIX_FC_BEFORE);
				attributes.deleteAttribute(name + Config.SUFFIX_FC_AFTER);
				attributes.deleteAttribute(name + Config.SUFFIX_FOLD);
				attributes.deleteAttribute(name + Config.SUFFIX_SUBNET_SIZE);
			}
		}

		Cytoscape.getNetworkAttributes().setMapAttribute(
				network.getIdentifier(), Config.PREFIX_PARAMETER + name,
				parameter.toMap());

		// Process
		log("Calculate start: " + network.getTitle());
		Task task = new Worker(this, parameter);
		JTaskConfig config = new JTaskConfig();
		config.displayCancelButton(true);
		config.displayStatus(true);
		// config.displayTimeRemaining(true);
		boolean success = TaskManager.executeTask(task, config);

		if (success) {
			log("Calculate end: " + network.getTitle());
			analyse(parameter);
		} else {
			log("Calculate cancelled: " + network.getTitle());
		}
	}

	public void highlightSubnetwork(DefaultTableModel model,
			Parameter parameter, double cutoff, boolean highlightNetwork) {
		DecimalFormat formatter = new DecimalFormat("0.##");

		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CyAttributes attributes = Cytoscape.getNodeAttributes();

		int n = network.getNodeCount();
		String name = parameter.result;

		Map map = buildMap();

		double fold[];
		if (parameter.perturbationType == Config.PERTURBATION_SINGLE){
			fold = getDoubleAttribute(map, name + Config.SUFFIX_FOLD);
			model.setValueAt(cutoff, 0, 1);
		}
		else if (parameter.perturbationType == Config.PERTURBATION_BATCH){
			fold = getDoubleAttribute(map, name + Config.SUFFIX_SUBNET_SIZE);
			model.setValueAt(parameter.subgroupThreshold, 0, 1);
		}
		else {
			alert("Invalid perturbationType!");
			return;
		}
		boolean sel[] = getBooleanAttribute(map, name + Config.SUFFIX_SELECT);

		SortedMap subnodes = new TreeMap();

		double sum=0,min=Double.MAX_VALUE,max=-Double.MAX_VALUE;
		boolean highlight[] = new boolean[n];
		String id;
		int idx;
		for (Iterator i = network.nodesIterator(); i.hasNext();) {
			CyNode node = (CyNode) i.next();
			id = node.getIdentifier();
			idx = (Integer) map.get(id);
			if ((parameter.perturbationType == Config.PERTURBATION_SINGLE && Math.abs(fold[idx] - 1.0D) >= cutoff)
					||(parameter.perturbationType == Config.PERTURBATION_BATCH && fold[idx] >= cutoff)
					)
			{

				subnodes.put(id, fold[idx]);
				highlight[idx] = true;
				sum+=Math.abs(fold[idx]);
				min=Math.min(min, fold[idx]);
				max=Math.max(max, fold[idx]);
			}
		}
		int m = subnodes.size();
		if(m>0){
			sum/=m;
		}
		model.setValueAt(m, 1, 1);
		model.setValueAt(sum, 2, 1);
		model.setValueAt(max, 3, 1);
		model.setValueAt(min, 4, 1);
		
		if (highlightNetwork) {
			highlightNodes(map, highlight);
			//Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
			Cytoscape.getCurrentNetworkView().updateView();
		}


		model.setNumRows(m + 6);
		Iterator iterator = subnodes.keySet().iterator();
		int k = 0;
		while (iterator.hasNext()) {
			Object key = iterator.next();
			model.setValueAt(key.toString(), 6 + k, 0);
			model.setValueAt(subnodes.get(key).toString(), 6 + k, 1);
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
		if (attributes.getType(name) != CyAttributes.TYPE_FLOATING) {
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
		if (attributes.getType(name) != CyAttributes.TYPE_BOOLEAN) {
			alert(name + " must be a boolean node attribute");
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

	public void highlightSources(Map map, boolean[] sel) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes attributes = Cytoscape.getNodeAttributes();

		String id;
		int idx;
		for (Iterator i = network.nodesIterator(); i.hasNext();) {
			CyNode node = (CyNode) i.next();
			id = node.getIdentifier();
			idx = (Integer) map.get(id);
			//network.setSelectedNodeState(node, sel[idx]);
			if(sel[idx]){
				NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(node);
				nv.setHeight(nv.getHeight()*2);
				nv.setWidth(nv.getWidth()*2);
				nv.setBorderWidth((float) (nv.getHeight()/8));
				nv.setBorderPaint(new Color(250,250,0));
//				System.out.println(nv);
//				System.out.println(nv.getHeight());
//				System.out.println(nv.getWidth());
//				System.out.println(nv.getBorderWidth());
//				attributes.setAttribute(id, VisualPropertyType.NODE_BORDER_COLOR.getBypassAttrName(), "250,250,0");
//				attributes.setAttribute(id, VisualPropertyType.NODE_LINE_WIDTH.getBypassAttrName(), ""+nv.getHeight()/5);
//				attributes.setAttribute(id, VisualPropertyType.NODE_HEIGHT.getBypassAttrName(), ""+(nv.getHeight()*2));
//				attributes.setAttribute(id, VisualPropertyType.NODE_WIDTH.getBypassAttrName(), ""+(nv.getWidth()*2));
//				System.out.println("highlightSources VisualPropertyType.NODE_BORDER_COLOR "+id);
//				System.out.println(VisualPropertyType.NODE_LINE_WIDTH.getValueParser());
//				System.out.println(VisualPropertyType.NODE_LINE_WIDTH.getVisualProperty());
//				System.out.println(VisualPropertyType.NODE_LINE_WIDTH.getVisualProperty().getDefaultAppearanceObject());
			}
		}
		//Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
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
				.getComponent(), msg, Config.NAME,
				JOptionPane.INFORMATION_MESSAGE);
	}

}

/**
 * Calculate Task, used to illustrate the Task Framework. This tasks counts from
 * 0 to maxValue.
 */
class Worker implements Task {
	private TaskMonitor taskMonitor = null;
	private boolean interrupted = false;
	private Perturbation perturbation = null;
	private Parameter parameter;

	public Worker(Perturbation p, Parameter param) {
		perturbation = p;
		parameter = param;
	}

	public double[] maslov(String taskTitle, double[] tc, int[][] s)
			throws InterruptedException {
		perturbation.log(taskTitle);
		int n = tc.length;
		// Calculate
		// function fc=my_free_conc(tc,hprd_s1)
		// Fc=tc;
		double[] fc = tc.clone();
		double[] fc_old = tc.clone();
		// s1=hprd_s1; %s1=1./K;
		// k=340;
		double k = parameter.dissociationConstant;//340;
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
					if(Math.abs(k)<Double.MIN_VALUE){
						sum += s[i][j] * fc[j] / (Math.max(tc[i],tc[j])/20);
					}
					else
						sum += s[i][j] * fc[j] / k;
				}
				fc[i] = tc[i] / (1 + sum);

				max = Math.max(max, Math.abs(fc_old[i] - fc[i]) / fc[i]);
				fc_old[i] = fc[i];
			}
			DecimalFormat formatter = new DecimalFormat("0.####E0");
			// perturbation.log("[" + iter + "," + formatter.format(max) + ","
			// + eps + "]");
			taskMonitor.setStatus(taskTitle + " " + iter + " / "
					+ formatter.format(max));
			if (max < eps)
				break;
			// Thread.sleep(300);
		}
		for (int i = 0; i < n; i++)
			s[i][i] /= 2;
		return fc;
	}

	public void run() {
		if (taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}

		try {
			CyNetwork network = Cytoscape.getCurrentNetwork();
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			CyAttributes attributes = Cytoscape.getNodeAttributes();
			int n = network.getNodeCount();

			perturbation.log("> Protein abundance: "
					+ parameter.proteinAbundance);
			perturbation.log("> Save result as: " + parameter.result);
			perturbation.log("> Change fold: " + parameter.changeFold);
			perturbation.log("> Select " + network.getSelectedNodes().size()
					+ " node(s)");

			if (parameter.perturbationType == Config.PERTURBATION_SINGLE)
				calculateSingle();
			else if (parameter.perturbationType == Config.PERTURBATION_BATCH)
				calculateBatch();

			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null,
					null);
		} catch (InterruptedException e) {
			taskMonitor.setException(e, "Calculating cancelled");
		}
	}

	protected void calculateBatch() throws InterruptedException {
		String id;
		int idx;

		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		int n = network.getNodeCount();

		String name = parameter.result;
		double changeFold = parameter.changeFold;
		// init
		Map map = perturbation.buildMap();

		double[] tc = perturbation.getDoubleAttribute(map,
				parameter.proteinAbundance);
		int[][] s = perturbation.getAdjacentMatrix(map);

		// calc
		double[] fc_before = maslov("Calulate original fc before perturbation",
				tc, s);

		double[] subnet_size = new double[n];
		boolean[] sel = new boolean[n];
		Arrays.fill(subnet_size, 0);
		Arrays.fill(sel, false);
		
		int count = 0;
		for (Object selectedNode : network.getSelectedNodes()) {
			count++;
			idx = (Integer) map.get(((CyNode) selectedNode).getIdentifier());
			double tc0 = tc[idx];
			sel[idx] = true;

			if (changeFold > 0)
				tc[idx] *= changeFold;
			else if (changeFold < 0)
				tc[idx] /= (-changeFold);

			double[] fc_after = maslov("Calulate the " + count
					+ perturbation.getOrdinalFor(count)+" fc after perturbation", tc, s);

			double fc_fold = 0;
			for (int k = 0; k < n; k++) {
				if (Math.abs(fc_before[k]) < 1e-10
						&& Math.abs(fc_after[k]) > 1e-10) {
					subnet_size[idx]++;
				} else {
					fc_fold = (fc_before[k] != 0 ? fc_after[k] / fc_before[k]
							: Double.NaN);
					// TODO changeFold threshold
					if (Math.abs(fc_fold - 1) > 0.2D) {
						subnet_size[idx]++;
					}
				}
			}

			tc[idx] = tc0;
		}

		// write back
		perturbation.setAttribute(map, name + Config.SUFFIX_SUBNET_SIZE,
				subnet_size);
		perturbation.setAttribute(map, name + Config.SUFFIX_SELECT, sel);
	}

	protected void calculateSingle() throws InterruptedException {
		String id;
		int idx;

		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		int n = network.getNodeCount();

		String name = parameter.result;
		double changeFold = parameter.changeFold;
		// init
		Map map = perturbation.buildMap();

		double[] tc = perturbation.getDoubleAttribute(map,
				parameter.proteinAbundance);
		int[][] s = perturbation.getAdjacentMatrix(map);

		// calc
		double[] fc_before = maslov("Calulate fc before perturbation", tc, s);

		boolean[] sel = new boolean[n];
		for (Object selectedNode : network.getSelectedNodes()) {
			idx = (Integer) map.get(((CyNode) selectedNode).getIdentifier());
			if (changeFold > 0)
				tc[idx] *= changeFold;
			else if (changeFold < 0)
				tc[idx] /= (-changeFold);
			sel[idx] = true;
		}

		double[] fc_after = maslov("Calulate fc after perturbation", tc, s);

		// write back
		double[] fc_fold = new double[n];
		for (idx = 0; idx < n; idx++) {
			fc_fold[idx] = (fc_before[idx] != 0 ? fc_after[idx]
					/ fc_before[idx] : Double.NaN);
			//if(sel[idx]) fc_fold[idx]=0; // patch for better color mapping
		}

		perturbation.setAttribute(map, name + Config.SUFFIX_FC_BEFORE,
				fc_before);
		perturbation.setAttribute(map, name + Config.SUFFIX_FC_AFTER, fc_after);
		perturbation.setAttribute(map, name + Config.SUFFIX_FOLD, fc_fold);
		perturbation.setAttribute(map, name + Config.SUFFIX_SELECT, sel);
	}

	public void halt() {
		interrupted = true;
	}

	public void setTaskMonitor(TaskMonitor tm) {
		if (taskMonitor != null) {
			throw new IllegalStateException("Task Monitor is already set.");
		}
		taskMonitor = tm;
	}

	public String getTitle() {
		return new String("Calculating perturbation, please waiting...");
	}
}
