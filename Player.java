public class Player extends GameObject{
    private String name;
    private int team; // 0 -> Red Team | 1 -> Blue Team
    private int playerId; 
    private Agent agent;
    private boolean ready;

    private int itemsHolding;
    private Gun primaryGun;
    private Gun secondaryGun;
    private int holdingSlot;
    private int health;
    private boolean alive;
    private int numCredits;
    private boolean hasSpike;
    private double movementSpeed;
    private final double defaultMovementSpeed;
    private int direction; // degrees

    Player(int newPlayerId){
        this.playerId = newPlayerId;
        
        this.team = -1;
        this.holdingSlot = 2;
        this.defaultMovementSpeed = Const.PLAYER_MOVEMENT_SPEED;
        this.movementSpeed = defaultMovementSpeed;
    }

    public boolean collides(GameObject other) {
        return false;
    }
    public void resetMovementSpeed(){
        this.movementSpeed = defaultMovementSpeed;
    }

//------------------------------------------------------------------------------------------------
    // getters and setters

    public String getName(){
        return this.name;
    }
    public int getTeam(){
        return this.team;
    }
    public int getPlayerId(){
        return this.playerId;
    }
    public Agent getAgent(){
        return this.agent;
    }
    public boolean checkReady(){
        return this.ready;
    }
    public int getCredits(){
        return this.numCredits;
    }
    public boolean getAlive(){
        return this.alive;
    }
    public Gun getPrimGun(){
        return this.primaryGun;
    }
    public Gun getSecGun(){
        return this.secondaryGun;
    }
    public int getHealth(){
        return this.health;
    }
    public double getMovementSpeed(){
        return this.movementSpeed;
    }
    public boolean checkSpike(){
        return this.hasSpike;
    }
    public int getDirection(){
        return this.direction;
    }
    public Gun getHolding(){
        if(this.holdingSlot == 1) return primaryGun;
        if(this.holdingSlot == 2) return secondaryGun;
        else return null;
    }
    public int getItemsHolding(){
        return this.itemsHolding;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setTeam(int teamId){
        this.team = teamId;
    }
    public void setAgent(String agentName){
        this.agent = Agent.valueOf(agentName);
    }
    public void setReady(){
        this.ready = !(this.ready);
    }
    public void setCredits(int credits){
        this.numCredits = credits;
    }
    public void setAlive(boolean alive){
        this.alive = alive;
    }
    public void setGun(int slot, Gun gun){
        switch(slot){
            case 0: this.primaryGun = gun;
            case 1: this.secondaryGun = gun;
        }
    }
    public void setMovementSpeed(double newSpeed){
        this.movementSpeed = newSpeed;
    }
    public void setHealth(int health){
        this.health = health;
    }
    public void setHealth(boolean damage, int amount){
        if(damage) this.health -= amount;
        if(!damage) this.health += amount;
    }
    public void setSpike(boolean hasSpike){
        this.hasSpike = hasSpike;
    }
    public void setDirection(int degrees){
        this.direction = degrees % 360;
    }
    public void setDirection(int deltaX, int deltaY){
        this.direction = (int)(Math.atan2(deltaY, deltaX));
    }
    public void setHolding(int gunSlot){
        switch(gunSlot){
            case 1: this.holdingSlot = gunSlot;
            case 2: this.holdingSlot = gunSlot;
        }
    }
    public void switchWeaponUp(){
        this.holdingSlot = (this.holdingSlot+1) % itemsHolding;
    }
    public void switchWeaponDown(){
        if(this.holdingSlot == 1) this.holdingSlot = this.itemsHolding;
        else this.holdingSlot--;
    }
    public void setItemsHolding(int numItems){
        this.itemsHolding = numItems;
    }
}
