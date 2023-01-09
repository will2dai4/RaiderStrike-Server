import java.util.ArrayList;

public class Room {
    private final int width;
    private final int height;
    private final ArrayList<Obstacle> obstacles;
    private final ArrayList<Door> doors;
    private final int[] spawnLocation;

    Room(int width, int height, ArrayList<Obstacle> obstacles, ArrayList<Door> doors, int[] spawnLocation){
        this.width = width;
        this.height = height;
        this.obstacles = obstacles;
        this.doors = doors;
        this.spawnLocation = spawnLocation;
    }
    Room(int width, int height, ArrayList<Obstacle> obstacles, ArrayList<Door> doors){
        this.width = width;
        this.height = height;
        this.obstacles = obstacles;
        this.doors = doors;
        this.spawnLocation = new int[0];
    }
}
