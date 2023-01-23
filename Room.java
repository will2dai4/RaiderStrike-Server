import java.util.ArrayList;
import java.awt.*;
/*
 * Room class that handles room attributes
 */

public class Room {
    private final int roomId;
    private final int width;
    private final int height;
    private final Rectangle roomRect;

    private final ArrayList<Obstacle> obstacles;
    private final ArrayList<Door> doors;
    private final ArrayList<Player> players;

    private boolean isBombRoom;

    Room(int roomId, int width, int height, ArrayList<Obstacle> obstacles, ArrayList<Door> doors){
        this.roomId = roomId;
        this.width = width;
        this.height = height;
        this.roomRect = new Rectangle(0, 0, this.width, this.height);

        this.obstacles = obstacles;
        this.doors = doors;
        this.players = new ArrayList<>();
    }

    public int getWidth(){
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }

    public int getId(){
        return this.roomId;
    }

    public void setBombRoom(){
        this.isBombRoom = true;
    }

    public int[] getSpawn() {
        return null;
    }

    public void addPlayer(Player player){
        this.players.add(player);
    }

    public Player removePlayer(Player player){
        if(this.players.remove(player)){
            return player;
        }
        return null;
    }

    public ArrayList<Obstacle> getObstacles(){
        return this.obstacles;
    }

    public ArrayList<Door> getDoors() {
        return this.doors;
    }

    public ArrayList<Player> getPlayers(){
        return this.players;
    }
}
