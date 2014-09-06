package com.github.vmarquet.graph.model;

import java.util.*; // pour les math
import com.github.vmarquet.graph.model.*;
import com.github.vmarquet.graph.physicalworld.*;

public class SimulationModel {  // singleton pattern

	private static SimulationModel instance = null;
	private ArrayList<Node> nodes = null;
	private ArrayList<Link> links = null;

	// physic simulation:
	private double lambda = 3.0;   // constante de frottements
	private double coef_repulsion = 0.5;  // constante de répulsion
	private double length = 0.1;  // longueur au repos
	private double rigidity = 50.0;  // constante de raideur

	// jbox2d stuff:
	private PhysicalWorld world = null;


	private SimulationModel() {
		this.nodes = new ArrayList<Node>();
		this.links = new ArrayList<Link>();
	}

	public static SimulationModel getInstance() {  // [synchronized]
		if (instance == null)
			instance = new SimulationModel();
		return instance;
	}

	public void addNode(Node node) {
		this.nodes.add(node);
	}
	public void addLink(Link link) {
		this.links.add(link);
	}

	// to clear the model
	public void clear() {
		this.nodes.clear();
		Node.resetTotalNodeNumber();
		this.links.clear();
	}

	// getters:
	public Node getNodeNumber(int number) {
		if (number < 0 || number >= getNumberOfNodes())
			return null;  // TODO: throw exception
		else
			return this.nodes.get(number);
	}
	public int getNumberOfNodes() {
		return this.nodes.size();
	}
	public ArrayList<Node> getNodes() {
		return this.nodes;
	}
	public ArrayList<Link> getLinks() {
		return this.links;
	}
	public double getLambda() {
		return this.lambda;
	}
	public double getCoefRepulsion() {
		return this.coef_repulsion;
	}
	public double getLength() {
		return this.length;
	}
	public double getRigidity() {
		return this.rigidity;
	}
	public PhysicalWorld getPhysicalWorld() {
		return this.world;
	}

	// setters:
	public void setNodePosition(int number, double X, double Y) {
		Node node = getNodeNumber(number);
		node.setPosition(X,Y);
	}
	public void setRepulsionConstant(double newRepulsionConstant){
		this.coef_repulsion = newRepulsionConstant;
	}
	public void setLambda(double newLambda){
		this.lambda = newLambda;
	}
	public void setPhysicalWorld(PhysicalWorld world) {
		this.world = world;
	}

	// to know if two nodes are linked
	public boolean isLinked(Node node1, Node node2) {
		// on parcourt la liste des liens:
		for (Link link : this.links) {
			if ( (node1 == link.getStartNode() && node2 == link.getEndNode()) ||
				 (node2 == link.getStartNode() && node1 == link.getEndNode()) )
				return true;
		}
		return false;
	}

	public void print() {
		System.out.println("Number of nodes: " + this.nodes.size());
		System.out.println("Number of links: " + this.links.size());
	}

}
