import java.awt.Color;

public class Pickup extends Puck {
	public Debuf effect = Debuf.NONE;

	public Pickup(Debuf effect, float x, float y, float radius, String src, Color color) {
		super(x, y, radius, src, color);
		this.effect = effect;
	}

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
	}
}
