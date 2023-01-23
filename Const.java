public class Const {
    public static final int PORT = 5001;

    public static final int PLAYER_RADIUS = 15;
    public static final double COLLISION_BOX_RATIO = 0.8;

    public static final int MAX_PLAYER_COUNT = 6;
    public static final int MAX_TEAM_SIZE = 3;
    public static final double PLAYER_MOVEMENT_SPEED = 6; // pixels per frame
    public static final double STARTING_VELOCITY = 30; // object velocity
    public static final double DECCELERATION = 8; // pixels/second

    public static final int MAX_NAME_LENGTH = 24;

    public static final String AUGUSTA_MAP_PATHNAME = "Maps/AugustaMap.txt";
    public static final int DOOR_SIZE = 2;
    public static final int EXIT_DISTANCE = PLAYER_RADIUS + 20;

    public static final int STARTING_HEALTH = 100;
    public static final int STARTING_SHIELD = 0;
    public static final int STARTING_CREDITS = 100000;
    public static final int ROUND_WIN_CREDITS = 3000;
    public static final int ROUND_LOSE_CREDITS1 = 2000;
    public static final int ROUND_LOSE_CREDITS2 = 2800;

    public static final int BUY_PERIOD_TIME = 15; // seconds
    public static final int ROUND_PERIOD_TIME = 120;

    public static final int PRIMARY_SLOT = 1;
    public static final int SECONDARY_SLOT = 2;

    public static final int DEFAULT_BULLET_DISTANCE = 2000;

    private Const(){}
}
