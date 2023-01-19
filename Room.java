import java.util.ArrayList;

public class Room {
    private final int roomId;
    private final int width;
    private final int height;
    private final ArrayList<Obstacle> obstacles;
    private final ArrayList<Door> doors;

    Room(int roomId, int width, int height, ArrayList<Obstacle> obstacles, ArrayList<Door> doors){
        this.roomId = roomId;
        this.width = width;
        this.height = height;
        this.obstacles = obstacles;
        this.doors = doors;
    }
}
