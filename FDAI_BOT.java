import com.badlogic.gdx.math.Vector2;
import com.capgemini.spacerobots.engine.Robot;
import com.capgemini.spacerobots.engine.ScannedObject;
import com.capgemini.spacerobots.engine.playerclasses.IPilot;
import com.capgemini.spacerobots.engine.playerclasses.IScanController;
import com.capgemini.spacerobots.engine.playerclasses.IWeaponController;
import com.capgemini.spacerobots.util.SpaceRobotsMathUtil;

@SuppressWarnings("serial")
public class FDAI_BOT extends Robot {
	public static final String NAME = "FDAI_BOT";
	float heading = 0.0f;
	float speed = 1.0f;
	float scanLength, scanAperture = 25.0f;
	public static float scanDirection = 0.0f;
	float health, currHealth;
	int knownRobots, currNumRobots;

	boolean canScan, canShoot;

	float levelWidth, levelheight;

	IWeaponController gunner = getWeaponController();
	IScanController radar = getScanController();
	IPilot pilot = getPilot();

	public FDAI_BOT() {
		super(NAME);
	}

	@Override
	public void update() {
		scanDirection = scanDirection + 45;
		if (scanDirection > 360)
			scanDirection = 0;

		scann(scanDirection);

		ScannedObject dir = radar.getLastScanResult();
		Vector2 scanPos = dir.getPosition();
		System.out.println("scanPos:  " + scanPos);

		currNumRobots = getLevelInfo().getNumRobots();

		if (currNumRobots == knownRobots && scanPos != null) {
			System.out.println("shoot");
			shoot(scanPos);
		}
		if (currNumRobots < knownRobots)
			knownRobots = currNumRobots;

		currHealth = getDashboard().getHealth();

		if (currHealth < health)
			moveBot(heading, speed); // We were hit
		else
			pilot.stopRobot();

	}

	private void moveBot(float heading, float acceleration) {

		// If collides with other robots, change direction
		if (getDashboard().getBorderCollision().isCollided() == true) {
			heading = 90;
			speed = 3;
		}
		getPilot().moveRobot(heading, speed);

	}

	private void shoot(Vector2 dir) {
		gunner.shootRocket(dir);
	}

	private void scann(float scanning) {
		System.out.println("scanning " + scanning);
		radar.scan(scanning, scanLength, scanAperture);
	}

	@Override
	public void init() {
		speed = 3;
		levelWidth = getLevelInfo().getLevelWidth();
		levelheight = getLevelInfo().getLevelHeight();
		health = getDashboard().getHealth();
		knownRobots = getLevelInfo().getNumRobots();

		heading = getDashboard().getHeading();
		Vector2 lowerLeft = new Vector2(0, 0);
		Vector2 upperRight = new Vector2(levelheight, levelWidth);

		scanLength = SpaceRobotsMathUtil.distanceBetweenTwoPoints(lowerLeft, upperRight);

	}

}
