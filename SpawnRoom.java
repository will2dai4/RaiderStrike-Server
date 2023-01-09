import java.util.ArrayList;

public class SpawnRoom extends Room{
    private final int[] spawnLocation;

    SpawnRoom(int width, int height, ArrayList<Obstacle> obstacles, ArrayList<Door> doors, int[] spawnLocation) {
        super(width, height, obstacles, doors);
        this.spawnLocation = spawnLocation;
    }
    
}
