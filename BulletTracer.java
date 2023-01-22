import java.awt.geom.*;
import java.awt.*;
import java.util.*;

public class BulletTracer extends Line2D{
    private double startX;
    private double startY;
    private double endX;
    private double endY;

    private int direction;

    BulletTracer(int x, int y, int degrees){
        this.startX = x;
        this.startY = y;

        this.direction = degrees;
        this.endX = (Const.DEFAULT_BULLET_DISTANCE)*Math.cos((this.direction*Math.PI)/180);
        this.endY = (Const.DEFAULT_BULLET_DISTANCE)*Math.sin((this.direction*Math.PI)/180)*(-1); // -1 accounts for java's flipped y-axis
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

    public void closestIntersection(GameObject object){ // finds closest bullet-object collision points
        Line2D topLine = new Line2D.Double(object.getX(), object.getY(), object.getX() + object.getWidth(), object.getY());
        Line2D botLine = new Line2D.Double(object.getX(), object.getY() + object.getHeight(), object.getX() + object.getWidth(), object.getY() + object.getHeight());
        Line2D leftLine = new Line2D.Double(object.getX(), object.getY(), object.getX(), object.getY() + object.getHeight());
        Line2D rightLine = new Line2D.Double(object.getX() + object.getWidth(), object.getY(), object.getX() + object.getWidth(), object.getY() + object.getHeight());
        int centerX = object.getX() + object.getWidth()/2;
        int centerY = object.getY() + object.getHeight()/2;
        
        ArrayList<Line2D> intersectList = new ArrayList<>();
        if(this.intersectsLine(topLine)) intersectList.add(topLine);
        if(this.intersectsLine(botLine)) intersectList.add(botLine);
        if(this.intersectsLine(leftLine)) intersectList.add(leftLine);
        if(this.intersectsLine(rightLine)) intersectList.add(rightLine);

        ArrayList<Line2D> possibleFirstIntersect = new ArrayList<>();
        if(this.getX1() >= centerX){
            possibleFirstIntersect.add(rightLine);
            if(this.getY1() >= centerY){
                possibleFirstIntersect.add(botLine);
            } else {
                possibleFirstIntersect.add(topLine);
            }
        } else {
            possibleFirstIntersect.add(leftLine);
            if(this.getY1() >= centerY){
                possibleFirstIntersect.add(botLine);
            } else {
                possibleFirstIntersect.add(topLine);
            }
        }

        intersectList.retainAll(possibleFirstIntersect);

        if(intersectList.isEmpty()){
            Line2D intersectionLine = intersectList.get(0);

            double thisSlope = (this.endY - this.startY) / (this.endX - this.startX);
            double thisB = this.startY - (thisSlope*this.startX);

            int intersectionX, intersectionY;
            if(intersectionLine == topLine || intersectionLine == botLine){
                intersectionX = (int)((intersectionLine.getY1() - thisB) / thisSlope);
                intersectionY = (int)((thisSlope*intersectionX) + thisB);
            } else {
                intersectionY = (int)((thisSlope*intersectionLine.getX1()) + thisB);
                intersectionX = (int)((intersectionY - thisB) / thisSlope);
            }

            this.endX = intersectionX;
            this.endY = intersectionY;
        }
    }


    public Rectangle2D getBounds2D() {
        return null;
    }
}