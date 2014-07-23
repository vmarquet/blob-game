package com.github.vmarquet.graph.controler;

import com.github.vmarquet.graph.model.SimulationModel;
import com.github.vmarquet.graph.model.Node;
import com.github.vmarquet.graph.model.Link;
import java.io.*;
import java.util.Scanner;

public class GraphReaderFromFile {

	private SimulationModel model = null;

	public GraphReaderFromFile(String filename) {
		
		int nb_graph = -1;
	
		// we get the instance of the model (singleton pattern)
		this.model = SimulationModel.getInstance();

		// we read the file containing nodes and links description
		try {
			Scanner scanner = new Scanner(new File(filename));

			// we get the number of nodes:
			int numberOfNodes;
			if(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				numberOfNodes = Integer.parseInt(line);
			}
			else {
				return;  // TODO: FileNotValidException
			}

			// we create the nodes in the model:
			for (int i = 0; i < numberOfNodes; i++) {
				Node node = new Node(i);
				model.addNode(node);
				// the nodes are not linked, they all have a different graph number
				model.getNodeNumber(i).setGraphNumber(-1);
				// we set them to random positions
				// (it's possible to override this after)
				node.pos_x = Math.random();
				node.pos_y = Math.random();
			}

			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] word;
				switch(line.charAt(0)) {
					case 'N':  // setting node start position: 
						// "N NODE_NUMBER START_PIXEL_X START_PIXEL_Y"
						word = line.split(" ");
						if (word.length != 4) // invalid line
							break;
						int number  = Integer.parseInt(word[1]);
						if (number < 0 || number >= numberOfNodes )
							break;
						double start_x = Double.parseDouble(word[2]);
						if (start_x < 0 || start_x > 1)
							break;
						double start_y = Double.parseDouble(word[3]);
						if (start_y < 0 || start_y > 1 )
							break;
						this.model.setNodePosition(number, start_x, start_y);
						break;
					case 'L':  // link: "L START_NODE_NUMBER END_NODE_NUMBER"
						word = line.split(" ");
						if (word.length != 3) // invalid line
							break;
						int node_start = Integer.parseInt(word[1]);
						if (node_start < 0 || node_start >= numberOfNodes )
							break;
						int node_end   = Integer.parseInt(word[2]);
						if (node_end < 0 || node_end >= numberOfNodes)
							break;
						// if it's a link from a node to itself, it may cause a bug
						if (node_start == node_end)
							break;
						// TODO: if a link already exists, we should not create one again
						Link link = new Link(node_start, node_end);
						this.model.addLink(link);
						
						//we set the graph number
						if(this.model.getNodeNumber(node_start).getGraphNumber() != -1) // node 1 is already in a graph
							this.model.getNodeNumber(node_end).setGraphNumber(this.model.getNodeNumber(node_start).getGraphNumber());
							// node 2 takes the same graph number 
						else if(this.model.getNodeNumber(node_end).getGraphNumber() != -1) // node 2 is already in a graph
							this.model.getNodeNumber(node_start).setGraphNumber(this.model.getNodeNumber(node_end).getGraphNumber());
							// node 1 takes the same graph number
						else  // the 2 nodes don't belong to the same graph
						{     // we create a new one
							this.model.getNodeNumber(node_start).setGraphNumber(nb_graph + 1);
							this.model.getNodeNumber(node_end).setGraphNumber(nb_graph + 1);
							nb_graph++;	
						}
						break;
					case '#':  // commentary: "# anything"
						break;
					default:
						break;
				}
			}


		} catch(FileNotFoundException ex) {
			ex.printStackTrace();
		}

	}
}
