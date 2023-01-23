import java.awt.geom.*;
import java.awt.*;
/*
 * GameObject that encircle any game objects will hitboxes
 */

public abstract class GameObject{
    double x;
    double y;
    int width;
    int height;

    Rectangle2D hitbox;

    GameObject(){
        this.hitbox = new Rectangle((int)this.x, (int)this.y, this.width, this.height);
    }

    public int getX(){
        return (int)this.x;
    }
    public int getY(){
        return (int)this.y;
    }
    public double getDoubleY(){
        return this.y;
    }
    public double getDoubleX(){
        return this.x;
    }
    public int getWidth(){
        return this.width;
    }
    public int getHeight(){
        return this.height;
    }
    public Rectangle2D getHitBox(){
        return this.hitbox;
    }

    public void setX(double x){
        this.x = x;
        this.hitbox.setRect(x, this.y, this.width, this.height);
    }
    public void setY(double y){
        this.y = y;
        this.hitbox.setRect(this.x, y, this.width, this.height);
    }
    public void setWidth(int width){
        this.width = width;
        this.hitbox.setRect(this.x, this.y, width, this.height);
    }
    public void setHeight(int height){
        this.height = height;
        this.hitbox.setRect(this.x, this.y, this.width, height);
    }
}