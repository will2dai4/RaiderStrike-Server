import java.util.ArrayList;

public class BombRoom extends Room{
    private boolean bombPlanted;
    private int plantLocationX;
    private int plantLocationY;

    BombRoom(int roomId, int width, int height, ArrayList<Obstacle> obstacles, ArrayList<Door> doors) {
        super(roomId, width, height, obstacles, doors);
    }

    public void plant(){
        this.bombPlanted = true;
    }
    public void defuse(){
        this.bombPlanted = false;
    }

    public int getPlantX(){
        return this.plantLocationX;
    }
    public int getPlantY(){
        return this.plantLocationY;
    }
}
