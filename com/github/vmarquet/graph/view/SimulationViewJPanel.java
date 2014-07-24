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
	
	private boolean displayNumbers = false, displayNodes = false, displayShape = true; //booleans for the display (checkboxes) 
	
	private double zoomValue = 5.0; //valeur de l'échelle du zoom (1:1 --> 1:10)


	public SimulationViewJPanel() {
		
		//Button to center all nodes
		JButton centerButton;
		
		//Slider for zoom, link length, link rigidity, repulsion constant, lambda and node mass
		JSlider sliderZoom, sliderLinkLength, sliderLinkRigidity, sliderRepulsionConstant, sliderLambda, sliderNodeMass;
        
        //Checkboxes for displaying numbers, nodes and/or shape
        final JCheckBox numberButton, nodesButton, shapeButton;
        
        
        //Button to center all nodes
        centerButton = new JButton("Sylvester Stallone CENTER");
        centerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	for (Node node : model.getNodes()) {
		    		//TODO : PIMP THAT SHIT
					node.setPosition(convertPixelToNodePositionX((int)(withDezoomX(getWidth()/2))), convertPixelToNodePositionX((int)(withDezoomY(getHeight()/2))));
				}
		    }          
     	});
        this.add(centerButton); 
        
        //Slider for zoom
		sliderZoom = new JSlider(1,100);
		sliderZoom.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				
						double zoomValue = (int)source.getValue();
						System.out.println("Zoom : échelle 1:"+zoomValue/10);
						//slider set new zoom
						setZoomValue(zoomValue/10);
			}
		});
		Hashtable labelTable0 = new Hashtable();
		labelTable0.put( new Integer(50), new JLabel("Zoom") );
		sliderZoom.setLabelTable( labelTable0 );
		sliderZoom.setMajorTickSpacing(100); 
		sliderZoom.setMinorTickSpacing(1);
		sliderZoom.setValue(50); 
		sliderZoom.setPaintLabels(true);
		sliderZoom.setPaintTicks(true);
		this.add(sliderZoom);
		
        
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

		// check if there is a possibility to link the graphs
		// for each combinaison of nodes 
		for (Node i : model.getNodes()){
			for (Node j : model.getNodes()){
				//if they don't belong to the same graph
				if(i.getGraphNumber() != j.getGraphNumber()) {
					double distX = convertNodePositionToPixelX(i) - convertNodePositionToPixelX(j);
					double distY = convertNodePositionToPixelY(i) - convertNodePositionToPixelY(j);
					// Get distance with Pythagoras
					double dist = Math.sqrt((distX * distX) + (distY * distY));
					// if these links are close enougth
					if( dist <= (convertNodeDiameterToPixel(i)/0.8 + convertNodeDiameterToPixel(j)/0.8) ){
						// we get 2 nodes i_ and j_ that are close from each graph (linked to the i and j nodes)
						int i_Number=-1, j_Number=-1;
						// getting a node i_ near i
						for (Node i_ : model.getNodes()){	
							if(model.isLinked(i_, i) == true){
								i_Number = i_.getNodeNumber();
							}
						}
						// getting a node j_ near j
						for (Node j_ : model.getNodes()){
							
							if(model.isLinked(j_, j) == true){
								j_Number = j_.getNodeNumber();
							}
						}
					
						// we link i j and i_ j_ with a special link
						Link link1 = new Link(i.getNodeNumber(), j.getNodeNumber());
						link1.setLength(0.0025);
						link1.setRigidity(80.0);
						this.model.addLink(link1);
						Link link2 = new Link(i_Number, j_Number);
						link2.setLength(0.0025);
						link2.setRigidity(80.0);
						this.model.addLink(link2);
						
						//the two graphs have to be only one graph
						int graphToKeep = i.getGraphNumber();
						int graphToErase = j.getGraphNumber();
						for (Node node : model.getNodes()){
							if(node.getGraphNumber() == graphToErase){
								node.setGraphNumber(graphToKeep);
							}
						}
					}
				}
			}
		}
		
		int [] pentagon = {0,1,2,3,4};
		int [] triangle = {5,6,7};
		int [] square = {8,9,10,11};
		int [] shapePart1 = {12,13,14};
		int [] shapePart2 = {12,14,15};
		int [] shapePart3 = {12,15,16,17};

		// we paint the objects
		if(displayShape == true) {
			paintShape(g2d,pentagon);
			paintShape(g2d,triangle);
			paintShape(g2d,square);
			paintShape(g2d,shapePart1);
			paintShape(g2d,shapePart2);
			paintShape(g2d,shapePart3);
		}
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
			g.fillOval((int)(withZoomX(convertNodePositionToPixelX(node))-diameter/2), 
			           (int)(withZoomY(convertNodePositionToPixelY(node))-diameter/2), 
			           (int)diameter, (int)diameter);
		}

	}
	
	private void paintNumbers(Graphics2D g) {
	
		for (Node node : model.getNodes()) {
			// we draw the text (node number / ...)
			g.setColor(Color.BLACK);
			g.drawString(Integer.toString(node.getNodeNumber()),
			             (int)(withZoomX(convertNodePositionToPixelX(node)) -5),
			             (int)(withZoomY(convertNodePositionToPixelY(node)) +4));
		}

	}
	
	private void paintShape(Graphics2D g, int[] nodeShape){
		//TODO : modify this shit
		
		//we make a path to form the pentagon and we fill it
		Path2D.Double path = new Path2D.Double();
		path.moveTo(withZoomX(convertNodePositionToPixelX(model.getNodeNumber(nodeShape[0]))), withZoomY(convertNodePositionToPixelY(model.getNodeNumber(nodeShape[0]))));
		for (int i = 0; i < nodeShape.length ; i++)
			path.lineTo(withZoomX(convertNodePositionToPixelX(model.getNodeNumber(nodeShape[i]))), withZoomY(convertNodePositionToPixelY(model.getNodeNumber(nodeShape[i]))));		
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
			
			g.drawLine((int)(withZoomX(convertNodePositionToPixelX(node1))),
			           (int)(withZoomY(convertNodePositionToPixelY(node1))),
			           (int)(withZoomX(convertNodePositionToPixelX(node2))),
			           (int)(withZoomY(convertNodePositionToPixelY(node2))));
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
	private double withZoomX(double pixel){
		int width = this.getWidth();
		return ((pixel + width/2)/this.getZoomValue());
	}
	private double withZoomY(double pixel){
		int height = this.getHeight();
		return ((pixel + height/2)/this.getZoomValue());
	}
	private double withDezoomX(double pixel){
		int width = this.getWidth();
		return (this.getZoomValue()*pixel-width/2);
	}
	private double withDezoomY(double pixel){
		int height = this.getHeight();
		return (this.getZoomValue()*pixel-height/2);
	}
	
	//setters:
	private void setZoomValue(double zoomValue){
		this.zoomValue = zoomValue;
	}
	//getters:
	private double getZoomValue(){
		return this.zoomValue;
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
				double X = withZoomX(convertNodePositionToPixelX(node));
				double Y = withZoomY(convertNodePositionToPixelY(node));
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
				double X = withZoomX(convertNodePositionToPixelX(node));
				double Y = withZoomY(convertNodePositionToPixelY(node));
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

		double x = convertPixelToNodePositionX((int)(withDezoomX(pos.getX())));
		double y = convertPixelToNodePositionY((int)(withDezoomY(pos.getY())));
		this.grabbedNode.setPosition(x,y);

	}
}
