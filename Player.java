public class Player extends GameObject{
    private String name;
    private int team; // 0 -> Red Team | 1 -> Blue Team
    private int playerId; 
    private int agent; // agent Id
    private boolean ready;

    private int numCredits;
    private boolean alive;
    private int[] guns; // gun Id
    private int health;
    private boolean hasSpike;

    Player(int newPlayerId){
        this.playerId = newPlayerId;
        
        this.team = -1;
        this.agent = -1;
    }

    public boolean coolidesWith(GameObject other) {
        return false;
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
    public int getAgent(){
        return this.agent;
    }
    public boolean checkReady(){
        return this.ready;
    }
    public int getCredits(){
        return this.numCredits;
    }
    public boolean checkAlive(){
        return this.alive;
    }
    public int[] getGuns(){
        return this.guns;
    }
    public int getHealth(){
        return this.health;
    }
    public boolean checkSpike(){
        return this.hasSpike;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setTeam(int teamId){
        this.team = teamId;
    }
    public void setAgent(int agentId){
        this.agent = agentId;
    }
    public void switchReady(){
        this.ready = !(this.ready);
    }
    public void setCredits(int credits){
        this.numCredits = credits;
    }
    public void switchAlive(){
        this.alive = !(this.alive);
    }
    public void setGuns(int slot, int gunId){
        this.guns[slot] = gunId;
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
}
