public class Obstacle extends GameObject{
    private String shape;
    private int width;
    private int height;
    private boolean permeable;

    Obstacle(String shape, int x, int y, int width, int height, boolean permeable){
        super.setX(x);
        super.setY(y);
        super.setWidth(width);
        super.setHeight(height);
        this.shape = shape;
        this.permeable = permeable;
    }

    public boolean collides(GameObject other) {
        return false;
    }

    public boolean getPermeable(){
        return this.permeable;
    }
}
