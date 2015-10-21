import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import javax.swing.ImageIcon;

public class Player extends Puck {
	public String id;
	public Rectangle2D goal;
	public Image gImg;
	public int score;
	public String colorName;
	public Debuf activeDebuf = Debuf.INVERSION;

	public Player(float x, float y, int radius, String id, String src,
			Color color, String gSrc) {
		super(x, y, radius, src, color);
		System.out.println("NEW PLAYER ADDED: " + id);
		this.id = id;
		try {
			gImg = new ImageIcon(gSrc).getImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setup() {
		if (index == 0) {
			goal = new Rectangle2D.Float(0, (Game.gameHeight / 2)
					- (Game.goalLength / 2), Game.goalWidth, Game.goalLength);
		} else if (index == 1) {
			goal = new Rectangle2D.Float(Game.gameWidth - Game.goalWidth,
					(Game.gameHeight / 2) - (Game.goalLength / 2),
					Game.goalWidth, Game.goalLength);
		} else if (index == 2) {
			goal = new Rectangle2D.Float((Game.gameWidth / 2)
					- (Game.goalLength / 2), 0, Game.goalLength, Game.goalWidth);
		} else {
			goal = new Rectangle2D.Float((Game.gameWidth / 2)
					- (Game.goalLength / 2), Game.gameHeight - Game.goalWidth,
					Game.goalLength, Game.goalWidth);
		}
		if (index == 0) {
			color = Color.BLUE;
			colorName = "Blue";
		} else if (index == 1) {
			color = Color.RED;
			colorName = "Red";
		} else if (index == 2) {
			color = Color.GREEN;
			colorName = "Green";
		} else {
			color = Color.YELLOW;
			colorName = "Yellow";
		}
	}

	@Override
	public void update(float delta) {
		float oldX = x;
		float oldY = y;
		
		switch(activeDebuf) {
		case INVERSION:
			this.x += (xMov * -1) * delta;
			this.y += (yMov * -1) * delta;
			break;
		case SPEED_INHIBITOR:
			this.x += (xMov * 0.3) * delta;
			this.y += (yMov * 0.3) * delta;
			break;
		case DISRUPTION:
			Random rand = new Random();
			int min = 0;
			int max = 8;
			int randomNum = rand.nextInt((max - min) + 1) + min;
			
			if (randomNum == 0) {
				// cause disruption
				this.x += (yMov * 3) * delta;
				this.y += (xMov * 3) * delta;
			} else {
				// don't cause disruption
				this.x += xMov * delta;
				this.y += yMov * delta;
			}
			
			break;
		default:
			this.x += xMov * delta;
			this.y += yMov * delta;
		}
		
		// COLLISION CHECKING:
		if (this.x <= 0 || this.x >= Game.gameWidth) {
			x = oldX;
		}
		if (this.y <= 0 || this.y >= Game.gameHeight) {
			y = oldY;
		}
		updateShape();
	}

	@Override
	public void render(Graphics2D g) {
		super.render(g);
		if (index == 0 || index == 1) {
			g.drawImage(gImg, (int) goal.getX(), (int) goal.getY(),
					Game.goalWidth, Game.goalLength, null);
		} else {
			g.drawImage(gImg, (int) goal.getX(), (int) goal.getY(),
					Game.goalLength, Game.goalWidth, null);
		}
		g.setColor(Color.WHITE);
		g.drawString("" + score, (int) (goal.getX() + (goal.getWidth() / 2)),
				(int) (goal.getY() + (goal.getHeight() / 2)));
	}
}
