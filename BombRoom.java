import java.util.ArrayList;

public class BombRoom extends Room{
    BombRoom(int roomId, int width, int height, ArrayList<Obstacle> obstacles, ArrayList<Door> doors) {
        super(roomId, width, height, obstacles, doors);
    }
}
