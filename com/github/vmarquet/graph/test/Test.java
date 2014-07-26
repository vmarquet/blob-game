package com.github.vmarquet.graph.test;

import com.github.vmarquet.graph.model.SimulationModel;
import com.github.vmarquet.graph.model.Node;
import com.github.vmarquet.graph.model.Link;
import com.github.vmarquet.graph.view.SimulationView;
import com.github.vmarquet.graph.view.SimulationViewJPanel;
import com.github.vmarquet.graph.controler.GraphReaderFromFile;
import com.github.vmarquet.graph.controler.SimulationControler;
import com.github.vmarquet.graph.physicalworld.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.collision.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.contacts.*;
import org.jbox2d.callbacks.*;
import javax.swing.JFrame;
import java.lang.Thread;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.Color;

public class Test {

	public static void main(String[] args) {
		
		// on remplit le modèle
		GraphReaderFromFile reader = new GraphReaderFromFile("graphs/graph03.txt");

		SimulationModel model = SimulationModel.getInstance();
		model.print();

		// on crée une vue pour la simulation

		// we get the size of jBox2d world
		int width  = (int)model.getPhysicalWorld().getWidth();
		int height = (int)model.getPhysicalWorld().getHeight();

		// we create the panel
		SimulationViewJPanel panel = new SimulationViewJPanel(model.getPhysicalWorld(), new Dimension(width,height),1f);

		// we create a window and we put the panel in it
		JFrame fen = new JFrame();
		fen.setSize(width,height);
		//fen.setResizable(false);
		fen.add(panel);
		fen.setVisible(true);
		fen.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		panel.requestFocus();
		panel.setCameraPosition(new Vec2(width/2,height/2));  // jbox2d

		// on lance la simulation
		Thread thread = new Thread(new SimulationControler(panel));
		thread.start();

		try {
			Body body = model.getPhysicalWorld().addCircularObject(10f, BodyType.DYNAMIC, new Vec2(100,100), 0, 
					          new Sprite("TEST", 1, Color.WHITE, null));
			body.applyForceToCenter(new Vec2(100,100));
			body.getFixtureList().setSensor(false);
			body.setLinearVelocity(new Vec2(5000,0));
			Body body2 = model.getPhysicalWorld().addCircularObject(10f, BodyType.DYNAMIC, new Vec2(200,100), 0, 
					          new Sprite("TEST2", 1, Color.WHITE, null));
		} catch (InvalidSpriteNameException e) {
			e.printStackTrace();
		}

		return;
	}

}
