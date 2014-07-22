package com.github.vmarquet.graph.test;

import com.github.vmarquet.graph.model.SimulationModel;
import com.github.vmarquet.graph.model.Node;
import com.github.vmarquet.graph.model.Link;
import com.github.vmarquet.graph.view.SimulationView;
import com.github.vmarquet.graph.view.SimulationViewJPanel;
import com.github.vmarquet.graph.controler.GraphReaderFromFile;
import com.github.vmarquet.graph.controler.SimulationControler;
import javax.swing.JFrame;
import java.lang.Thread;
import javax.swing.WindowConstants;

public class Test {

	public static void main(String[] args) {
		
		// on remplit le modèle
		GraphReaderFromFile reader = new GraphReaderFromFile("graphs/graph03.txt");

		SimulationModel model = SimulationModel.getInstance();
		model.print();

		// on crée une vue pour la simulation
		SimulationViewJPanel panel = new SimulationViewJPanel();
		JFrame fen = new JFrame();
		fen.setSize(640,480);
		//fen.setResizable(false);
		fen.add(panel);
		fen.setVisible(true);
		fen.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		panel.requestFocus();

		// on lance la simulation
		Thread thread = new Thread(new SimulationControler(panel));
		thread.start();

		return;
	}

}
