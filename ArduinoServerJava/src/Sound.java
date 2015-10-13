import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {

	public static Clip mainClip;

	public static synchronized void play(String name, final boolean loop) {
		final String fileName = "/sounds/" + name + ".wav";
		new Thread(new Runnable() {
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem
							.getAudioInputStream(Game.class
									.getResource(fileName));
					clip.open(inputStream);
					if (loop) {
						if (mainClip != null) // REPLACE:
							mainClip.stop();
						clip.loop(clip.LOOP_CONTINUOUSLY);
						mainClip = clip;
					}
					clip.start();
				} catch (Exception e) {
					System.out.println("play sound error: " + e.getMessage()
							+ " for " + fileName);
				}
			}
		}).start();
	}
}
