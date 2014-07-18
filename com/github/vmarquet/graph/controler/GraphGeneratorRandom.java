package com.github.vmarquet.graph.controler;

import com.github.vmarquet.graph.model.SimulationModel;
import com.github.vmarquet.graph.model.Node;
import com.github.vmarquet.graph.model.Link;
import java.io.*;
import java.util.Scanner;

public class GraphGeneratorRandom {

	private SimulationModel model = null;

	public GraphGeneratorRandom(int numberOfNodes) {
		// we get the instance of the model (singleton pattern)
		this.model = SimulationModel.getInstance();

		if (numberOfNodes < 0)
			return;

		// we create the nodes in the model:
		for (int i = 0; i < numberOfNodes; i++) {
			Node node = new Node(i);
			model.addNode(node);
			// we set them to random positions
			node.pos_x = Math.random();
			node.pos_y = Math.random();
		}

		// we create links beetween all the nodes
		for (int i=0; i<numberOfNodes; i++) {
			for (int j=i+1; j<numberOfNodes; j++) {
				Link link = new Link(i,j);
				model.addLink(link);
			}
		}

	}
}
