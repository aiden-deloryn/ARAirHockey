import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Particle {

	private int x;
	private int y;
	private float dx;
	private float dy;
	private float size;
	private Color color;

	public Particle(int x, int y, float dx2, float dy2, float size, Color c) {
		this.x = x;
		this.y = y;
		this.dx = dx2;
		this.dy = dy2;
		this.size = size;
		this.color = c;
	}

	public boolean update(float delta) {
		x += dx * delta;
		y += dy * delta;
		size -= (delta / 50);
		if (size <= 0)
			return true;
		return false;
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		g2d.setColor(color);
		int drawSize = (int) size;
		g2d.fillRect(x - (drawSize / 2), y - (drawSize / 2), drawSize, drawSize);

		g2d.dispose();
	}

}