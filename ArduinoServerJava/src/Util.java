import java.util.Random;

public class Util {
	public static Random rand;

	public static int randInt(int min, int max) {
		if (rand == null)
			rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	public static float randFloat(int min, int max) {
		if (rand == null)
			rand = new Random();
		return rand.nextFloat() * (max - min) + min;
	}
}
