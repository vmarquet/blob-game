package com.github.vmarquet.graph.controler;

import com.github.vmarquet.graph.model.*;
import com.github.vmarquet.graph.view.*;
import com.github.vmarquet.graph.physicalworld.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;
import org.jbox2d.collision.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.contacts.*;
import org.jbox2d.callbacks.*;
import java.awt.*;

import java.util.*; // pour les math

// this class is the heart of the physics simulation
// it's where all the calculation is made
// "implements runnable" allows to create a thread

public class SimulationControler implements Runnable, ContactListener {

	private final static int timeStep = 60;  // milliseconds
	// when using timeStep during equations, timeStep should be in seconds
	// so use (this.timeStep/1000.0)
	// WARNING: if you use 1000 and not 1000.0, the result is false

	private SimulationModel model = null;
	private SimulationView view = null;
	private PhysicalWorld world = null;

	public SimulationControler(SimulationView view) {
		// we get the instance of the model (singleton pattern)
		this.model = SimulationModel.getInstance();
		this.view = view;
		this.world = this.model.getPhysicalWorld();
		this.world.setContactListener(this);

		// we convert the timestep to seconds
		float timeStepJBox2d = (float)(this.timeStep)*0.001f;
		// we set the timestep for jBox2d
		this.world.setTimeStep(timeStep);
	}

	// threaded function, the heart of the physics simulation
	public void run() {
		try {
			// initialization
			boolean wantToQuit = false;

			boolean first = true;

			// main loop
			while ( ! wantToQuit ) {

				// on remet la somme des forces à zéro:
				for (Node node : model.getNodes()) {
					node.for_x = 0;
					node.for_y = 0;
				}

				// Force du ressort: f = -k(x-lrepos)
				// on parcourt la liste des liens
				for (Link link : model.getLinks()) {
					// on récupère les noeuds de chaque bout du lien
					Node node_start = link.getStartNode();
					Node node_end   = link.getEndNode();

					// on récupère la distance entre les deux noeuds:
					double distance = link.getDistance();

					// pour éviter de diviser par 0:
					if (distance <= 0)
						continue;

					// on en déduit la force:
					// négatif = ressort étiré, positif = ressort contracté
					double lrepos = link.getLength();
					double k = link.getRigidity();
					double f = -k*(distance-lrepos);
					link.applyForceBetweenNodes(f);
				}

				// Force de répulsion en 1/r avec r la distance entre les noeuds
				// on parcours tous les noeuds
				for (int i=0; i<model.getNumberOfNodes(); i++) {

					// on parcours tous les noeuds pas encore traités
					for (int j=i+1; j<model.getNumberOfNodes(); j++) {

						Node node1 = model.getNodeNumber(i);
						Node node2 = model.getNodeNumber(j);

						// on n'applique la force répulsive qu'aux noeuds non directement reliés par ressort et aux noeuds appartenant au même graphe
						if (model.isLinked(node1, node2) == true || node1.getGraphNumber() != node2.getGraphNumber())
							continue;

						// on récupère la distance entre les noeuds
						double distance = Link.getDistance(node1, node2);

						// on évite de diviser par 0:
						if (distance <= 0)
							continue;

						// norme de la force en 1/r
						double coef_repulsion = model.getCoefRepulsion();
						double f = coef_repulsion / distance;
						Link.applyForceBetweenNodes(f, node1, node2);
					}
				}

				// force de frottement fluide: f = -lambda*v
				double lambda = model.getLambda();
				for (Node node : model.getNodes()) {
					node.for_x += -lambda*node.vit_x;
					node.for_y += -lambda*node.vit_y;
				}

				// Accélération: m*a = somme(forces) => a = somme(forces)/m
				for (Node node : model.getNodes()) {
					double m = node.getMass();
					node.acc_x = node.for_x*(1/m);
					node.acc_y = node.for_y*(1/m);
				}

				// Pour calculer la vitesse et la position
				// on utilise la méthode de Newton

				// Vitesse: a = dv/dt => dv = a*dt
				for (Node node : model.getNodes()) {
					node.vit_x += node.acc_x*(this.timeStep/1000.0);
					node.vit_y += node.acc_y*(this.timeStep/1000.0);
				}

				// Position: v = dx/dt => dx = v*dt
				for (Node node : model.getNodes()) {
					if (node.isHung() == true || node.isGrabbed() == true)
						continue;
					node.pos_x += node.vit_x*(this.timeStep/1000.0);
					node.pos_y += node.vit_y*(this.timeStep/1000.0);
				}

				// afin que le graphe ne se déplace pas hors du champ de la fenêtre,
				// on fait en sorte que son barycentre soit toujours au milieu de la fenêtre

				// on calcule le barycentre des points
				// double moyenne_x = 0;
				// double moyenne_y = 0;
				// for (Node node : model.getNodes()) {
				// 	moyenne_x += node.pos_x;
				// 	moyenne_y += node.pos_y;
				// }
				// int n = model.getNumberOfNodes();
				// if (n != 0) {
				// 	moyenne_x /= n;
				// 	moyenne_y /= n;
				// }

				// on calcule l'écart par rapport au centre de la fenêtre
				// double ecart_x = 0.5 - moyenne_x;
				// double ecart_y = 0.5 - moyenne_y;

				// on déplace tous les noeuds
				// for (Node node : model.getNodes()) {
				// 	node.pos_x += ecart_x;
				// 	node.pos_y += ecart_y;
				// }

				// DEBUG:
				// for (Node node : model.getNodes()) {
				// 	System.out.println("-----------------" + i);
				// 	System.out.println("for_x: " + node.for_x);
				// 	System.out.println("for_y: " + node.for_y);
				// 	System.out.println("acc_x: " + node.acc_x);
				// 	System.out.println("acc_y: " + node.acc_y);
				// 	System.out.println("vit_x: " + node.vit_x);
				// 	System.out.println("vit_y: " + node.vit_y);
				// 	System.out.println("pos_x: " + node.pos_x);
				// 	System.out.println("pos_y: " + node.pos_y);
				// }

				view.updateDisplay();
				this.world.step();  // jBox2d mouvements
				Thread.sleep(timeStep);  // Synchronize the simulation with real time
			}
		}
		catch(InterruptedException ex) {
			System.err.println(ex.getMessage());
		}
		return;
	}


	//--- jBox2d events ---
	/* Event when object are touching */
	public void beginContact(Contact contact) {
		System.out.println("Objects are touching "+Sprite.extractSprite(contact.getFixtureA().getBody()).getName()
		+" "+Sprite.extractSprite(contact.getFixtureB().getBody()).getName() ); 
	}

	/* Event when object are leaving */
	public void endContact(Contact contact) {
		System.out.println("Objects are leaving "+Sprite.extractSprite(contact.getFixtureA().getBody()).getName() 
	    +" "+Sprite.extractSprite(contact.getFixtureB().getBody()).getName() ); 
	}
	/* unused advanced stuff */
	public void postSolve(Contact contact, ContactImpulse impulse) {
		System.out.println("Contact: postSove");
	}
	public void preSolve(Contact contact, Manifold oldManifold) {
		System.out.println("Contact: preSolve");
	}
}
