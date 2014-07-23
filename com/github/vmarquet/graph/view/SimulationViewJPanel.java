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
import java.lang.*;
import java.awt.geom.Path2D;


public class SimulationViewJPanel extends JPanel implements SimulationView, MouseListener {

      
    private SimulationModel model = null;

	private double margin_x;
	private double margin_y;
	private double min_size;

	private Node grabbedNode = null; // link to the node that is grabbed with the mouse
	
	private boolean displayNumbers = true, displayNodes = true, displayShape = false; //booleans for the display (checkboxes) 


	public SimulationViewJPanel() {
		
		//Slider for link length, link rigidity, repulsion constant, lambda and node mass
		JSlider sliderLinkLength, sliderLinkRigidity, sliderRepulsionConstant, sliderLambda, sliderNodeMass;
        
        //Checkboxes for displaying numbers, nodes and/or shape
        final JCheckBox numberButton, nodesButton, shapeButton;
        
        
        //Slider for link length
		sliderLinkLength = new JSlider(0,25);
		sliderLinkLength.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
						double lengthValue = (int)source.getValue();
						System.out.println("Link Length : "+(lengthValue/20));
						//slider set new link length
						for (Link link : model.getLinks()) {
							link.setLength(lengthValue/20);
						}	
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
						for (Link link : model.getLinks()) {
							link.setRigidity(rigidityValue);
						}
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
		
		//Slider for node mass
		sliderNodeMass = new JSlider(10,110);		
		sliderNodeMass.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
						double nodeMass = (int)source.getValue();
						System.out.println("Node Mass : "+(nodeMass/10));
						//slider set new node mass
						for (Node node : model.getNodes()) {
							node.setMass(nodeMass/10);
						}
				}
			}
		});	
		Hashtable labelTable5 = new Hashtable();
		labelTable5.put( new Integer(60), new JLabel("Node Mass") );
		sliderNodeMass.setLabelTable( labelTable5 );
		sliderNodeMass.setMajorTickSpacing(100);
		sliderNodeMass.setMinorTickSpacing(1);   
		sliderNodeMass.setPaintLabels(true);
		sliderNodeMass.setPaintTicks(true);
		this.add(sliderNodeMass);
		
		//checkbox for numbers
		numberButton = new JCheckBox("Display Numbers");
        numberButton.setSelected(true);
		numberButton.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				displayNumbers = false;
				
				Object source = e.getItemSelectable();

				if (source == numberButton) {
				    displayNumbers = true;

					if (e.getStateChange() == ItemEvent.DESELECTED) {
						displayNumbers = false;
					}
   				}
   				
   				System.out.println("Display Numbers : "+displayNumbers);
   			}
		});
		this.add(numberButton);
		
		//checkbox for nodes
		nodesButton = new JCheckBox("Display Nodes");
        nodesButton.setSelected(true);
		nodesButton.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				displayNodes = false;
				
				Object source = e.getItemSelectable();

				if (source == nodesButton) {
				    displayNodes = true;

					if (e.getStateChange() == ItemEvent.DESELECTED) {
						displayNodes = false;
					}
   				}
   				
   				System.out.println("Display Nodes : "+displayNodes);
   			}
		});
		this.add(nodesButton);
		
		//checkbox for shape
		shapeButton = new JCheckBox("Display Shape");
        shapeButton.setSelected(false);
		shapeButton.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				displayShape = false;
				
				Object source = e.getItemSelectable();

				if (source == shapeButton) {
				    displayShape = true;

					if (e.getStateChange() == ItemEvent.DESELECTED) {
						displayShape = false;
					}
   				}
   				
   				System.out.println("Display Shape : "+displayShape);
   			}
		});
		this.add(shapeButton);
		
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

		//check if there is a possibility to link the graphs (x2)
		if(isFusionNodeIandNodeJ(0, 5)==true){
			System.out.println("0 et 5 s'embrassent, ça a donné des idées à 4 et 6");
			Link link = new Link(model.getNodeNumber(4), model.getNodeNumber(6));
			link.setLength(0.025);
			this.model.addLink(link);
		}
		if(isFusionNodeIandNodeJ(4, 6)==true){
			System.out.println("4 et 6 s'embrassent, ça a donné des idées à 0 et 5");
			Link link = new Link(model.getNodeNumber(0), model.getNodeNumber(5));
			link.setLength(0.025);
			this.model.addLink(link);
		}

		// we paint the objects
		if(displayShape == true) paintShape(g2d);
		
		if(displayShape == true && model.isLinked(model.getNodeNumber(4), model.getNodeNumber(6))) paintShape2(g2d);
		
		paintLinks(g2d);
		if(displayNodes == true) paintNodes(g2d);
		if(displayNumbers == true) paintNumbers(g2d);
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
		}

	}
	
	private void paintNumbers(Graphics2D g) {
	
		for (Node node : model.getNodes()) {
			
			// we draw the text (node number / ...)
			g.setColor(Color.BLACK);
			g.drawString(Integer.toString(node.getNodeNumber()),
			             (int)(convertNodePositionToPixelX(node) -5),
			             (int)(convertNodePositionToPixelY(node) +4));
		}

	}
	
	private void paintShape(Graphics2D g){
		//TODO : modify this shit
		
		//we make a path to form the pentagon and we fill it
		Path2D.Double path = new Path2D.Double();
		path.moveTo(convertNodePositionToPixelX(model.getNodeNumber(0)), convertNodePositionToPixelY(model.getNodeNumber(0)));
		path.lineTo(convertNodePositionToPixelX(model.getNodeNumber(1)), convertNodePositionToPixelY(model.getNodeNumber(1)));
		path.lineTo(convertNodePositionToPixelX(model.getNodeNumber(2)), convertNodePositionToPixelY(model.getNodeNumber(2)));
		path.lineTo(convertNodePositionToPixelX(model.getNodeNumber(3)), convertNodePositionToPixelY(model.getNodeNumber(3)));
		path.lineTo(convertNodePositionToPixelX(model.getNodeNumber(4)), convertNodePositionToPixelY(model.getNodeNumber(4)));
		path.closePath();
		g.setColor(Color.WHITE);
		g.fill(path);
		path.moveTo(convertNodePositionToPixelX(model.getNodeNumber(5)), convertNodePositionToPixelY(model.getNodeNumber(5)));
		path.lineTo(convertNodePositionToPixelX(model.getNodeNumber(6)), convertNodePositionToPixelY(model.getNodeNumber(6)));
		path.lineTo(convertNodePositionToPixelX(model.getNodeNumber(7)), convertNodePositionToPixelY(model.getNodeNumber(7)));
		path.closePath();
		g.setColor(Color.WHITE);
		g.fill(path);
	}
	
	private void paintShape2(Graphics2D g){
		//TODO : modify this shit
		
		Path2D.Double path = new Path2D.Double();
		path.moveTo(convertNodePositionToPixelX(model.getNodeNumber(5)), convertNodePositionToPixelY(model.getNodeNumber(5)));
		path.lineTo(convertNodePositionToPixelX(model.getNodeNumber(0)), convertNodePositionToPixelY(model.getNodeNumber(0)));
		path.lineTo(convertNodePositionToPixelX(model.getNodeNumber(4)), convertNodePositionToPixelY(model.getNodeNumber(4)));
		path.lineTo(convertNodePositionToPixelX(model.getNodeNumber(6)), convertNodePositionToPixelY(model.getNodeNumber(6)));
		path.closePath();
		g.setColor(Color.WHITE);
		g.fill(path);
	}
	
	private void paintLinks(Graphics2D g) {
	
		for (Link link : model.getLinks()) {
			Color color = link.getColor();
			g.setColor(Color.WHITE);
			Node node1 = link.getStartNode();
			Node node2 = link.getEndNode();
			g.setStroke(new BasicStroke(2));
			g.drawLine((int)(convertNodePositionToPixelX(node1)), 
			           (int)(convertNodePositionToPixelY(node1)), 
			           (int)(convertNodePositionToPixelX(node2)), 
			           (int)(convertNodePositionToPixelY(node2)));
		}
	}
	
	private boolean isFusionNodeIandNodeJ(int i, int j) {
		//if the two nodes i and j are close to eachother they will be linked
		if(model.isLinked(model.getNodeNumber(i), model.getNodeNumber(j)) == false){
			double distX = convertNodePositionToPixelX(model.getNodeNumber(i)) - convertNodePositionToPixelX(model.getNodeNumber(j));
			double distY = convertNodePositionToPixelY(model.getNodeNumber(i)) - convertNodePositionToPixelY(model.getNodeNumber(j));
			// Get distance with Pythagoras
			double dist = Math.sqrt((distX * distX) + (distY * distY));
			if(dist <= (convertNodeDiameterToPixel(model.getNodeNumber(i))/0.8 + convertNodeDiameterToPixel(model.getNodeNumber(j))/0.8)){
	
				Link link = new Link(model.getNodeNumber(i), model.getNodeNumber(j));
				link.setLength(0.025);
				this.model.addLink(link);
		
				//the two graphs have to be only one graph
				int graphToKeep = model.getNodeNumber(i).getGraphNumber();
				int graphToErase = model.getNodeNumber(j).getGraphNumber();
				for (Node node : model.getNodes()){
					if(node.getGraphNumber() == graphToErase){
						node.setGraphNumber(graphToKeep);
					}
				}
				return true;
			}
		}
		return false;
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
