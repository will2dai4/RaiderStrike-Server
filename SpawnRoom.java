import java.util.ArrayList;

public class SpawnRoom extends Room{
    private final int[] spawnLocation; // 3 pairs of coordinates

    SpawnRoom(int roomId, int width, int height, ArrayList<Obstacle> obstacles, ArrayList<Door> doors, int[] spawnLocation) {
        super(roomId, width, height, obstacles, doors);
        this.spawnLocation = spawnLocation;
    }

    public int[] getSpawn(){
        return this.spawnLocation;
    }
}
