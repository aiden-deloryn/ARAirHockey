import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;

public class Puck {
	public float radius;
	public float x, y;
	public float spawnX, spawnY;
	public float xMov, yMov;
	public Shape shape;
	float oldX, oldY;
	public Color color;
	public Image img;
	public int lastHitIndex;
	public int index;
	public int colliderCount;

	public Puck(float x, float y, float radius, String src, Color color) {
		this.x = x;
		this.y = y;
		spawnX = x;
		spawnY = y;
		this.radius = radius;
		try {
			img = new ImageIcon(src).getImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.color = color;
		reset();
	}

	public void update(float delta) {
		savePos();
		x += xMov * delta;
		y += yMov * delta;
		updateShape();
		checkCol();

		// DECEL:
		xMov *= 0.999f;
		yMov *= 0.999f;
	}

	public void updateShape() {
		shape = new Ellipse2D.Double(x - radius, y - radius, 2.0 * radius,
				2.0 * radius);
	}

	public void reset() {
		lastHitIndex = 0;
		x = spawnX;
		y = spawnY;
		xMov = 0;
		yMov = 0;
		updateShape();
	}

	public void checkCol() {
		if (this.x <= 0 || this.x >= Game.gameWidth) {
			xMov *= -1;
			x = oldX;
			Sound.play("HitSound", false);
		}
		if (this.y <= 0 || this.y >= Game.gameHeight) {
			yMov *= -1;
			y = oldY;
			Sound.play("HitSound", false);
		}
	}

	public void getPushed(Player other) {
		savePos();
		xMov = other.xMov * 2;
		yMov = other.yMov * 2;
		
		if (other.activeDebuf == Debuf.INVERSION) {
			xMov *= -1;
			yMov *= -1;
		}
	}

	public boolean hasCollision(Puck other) {
		double xDiff = x - other.x;
		double yDiff = y - other.y;
		double distance = Math.sqrt((Math.pow(xDiff, 2) + Math.pow(yDiff, 2)));
		boolean coll = distance < (radius + other.radius);
		if (coll)
			other.lastHitIndex = index;
		return coll;
	}

	public void render(Graphics2D g) {
		g.setColor(color);
		g.drawImage(img, (int) (x - (radius / 2)), (int) (y - (radius / 2)),
				(int) radius * 2, (int) radius * 2, null);
	}

	public void savePos() {
		oldX = x;
		oldY = y;
	}
}
