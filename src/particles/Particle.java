/**
 * 
 */
package particles;

import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Particle implements Runnable {

	private ParticlesController controller;

	// taille (largeur et hauteur) de la particule
	private static final double SIZE = 10.0;

	// position de la particule (initialement : centre du panneau)
	private double x, y;

	// couleur de la particule (alÃ©atoire)
	private Color color;

	// vitesse de la particule (alÃ©atoire, entre -SIZE et SIZE)
	private double vx, vy;

	/**
	 * constructeur
	 * 
	 * @param controller
	 *            contrÃ´leur de l'application
	 */
	public Particle(ParticlesController controller) {
		Random rand = new Random();
		this.controller = controller;
		this.x = this.controller.getCanvas().getHeight() / 2;
		this.y = this.controller.getCanvas().getWidth() / 2;
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		this.color = new Color(r, g, b, 1);
		this.vx = rand.nextDouble() * (this.SIZE * 2) - this.SIZE;
		this.vy = rand.nextDouble() * (this.SIZE * 2) - this.SIZE;
	}

	/**
	 * dessine la particule sur le Canvas du contrÃ´leur. Cette mÃ©thode sera
	 * invoquÃ©eÃ©e par la mÃ©thode drawParticles du contrÃ´leur
	 */
	public synchronized void draw() {
		this.controller.getCanvas().getGraphicsContext2D().setFill(this.color);
		this.controller.getCanvas().getGraphicsContext2D().fillOval(this.x, this.y, this.SIZE, this.SIZE);
		/*
		this.controller.getCanvas().getGraphicsContext2D().setFill(color.WHITE);
		this.controller.getCanvas().getGraphicsContext2D().fillOval(this.x - 1, this.y - 1, this.SIZE * 1.2,
				this.SIZE * 1.2);
				*/

	}

	/**
	 * met Ã jour la position de la particule
	 */
	public synchronized void update() {
		this.x = this.x + this.vx;
		this.y = this.y + this.vy;
		for(Particle particule : controller.getParticles()){
			this.collisionTest(particule);
		}
	}

	/**
	 * retourne la visibilitÃ© de la particule (selon sa position, sa taille et
	 * celle du canvas)
	 * 
	 * @return true si la particule est visible, false sinon
	 */
	public boolean isVisible() {
		boolean b = true;
		if (((this.x - this.SIZE / 2 > this.controller.getCanvas().getHeight()-(this.SIZE+5)) || this.x - this.SIZE / 2 <= 0)
				|| ((this.y - this.SIZE / 2 > this.controller.getCanvas().getWidth()-(this.SIZE+5) )
						|| (this.y - this.SIZE / 2 <= 0))) {
			b = false;
		}

		return b;
	}
	
	
	 /**
     * effectue un test de collision entre la particule courante et
     * la particule p. S'il y a collision, les vitesses des particules sont
     * recalculées en fonction des vitesses initiales.
     * @param p particule à tester
     */
    private void collisionTest(Particle p) {
        if (p == this) {
            return;
        }
        double nx = x - p.x;
        double ny = y - p.y;
        double d = Math.sqrt(nx * nx + ny * ny);
        if (d <= SIZE && d > 0.0001) {
            nx = nx * (SIZE - d) / d;
            ny = ny * (SIZE - d) / d;
            x += nx * 0.5;
            y += ny * 0.5;
            p.x -= nx * 0.5;
            p.y -= ny * 0.5;
            d = Math.sqrt(nx * nx + ny * ny);
            nx /= d;
            ny /= d;
            double vn = (vx - p.vx) * nx + (vy - p.vy) * ny;
            if (vn > 0.0) {
                return;
            }
            vn *= -0.9;
            nx *= vn;
            ny *= vn;
            vx += nx;
            vy += ny;
            p.vx -= nx;
            p.vy -= ny;
        }
    }
	
	
	

	/**
	 * animation de la particule :
	 *
	 * Tant que la particule est visible - mise Ã jour de la particule (mÃ©thode
	 * update) - dÃ©clencher le dessin de toutes les particules - "dormir"
	 * pendant 25 ms (Thread.sleep)
	 *
	 * Lorsque la particule n'est plus visible : - retirer la particule du
	 * contrÃ´leur - ajouter une nouvelle particule au contrÃ´leur
	 */
	public void run() {
		while (this.isVisible()) {
			this.update();
			controller.drawParticles();
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(this.x > this.controller.getCanvas().getWidth()-(this.SIZE+5) || this.x <= 0)
			this.vx = -vx;
		if(this.y > this.controller.getCanvas().getWidth()-(this.SIZE+5) || this.y <= 0)
			this.vy = -vy;
		if(!this.isVisible()){
			this.update();
			this.run();
		}
	}
}
