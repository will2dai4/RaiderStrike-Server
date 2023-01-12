public class Door extends GameObject{
    private final char wall;
    private final int distanceOnWall;
    private final Room room;
    private Door exit;

    Door(char wall, int distanceOnWall, Room doorRoom){
        this.wall = wall;
        this.distanceOnWall = distanceOnWall;
        this.room = doorRoom;
        switch(wall){
            case 'A': /* TODO: instantiate x and y values based on cases */
            case 'B':
            case 'C':
            case 'D':
        }
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
