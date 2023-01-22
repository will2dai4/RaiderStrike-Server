public class Circle extends GameObject{
    private final int radius;

    Circle(int x, int y, int width){
        super.setX(x);
        super.setY(y);
        super.setWidth(width);
        this.radius = super.getWidth()/2;
    }

    public int getCenterX(){
        return this.getX() + this.radius;
    }
    public int getCenterY(){
        return this.getY() + this.radius;
    }
    public int getRadius() {
        return this.radius;
    }
}
