package com.github.vmarquet.graph.view;

import com.github.vmarquet.graph.model.*;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;



public class SimulationViewJPanel extends JPanel implements SimulationView {

	private SimulationModel model = null;

	private double margin_x;
	private double margin_y;
	private double min_size;

	public SimulationViewJPanel() {
		// on récupère l'instance du modèle (pattern singleton)
		this.model = SimulationModel.getInstance();
	}

	public void updateDisplay() {
		this.updateUI();
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// on caste l'objet Graphics en Graphics2D car plus de fonctionnalités
		Graphics2D g2d = (Graphics2D) g;
		// si on veut de l'antialiasing (ATTENTION ça fait ramer un max quand beaucoupd de noeuds)
		// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// we clear the background
		Color backgroundColor = Color.decode("#000000");
		this.setBackground(backgroundColor);

		// we compute the ratio for the display
		computeMargin(g2d);

		// we paint the objetcs
		paintLinks(g2d);
		paintNodes(g2d);
	}
	private void paintNodes(Graphics2D g) {
		for (Node node : model.getNodes()) {
			Color color = node.getColor();
			double diameter = min_size*node.getDiameter();
			// we draw the circle:
			g.setColor(color);
			g.fillOval((int)(convertNodePositionToPixelX(node)-diameter/2), 
			           (int)(convertNodePositionToPixelY(node)-diameter/2), 
			           (int)diameter, (int)diameter);
			// we draw the text (node number / ...)
			g.setColor(Color.BLACK);
			g.drawString(Integer.toString(node.getNodeNumber()),
			             (int)(convertNodePositionToPixelX(node) -7),
			             (int)(convertNodePositionToPixelY(node) +4));
		}

	}
	private void paintLinks(Graphics2D g) {
		for (Link link : model.getLinks()) {
			Color color = link.getColor();
			g.setColor(color);
			Node node1 = link.getStartNode();
			Node node2 = link.getEndNode();
			g.drawLine((int)(convertNodePositionToPixelX(node1)), 
			           (int)(convertNodePositionToPixelY(node1)), 
			           (int)(convertNodePositionToPixelX(node2)), 
			           (int)(convertNodePositionToPixelY(node2)));
		}
	}

	public void computeMargin(Graphics2D g) {
		int width = this.getWidth();
		int height = this.getHeight();
		g.setColor(Color.decode("#111111"));

		if (width > height) {
			this.min_size = height;
			this.margin_x = (width-height)/2;
			this.margin_y = 0;
			g.drawLine(0,0,(int)margin_x,height);
			g.fillRect(width-(int)margin_x,0,(int)margin_x,height);
		}
		else {
			this.min_size = width;
			this.margin_x = 0;
			this.margin_y = (height-width)/2;
			g.fillRect(0,0,width,(int)margin_y);
			g.fillRect(0,height-(int)margin_y,width,(int)margin_y);
		}
	}

	// the values used for the simulation and stored in the node class are NOT pixel values
	// because the simulation should not depend on the size of the window
	// so the following functions convert these values to pixels
	// these functions return the position of the center of the node
	private double convertNodePositionToPixelX(Node node) {
		return margin_x+node.pos_x*min_size;
	}
	private double convertNodePositionToPixelY(Node node) {
		return margin_y+node.pos_y*min_size;
	}
	private double convertNodeDiameterToPixel(Node node) {
		return min_size*node.getDiameter();
	}
	// and to do the reverse operation:
	// (it returns a double between 0 and 1)
	private double convertPixelToNodePositionX(int x) {
		double x_f = (double)x;
		return (x_f-margin_x)/min_size;
	}
	private double convertPixelToNodePositionY(int y) {
		double y_f = (double)y;
		return (y_f-margin_y)/min_size;
	}

}
