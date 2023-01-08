public abstract class Ability extends GameObject{
    private final int size;
    private final int damage;
    private final double lingerTime;

    Ability(int size, int damage, double lingerTime){
        this.size = size;
        this.damage = damage;
        this.lingerTime = lingerTime;
    }
}