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
import javax.swing.*;



/**
 * @author PITTACUS
 */
public class OutputPanel extends JPanel {
	public OutputPanel() {
		initComponents();	
	}
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		tabbedPane = new JTabbedPane();

		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	public JTabbedPane tabbedPane;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
