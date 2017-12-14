package particles;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ParticlesController {
	@FXML
	private Canvas canvas;

	@FXML
	private Label label;

	private ArrayList<Particle> particles = new ArrayList<>();

	private ExecutorService pool = Executors.newCachedThreadPool();

	/**
	 * retourne le canvas dans lequel les particules doivent Ãªtre dessinÃ©es
	 * 
	 * @return the canvas
	 */
	public Canvas getCanvas() {
		return this.canvas;
	}
	
	public ArrayList<Particle> getParticles() {
		return this.particles;
	}

	/**
	 * ajoute une nouvelle particule (addParticle) Ã la liste et met Ã jour le
	 * texte du label.
	 */
	@FXML
	public void onAdd() {
		/*
		Particle p = new Particle(this);
		this.particles.add(p);
		*/
		this.addParticle() ;
		this.label.setText(particles.size()+"");
	}

	/**
	 * ajoute une nouvelle particule Ã la liste et la soumet au pool
	 */
	public void addParticle() {

		Particle p = new Particle(this);
		this.particles.add(p);
		this.pool.submit(p);
	}

	/**
	 * retire une particule de la liste
	 * 
	 * @param p
	 *            particule Ã retirer
	 */
	public void removeParticle(Particle p) {
		this.particles.remove(p);
	}

	/**
	 * efface le canvas (dessin d'un rectangle noir) et dessine les particules
	 * de la liste (par appel de leur mÃ©thode draw).
	 */
	public void drawParticles() {
		Platform.runLater(new Runnable() {
			public void run() {
				ParticlesController.this.canvas.getGraphicsContext2D().setFill(Color.BLACK);
				ParticlesController.this.canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getHeight(), canvas.getWidth());
				synchronized(this){
					for (Particle particle : ParticlesController.this.particles) {
						particle.draw();
					}
				}
			}
		});
	}

}
