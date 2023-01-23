import java.awt.geom.*;

public class Door extends GameObject{
    private final int idToRoom;
    private final int idToDoor;
    private final int direction;
    private Door exit;
    private Room thisRoom;

    Timer cooldown;

    Door(int width, int x, int y, int idToRoom, int idToDoor, int direction){
        super.setX(x);
        super.setY(y);
        
        this.idToRoom = idToRoom;
        this.idToDoor = idToDoor;
        this.direction = direction;

        this.cooldown = new Timer(); cooldown.setTimerLength(1);

        if(this.direction == 0 || this.direction == 2){ // up and down
            super.setWidth(width);
            super.setHeight(Const.DOOR_SIZE);
        } else if(this.direction == 1 || this.direction == 3){ // left and right
            super.setWidth(Const.DOOR_SIZE);
            super.setHeight(width);
        }

        this.hitbox = new Rectangle2D.Double(x, y, width, direction);
    }
    public void exit(){
        cooldown.start();
    }

    public int getIdToRoom() {
        return this.idToRoom;
    }
    public int getIdToDoor() {
        return this.idToDoor;
    }
    public int getDirection() {
        return this.direction;
    }
    public void setExit(Door door){
        this.exit = door;
    }
    public Door getExit(){
        return this.exit;
    }
    public Room getThisRoom() {
        return thisRoom;
    }
    public void setThisRoom(Room thisRoom) {
        this.thisRoom = thisRoom;
    }

    public int[] thisExitLocation(){
        switch(direction){
            case 0: return new int[]{(int)(this.x + this.width/2), (int)(this.y - Const.EXIT_DISTANCE)};
            case 1: return new int[]{(int)(this.x + Const.EXIT_DISTANCE), (int)(this.y + this.height/2)};
            case 2: return new int[]{(int)(this.x + this.width/2), (int)(this.y + Const.EXIT_DISTANCE)};
            case 3: return new int[]{(int)(this.x - Const.EXIT_DISTANCE), (int)(this.y + this.height/2)};
        }
        return new int[0];
    }

    public boolean collides(GameObject other) {
        return false;
    }
}
