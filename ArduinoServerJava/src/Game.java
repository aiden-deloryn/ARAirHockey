import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game implements Runnable {

	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	public static int gameWidth = (int) screenSize.getWidth();
	public static int gameHeight = (int) screenSize.getHeight();

	// GAME VALUES:
	public ArrayList<Player> players;
	public Puck puck;
	public static final int goalLength = gameHeight / 3;
	public static final int goalWidth = goalLength / 4;
	public static String wonText = "";

	// TEXTS:
	public Font bigFont;
	public Font smallFont;

	// WIN CONDITION:
	public int maxPoints = 5;
	public int gameOverTimer = 500;
	public int gameOverCounter = gameOverTimer;
	boolean gameOver = false;

	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;

	public Game() {
		frame = new JFrame("Basic Game");

		bigFont = new Font("TimesRoman", Font.PLAIN, gameHeight / 10);
		smallFont = new Font("TimesRoman", Font.PLAIN, gameHeight / 26);

		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(gameWidth, gameHeight));
		panel.setLayout(null);

		canvas = new Canvas();
		canvas.setBounds(0, 0, gameWidth, gameHeight);
		canvas.setIgnoreRepaint(true);

		panel.add(canvas);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);

		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();

		canvas.requestFocus();

		players = new ArrayList<>();
		puck = new Puck(gameWidth / 2, gameHeight / 2,
				(int) (Server.puckSize * 0.75), "resources/Puck.png",
				Color.WHITE);

		new Thread(this).start();
	}

	long desiredFPS = 60;
	long desiredDeltaLoop = (1000 * 1000 * 1000) / desiredFPS;

	boolean running = true;

	public void run() {

		long beginLoopTime;
		long endLoopTime;
		long currentUpdateTime = System.nanoTime();
		long lastUpdateTime;
		long deltaLoop;

		while (running) {
			beginLoopTime = System.nanoTime();

			render();

			lastUpdateTime = currentUpdateTime;
			currentUpdateTime = System.nanoTime();
			update(((currentUpdateTime - lastUpdateTime) / (1000 * 1000)));

			endLoopTime = System.nanoTime();
			deltaLoop = endLoopTime - beginLoopTime;

			if (deltaLoop > desiredDeltaLoop) {
			} else {
				try {
					Thread.sleep((desiredDeltaLoop - deltaLoop) / (1000 * 1000));
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private void render() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		g.clearRect(0, 0, gameWidth, gameHeight);
		render(g);
		g.dispose();
		bufferStrategy.show();
	}

	protected void update(float deltaTime) {
		float oldX = puck.x;
		float oldY = puck.y;
		boolean puckCollided = false;
		puck.update(deltaTime);
		for (Player player : players) {
			player.update(deltaTime);
			if (player.hasCollision(puck)) {
				puckCollided = true;
				puck.colliderCount += 1;
				Sound.play("HitSound", false);
				puck.getPushed(player);
				if (puck.colliderCount >= 2) {
					puck.xMov *= -1;
					puck.yMov *= -1;
					puck.colliderCount = 0;
				}
				puck.x = oldX + puck.xMov;
				puck.y = oldY + puck.yMov;
			}

			if (!gameOver && player.goal.getBounds2D().contains(puck.x, puck.y)) {
				Sound.play("ScoreSound", false);
				if (player.index != puck.lastHitIndex) {
					players.get(puck.lastHitIndex).score += 1;
					if (players.get(puck.lastHitIndex).score >= maxPoints) {
						// WIN!!
						wonText = ("The "
								+ players.get(puck.lastHitIndex).colorName + " Player has Won!");
						gameOver = true;
					}
				}
				puck.reset();
			}
		}
		if (!puckCollided) {
			puck.colliderCount = 0;
		}

		if (gameOver) {
			if (gameOverCounter > 0) {
				gameOverCounter -= 1;
				if (gameOverCounter <= 0) {
					gameOverCounter = gameOverTimer;
					gameOver = false;
					wonText = "";
					for (Player player : players) {
						player.score = 0;
					}
					puck.reset();
				}
			}
		}
	}

	public void addPlayer(Player newPlayer) {
		if (players.size() < 4) {
			newPlayer.index = players.size();
			newPlayer.setup();
			players.add(newPlayer);
		}
	}

	public boolean doesPlayerExist(String id) {
		return (findPlayerById(id) != null);
	}

	public void removePlayer(String id) {
		try {
			players.remove(findPlayerById(id));
		} catch (Exception e) {
		}
	}

	public void updatePlayerMov(String id, float x, float y) {
		Player tmpPlayer = findPlayerById(id);
		tmpPlayer.xMov = x;
		tmpPlayer.yMov = y;
	}

	public Player findPlayerById(String id) {
		for (Player player : players) {
			if (player.id.equals(id)) {
				return player;
			}
		}
		return null;
	}

	protected void render(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.setFont(smallFont);
		g.fillRect(0, 0, gameWidth, gameHeight);
		try {
			for (Player player : players) {
				player.render(g);
			}
		} catch (Exception e) {

		}
		puck.render(g);

		if (wonText.length() > 0) {
			g.setColor(Color.WHITE);
			g.setFont(bigFont);
			FontMetrics fm = g.getFontMetrics();
			Rectangle2D r = fm.getStringBounds(wonText, g);
			int x = ((gameWidth) - (int) r.getWidth()) / 2;
			int y = ((gameHeight) - (int) r.getHeight()) / 2;
			g.drawString(wonText, x, y);
		}
	}
}
