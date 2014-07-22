package com.github.vmarquet.graph.view;

import com.github.vmarquet.graph.model.*;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSlider; 
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Hashtable;
import java.awt.event.MouseListener;
import java.awt.Point;


public class SimulationViewJPanel extends JPanel implements SimulationView, MouseListener {

      
    private SimulationModel model = null;

	private double margin_x;
	private double margin_y;
	private double min_size;

	private Node grabbedNode = null; // link to the node that is grabbed with the mouse

	public SimulationViewJPanel() {
		
		//Slider for link length, link rigidity, repulsion constant and lambda
		JSlider sliderLinkLength, sliderLinkRigidity, sliderRepulsionConstant, sliderLambda;
        
        //Slider for link length
		sliderLinkLength = new JSlider(0,25);
		sliderLinkLength.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
						double lengthValue = (int)source.getValue();
							
						System.out.println("Link Length : "+(lengthValue/20));
						//slider set new link length 
						model.setLength(lengthValue/20);
				}
			}
		});
		Hashtable labelTable1 = new Hashtable();
		labelTable1.put( new Integer(12), new JLabel("Link Length") );
		sliderLinkLength.setLabelTable( labelTable1 );
		sliderLinkLength.setMajorTickSpacing(100); 
		sliderLinkLength.setMinorTickSpacing(1);   
		sliderLinkLength.setPaintLabels(true);  
		sliderLinkLength.setPaintTicks(true);
		this.add(sliderLinkLength);
				
		//Slider for link rigidity
		sliderLinkRigidity = new JSlider(1,100);		
		sliderLinkRigidity.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
						double rigidityValue = (int)source.getValue();
						System.out.println("Link Rigidity : "+rigidityValue);
						//slider set new link rigidity 
						model.setRigidity(rigidityValue);
				}
			}
		});
		Hashtable labelTable2 = new Hashtable();
		labelTable2.put( new Integer(50), new JLabel("Link Rigidity") );
		sliderLinkRigidity.setLabelTable( labelTable2 );
		sliderLinkRigidity.setMajorTickSpacing(10);
		sliderLinkRigidity.setMinorTickSpacing(1);   
		sliderLinkRigidity.setPaintLabels(true);  
		sliderLinkRigidity.setPaintTicks(true);
		this.add(sliderLinkRigidity);
		
		//Slider for link repulsion constant
		sliderRepulsionConstant = new JSlider(1,100);		
		sliderRepulsionConstant.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
						double repulsionConstant = (int)source.getValue();
						System.out.println("Repulsion Constant: "+(repulsionConstant/50));
						//slider set new repulsion constant
						model.setRepulsionConstant(repulsionConstant/50);
				}
			}
		});
		Hashtable labelTable3 = new Hashtable();
		labelTable3.put( new Integer(50), new JLabel("Repulsion Constant") );
		sliderRepulsionConstant.setLabelTable( labelTable3 );
		sliderRepulsionConstant.setMajorTickSpacing(10);
		sliderRepulsionConstant.setMinorTickSpacing(1);   
		sliderRepulsionConstant.setPaintLabels(true);  
		sliderRepulsionConstant.setPaintTicks(true);
		this.add(sliderRepulsionConstant);
		
		//Slider for lambda
		sliderLambda = new JSlider(1,10);		
		sliderLambda.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
						double lambda = (int)source.getValue();
						System.out.println("Lambda : "+lambda);
						//slider set new lambda
						model.setLambda(lambda);
				}
			}
		});	
		Hashtable labelTable4 = new Hashtable();
		labelTable4.put( new Integer(5), new JLabel("Lambda") );
		sliderLambda.setLabelTable( labelTable4 );
		sliderLambda.setMajorTickSpacing(10);
		sliderLambda.setMinorTickSpacing(1);   
		sliderLambda.setPaintLabels(true);  
		sliderLambda.setPaintTicks(true);
		this.add(sliderLambda);
		
		// on récupère l'instance du modèle (pattern singleton)
		this.model = SimulationModel.getInstance();

		// pour récupérer les mouvements de la souris:
		addMouseListener(this);
	}

	public void updateDisplay() {
		this.updateUI();
		this.setGrabbedNodePosition();
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// on caste l'objet Graphics en Graphics2D car plus de fonctionnalités
		Graphics2D g2d = (Graphics2D) g;
		// si on veut de l'antialiasing (ATTENTION ça fait ramer un max quand beaucoupd de noeuds)
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
			double diameter = convertNodeDiameterToPixel(node);
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

	@Override
	public void mouseClicked(MouseEvent e) {
		// on récupère la position du click, en pixels (relatif au panel)
		int x = e.getX();
		int y = e.getY();

		// on récupère le bouton qui a cliqué (1 = gauche, 2 = milieu, 3 = droit)
		int buttonClicked = e.getButton();
		if(buttonClicked == MouseEvent.BUTTON3) { // clic droit

			// on parcourt tous les noeuds pour trouver celui sur lequel on a cliqué
			for (Node node : model.getNodes()) {
				double X = convertNodePositionToPixelX(node);
				double Y = convertNodePositionToPixelY(node);
				double gap_x = X - x;
				double gap_y = Y - y;
				double distance = Math.sqrt(gap_x*gap_x + gap_y*gap_y);
				if (distance <= convertNodeDiameterToPixel(node)/2) {
					if (node.isHung() == false)
						node.hang();
					else
						node.unhang();
					break;
				}
			}
		}
	}
	public void mousePressed(MouseEvent e) {
		// on récupère la position à laquelle on a appuyé, en pixels (relatif au panel)
		int x = e.getX();
		int y = e.getY();

		// on récupère le bouton qui a été utilisé (1 = gauche, 2 = milieu, 3 = droit)
		int buttonPressed = e.getButton();
		if(buttonPressed == MouseEvent.BUTTON1) {  // clic gauche

			// on parcourt tous les noeuds pour trouver celui que l'on a attrapé ("grabbed")
			for (Node node : model.getNodes()) {
				double X = convertNodePositionToPixelX(node);
				double Y = convertNodePositionToPixelY(node);
				double gap_x = X - x;
				double gap_y = Y - y;
				double distance = Math.sqrt(gap_x*gap_x + gap_y*gap_y);
				if (distance <= convertNodeDiameterToPixel(node)/2) {
					// si le noeud est fixé, on ne fait rien
					if (node.isHung() == true)
						break;
					node.grab();
					this.grabbedNode = node;
					break;
				}
			}
		}
	}
	public void mouseReleased(MouseEvent e) {
		// on récupère le bouton qui a été utilisé (1 = gauche, 2 = milieu, 3 = droit)
		int buttonReleased = e.getButton();
		if(buttonReleased == MouseEvent.BUTTON1) {  // clic gauche

			// on libère le noeud qui était attrapé, s'il y en avait un
			if (this.grabbedNode != null) {
				this.grabbedNode.release();
				this.grabbedNode = null;
			}
		}
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	private void setGrabbedNodePosition() {
		if (this.grabbedNode == null) // if no node is grabbed
			return;

		// we get mouse position, and we set the node position to this position
		// good advice here: http://stackoverflow.com/questions/1439022/get-mouse-position
		Point pos = this.getMousePosition();

		// problem: if mouse is outside the panel, the method above return null
		// so in that case, we release the grabbed node
		if (pos == null) {
			this.grabbedNode.release();
			this.grabbedNode = null;
			return;
		}

		double x = convertPixelToNodePositionX((int)pos.getX());
		double y = convertPixelToNodePositionY((int)pos.getY());
		this.grabbedNode.setPosition(x,y);

	}
}
