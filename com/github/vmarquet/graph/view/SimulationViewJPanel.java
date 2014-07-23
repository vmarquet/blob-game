package com.github.vmarquet.graph.view;

import com.github.vmarquet.graph.model.*;
import com.github.vmarquet.graph.physicalworld.*;
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
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.collision.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.contacts.*;
import org.jbox2d.callbacks.*;
import java.awt.image.BufferedImage;


public class SimulationViewJPanel extends JPanel implements SimulationView, MouseListener {

      
    private SimulationModel model = null;
   private float scale;
   private PhysicalWorld world;
   private Color backgroundColor;
   private ImageIcon backgroundIcon;
   private Vec2 cameraPosition;


	private double margin_x;
	private double margin_y;
	private double min_size;

	private Node grabbedNode = null; // link to the node that is grabbed with the mouse
	
	private boolean displayNumbers = true, displayNodes = true, displayShape = false; //booleans for the display (checkboxes) 


	public SimulationViewJPanel(PhysicalWorld world, Dimension dimension, float scale) {

		// on récupère l'instance du modèle (pattern singleton)
		this.model = SimulationModel.getInstance();

		// pour récupérer les mouvements de la souris:
		addMouseListener(this);

      this.world = world;
      this.scale = scale;
      this.backgroundColor = null;
      this.backgroundIcon = null;
      this.setPreferredSize(dimension);
      // The cameraPosition in the simulation referential
      this.cameraPosition = new Vec2(0,0);
	}

	public void updateDisplay() {
		this.updateUI();
		this.setGrabbedNodePosition();
		this.setPhysicalPosition();
	}
	@Override
	public void paintComponent(Graphics g) {
		// super.paintComponent(g);

		// // on caste l'objet Graphics en Graphics2D car plus de fonctionnalités
		// Graphics2D g2d = (Graphics2D) g;
		// // si on veut de l'antialiasing (ATTENTION ça fait ramer un max quand beaucoupd de noeuds)
		// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// // we clear the background
		// Color backgroundColor = Color.decode("#000000");
		// this.setBackground(backgroundColor);

		// // we compute the ratio for the display
		// computeMargin(g2d);

		// //check if there is a possibility to link two nodes (x2)
		// connectNodeIandNodeJ(0, 5);
		// connectNodeIandNodeJ(4, 6);

		// // we paint the objects
		// if(displayShape == true) paintShape(g2d);
		// paintLinks(g2d);
		// if(displayNodes == true) paintNodes(g2d);
		// if(displayNumbers == true) paintNumbers(g2d);

		/* Painting the whole world in the buffer image */
     	  
      	 // The buffer is an image containing the painting of the whole world
        // The painting of the SimulationViewJBox2D will be a crop of this image, centered around the camera
        BufferedImage buffer = new BufferedImage(toScale(world.getWidth()), toScale(world.getHeight()), BufferedImage.TYPE_INT_RGB);
     	  
     	  // Get the Graphics context from the image (different from the Graphics context from the JPanel)
     	  Graphics imageGraphics = buffer.getGraphics();
     	  // Clear the image
        imageGraphics.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
        // Fill background with color
        if(backgroundColor!=null) {
        	imageGraphics.setColor(backgroundColor);
        	imageGraphics.fillRect(0,0, buffer.getWidth(), buffer.getHeight());
        }
        // Paint the background image (the image is scaled to fit the PhysicalWorld dimension)
        if(backgroundIcon != null) {
        	Sprite.rotatedPaint(imageGraphics, backgroundIcon, 0, 0 , ((float)buffer.getWidth())/backgroundIcon.getIconWidth(), ((float)buffer.getHeight())/backgroundIcon.getIconHeight(), 0, 0, 0);
        }
        // If the SimulationViewJBox2D is linked to a PhysicalWorld
        if(world != null) {
        	 world.paint(imageGraphics, this);
        	 // world.paint appelle Sprite.paint() sur tous les sprites du tableau de sprites
        }
        
        /* Painting the JPanel as a crop from the buffer image */
        // Clear the JPanel
        g.clearRect(0, 0, getWidth(), getHeight());
        // Get the camera's coordinate in JPanel referential
        Point cam = convert4draw(cameraPosition);
        // Center the JPanel on the camera and print the buffer image in the JPanel
        g.drawImage(buffer, this.getWidth()/2 - cam.x, this.getHeight()/2 -cam.y , null);
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
	
	private void connectNodeIandNodeJ(int i, int j) {
		//if the two nodes i and j are close to eachother they will be linked
		if(model.isLinked(model.getNodeNumber(i), model.getNodeNumber(j)) == false){
			double distX = convertNodePositionToPixelX(model.getNodeNumber(i)) - convertNodePositionToPixelX(model.getNodeNumber(j));
			double distY = convertNodePositionToPixelY(model.getNodeNumber(i)) - convertNodePositionToPixelY(model.getNodeNumber(j));
			// Get distance with Pythagoras
			double dist = Math.sqrt((distX * distX) + (distY * distY));
			if(dist <= (convertNodeDiameterToPixel(model.getNodeNumber(i))/0.6 + convertNodeDiameterToPixel(model.getNodeNumber(j))/0.6)){
	
				Link link = new Link(model.getNodeNumber(i), model.getNodeNumber(j));
				this.model.addLink(link);
		
				//the two graphs are now the same
				for (Node node : model.getNodes()){
		
					if(node.getGraphNumber() == model.getNodeNumber(j).getGraphNumber()){
						node.setGraphNumber(model.getNodeNumber(i).getGraphNumber());
					}
				}
			}
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


	    /**
     * Set the camera position (in the simulation referential)
     * @param cameraPosition the cameraPosition (in the simulation referential)
     */
    public void setCameraPosition(Vec2 cameraPosition) {
    	this.cameraPosition.set(cameraPosition);
    }
    
    /**
     * Set the background color
     * @param backgroundColor the new Color for the background
     */
    public void setBackGroundColor(Color backgroundColor) {
    	this.backgroundColor = backgroundColor;
    }
    
    /**
     * Set the background image
     * @param backgroundIcon the new ImageIcon for the background
     */
    public void setBackGroundIcon(ImageIcon backgroundIcon) {
    	this.backgroundIcon = backgroundIcon;
    }
    
    /**
     * Convert a simulation's size into pixel'size
     * @param value the value in the simulation
     * @return the value in pixel
     */
    public int toScale(float value) {
    	return Math.round(value *scale);
    }
    
    /**
     * Convert simulation coordinate (Origin centered, Positive ordinate up) into JPanel coordinate (Top-left origin, Positive ordinate down)
     * @param v a Vec2 vector coordinate in simulation referential
     * @return a Point vector in JPanel referential
     */
    public Point convert4draw(Vec2 v) { // Change orientation of the referentiel and put to scale
    	return  new Point(toScale(v.x - world.getXMin()), toScale(world.getYMax() - (v.y)));
    }

    private void setPhysicalPosition() {
    	for (Node node : model.getNodes()) {
    		Body body = node.getBody();
    		body.setTransform(new Vec2(15,15), 0);
    	}
    }

}
