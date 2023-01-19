public class Door extends GameObject{
    private final int idToRoom;
    private final int direction;
    private Door exit;

    Door(int width, int x, int y, int idToRoom, int direction){
        super.setWidth(width);
        super.setX(x);
        super.setY(y);
        this.idToRoom = idToRoom;
        this.direction = direction;
    }

    public void setExit(Door door){
        this.exit = door;
    }
    public Door getExit(){
        return this.exit;
    }

    public boolean collides(GameObject other) {
        return false;
    }
}
