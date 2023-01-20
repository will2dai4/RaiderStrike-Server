import java.awt.*;

public class Obstacle extends GameObject{
    private String shape;
    private boolean permeable;
    private Rectangle hitbox;

    Obstacle(String shape, int x, int y, int width, int height, boolean permeable){
        super.setX(x);
        super.setY(y);
        super.setWidth(width);
        super.setHeight(height);

        this.shape = shape;
        this.permeable = permeable;
        this.hitbox = new Rectangle(super.getWidth(), super.getHeight(), super.getX(), super.getY());
    }

    public Rectangle getHitbox(){
        return this.hitbox;
    }

    public boolean collides(GameObject other) {
        return false;
    }

    public boolean getPermeable(){
        return this.permeable;
    }
}
