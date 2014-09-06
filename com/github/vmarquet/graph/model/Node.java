package com.github.vmarquet.graph.model;

import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.collision.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.contacts.*;
import org.jbox2d.callbacks.*;
import java.awt.Color;

public class Node {

	private int number;   // starts from 0
	private int graphNumber; // starts from 0
	
	private static int totalNodeNumber = 0;

	public double pos_x = 0;  // position
	public double pos_y = 0;
	public double vit_x = 0;  // speed
	public double vit_y = 0;
	public double acc_x = 0;  // acceleration
	public double acc_y = 0;
	public double for_x = 0;  // force
	public double for_y = 0;	

	// physic simulation: a node is a mass:
	private double mass = 1.5;

	private Color color = Color.WHITE;
	private double diameter = 0.06;
	private boolean hung = false;  // when set to true, the node cannot move
	private boolean grabbed = false;  // to know if the node has been grabbed by the mouse

	private Body body = null;

	// constructors:
	// if node number < 0, automatic numerotation
	public Node(int number) {
		setNodeNumber(number);
	}
	public Node(int number, double X, double Y) {
		setNodeNumber(number);
		setPosition(X,Y);
	}
	public Node(int number, double X, double Y, double mass) {
		setNodeNumber(number);
		setPosition(X,Y);
		this.mass = mass;
	}
	private void setNodeNumber(int number) {
		if (number < 0)  // automatic numerotation
			this.number = totalNodeNumber;
		else
			this.number = number;
		this.totalNodeNumber++;
	}

	// setters:
	public void setPosition(double X, double Y) {
		this.pos_x = X;
		this.pos_y = Y;
	}
	public static void resetTotalNodeNumber() {
		totalNodeNumber = 0;
	}
	public boolean isHung() {
		return this.hung;
	}
	public void hang() {
		this.hung = true;
	}
	public void unhang() {
		this.hung = false;
	}
	public boolean isGrabbed() {
		return this.grabbed;
	}
	public void grab() {
		this.grabbed = true;
	}
	public void release() {
		this.grabbed = false;
	}
	public void setMass(double newMass) {
		this.mass = newMass;
	}
	public void setGraphNumber(int newGraphNumber) {
		this.graphNumber = newGraphNumber;
	}
	public void setBody(Body body) {
		this.body = body;
	}

	// getters:
	public int getNodeNumber() {
		return this.number;
	}
	public double getMass() {
		return this.mass;
	}
	public Color getColor() {
		return this.color;
	}
	public double getDiameter() {
		return this.diameter;
	}
	public int getGraphNumber() {
		return this.graphNumber;
	}
	public Body getBody() {
		return this.body;
	}
}
