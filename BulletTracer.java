import java.awt.geom.*;
import java.awt.*;
import java.util.*;

public class BulletTracer extends Line2D{
    private double startX;
    private double startY;
    private double endX;
    private double endY;

    private int direction;

    BulletTracer(double startX, double startY, int degrees){
        this.startX = startX;
        this.startY = startY;

        this.direction = degrees;
        this.endX = (Const.DEFAULT_BULLET_DISTANCE)*Math.cos((this.direction*Math.PI)/180);
        this.endY = (Const.DEFAULT_BULLET_DISTANCE)*Math.sin((this.direction*Math.PI)/180);
    }
    
    public double getX1() {
        return this.startX;
    }

    public double getY1() {
        return this.startY;
    }

    public Point2D getP1() {
        return new Point((int)startX, (int)startY);
    }

    public double getX2() {
        return this.endX;
    }

    public double getY2() {
        return this.endY;
    }

    public Point2D getP2() {
        return new Point((int)endX, (int)endY);
    }

    public int getDirection(){
        return this.direction;
    }

    public void setLine(double x1, double y1, double x2, double y2) {
        this.startX = x1;
        this.startY = y1;
        this.endX = x2;
        this.endY = y2;
    }

    public GameObject hits(ArrayList<GameObject> objects){
        ArrayList<GameObject> collides = new ArrayList<>();
        for(GameObject object: objects){
            if(this.intersects(object.getHitBox())){
                collides.add(object);
            }
        }
        if(!collides.isEmpty()){
            GameObject closest = collides.get(0);
            for(GameObject object: collides){
                if( Math.sqrt(Math.pow(object.getX() + this.getX1(), 2) + Math.pow(object.getY() + this.getY1(), 2)) <
                    Math.sqrt(Math.pow(closest.getX() + this.getX1(), 2) + Math.pow(closest.getY() + this.getY1(), 2))){
                        closest = object;
                }
            }
            return closest;
        }
        return null;
    }

    public int[] closestIntersection(GameObject object){
        /*TODO: this */
    }


    public Rectangle2D getBounds2D() {
        return null;
    }
}