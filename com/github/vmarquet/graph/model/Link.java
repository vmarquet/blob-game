package com.github.vmarquet.graph.model;

import java.lang.Math;
import java.awt.Color;

public class Link {

	private static SimulationModel model = null;

	private Node node_start = null;
	private Node node_end   = null;
	
	private double length = 0.1;  // longueur au repos
	private double rigidity = 50.0;  // constante de raideur

	private Color color = Color.decode("#888888");

	public Link(Node node_start, Node node_end) {
		if (this.model == null)
			this.model = SimulationModel.getInstance();
		this.node_start = node_start;
		this.node_end   = node_end;
	}
	public Link(int start_number, int end_number) {
		if (this.model == null)
			this.model = SimulationModel.getInstance();
		this.node_start = this.model.getNodeNumber(start_number);
		this.node_end   = this.model.getNodeNumber(end_number);
	}

	// to compute the distance between the nodes
	public double getDistance() {
		return Link.getDistance(this.node_start, this.node_end);
	}
	public static double getDistance(Node node1, Node node2) {
		if (node1 == null || node2 == null) {
			System.out.println("WARNING: Link.getDistance(): node == null");
			return -1;  // TODO: il faudrait mieux renvoyer une exception
		}
		double delta_x = node1.pos_x - node2.pos_x;
		double delta_y = node1.pos_y - node2.pos_y;
		double distance = Math.sqrt(delta_x*delta_x + delta_y*delta_y);
		return distance;
	}

	// getters:
	public Node getStartNode() {
		return this.node_start;
	}
	public Node getEndNode() {
		return this.node_end;
	}
	public Color getColor() {
		return this.color;
	}
	public double getLength() {
		return this.length;
	}
	public double getRigidity() {
		return this.rigidity;
	}
	
	//setters:
	public void setLength(double newLength) {
		this.length = newLength;
	}
	public void setRigidity(double newRigidity) {
		this.rigidity = newRigidity;
	}

	// pour les forces qui s'appliquent d'un noeud sur l'autre
	public void applyForceBetweenNodes(double f) {
		Link.applyForceBetweenNodes(f, this.node_start, this.node_end);
	}
	public static void applyForceBetweenNodes(double f, Node node1, Node node2) {
		// on calcule la projection de la force sur les axes x et y
		// et on l'applique

		double dist = Link.getDistance(node1, node2);
		if (dist <= 0)
			return;

		double delta_x = node1.pos_x - node2.pos_x;
		double delta_y = node1.pos_y - node2.pos_y;

		double cos_theta = delta_x / dist;
		double sin_theta = delta_y / dist;

		node1.for_x += f*cos_theta;
		node2.for_x -= f*cos_theta;

		node2.for_y -= f*sin_theta;
		node1.for_y += f*sin_theta;

		return;
	}

}
