import java.net.*;
import java.util.*;
import java.awt.geom.*;
import java.io.*;
import java.awt.*;
import java.awt.geom.*;
/*
 * Player class that handles player thread, attributes, and any player-server messages
 */

public class Player extends GameObject implements Runnable {
    private int x;
    private int y;
    // x and y are player center
    private String name;
    private Team team; // 0 -> Red Team | 1 -> Blue Team
    private int playerId;
    private Agent agent;
    private boolean ready;
    private boolean loaded;

    private Gun primaryGun;
    private Gun secondaryGun;
    private int holdingSlot; // 1 -> primary gun | 2 -> secondary gun

    private int health;
    private int shield;
    private boolean alive;
    private int numCredits;

    private boolean hasBomb;
    private boolean moving;
    private boolean firing;
    private int moveDirection;
    private double movementSpeed;
    private final double defaultMovementSpeed;
    private int direction; // degrees

    private Room room;

    private int radius;

    private Server server;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    private GameState state;
    private Queue<String> messages;
    private Timer playerActionDelay; /*TODO: implement this */

    Player(int playerId, Socket socket, Server server) throws IOException {
        this.playerId = playerId;
        this.loaded = false;

        this.server = server;
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream());

        this.health = Const.STARTING_HEALTH;
        this.shield = Const.STARTING_SHIELD;
        this.holdingSlot = 2;
        this.moving = false;
        this.firing = false;
        this.moveDirection = 0;
        this.defaultMovementSpeed = Const.PLAYER_MOVEMENT_SPEED;
        this.movementSpeed = this.defaultMovementSpeed;

        this.radius = Const.PLAYER_RADIUS;

        this.state = this.server.state;
        this.messages = new LinkedList<String>();
    }

    public int getX() {
        return x;
    }
    public void setX(double x) {
        this.x = (int)x;
    }
    public int getY() {
        return y;
    }
    public void setY(double y) {
        this.y = (int)y;
    }
    public Ellipse2D.Double getHitbox() {
        return new Ellipse2D.Double(this.x - Const.PLAYER_RADIUS, this.y - Const.PLAYER_RADIUS, Const.PLAYER_RADIUS * 2, Const.PLAYER_RADIUS * 2);
    }
    public void run() {
        try {
            while (true) {
                String msg = input.readLine();
                if (msg.length() > 0) {
                    System.out.println("input from " + this.playerId + ": " + msg);
                    this.messages.add(msg);
                }
            }
        } catch (Exception e) {
            /* THIS IS WHAT IS RUN WHEN THE SOCKET CLOSES! TODO: implement player disconnect */
        }
    }

    public void update() throws InterruptedException{
        while(!messages.isEmpty()){
            String[] msg = messages.poll().split(" ");
            String command = msg[0];
            String[] args = Arrays.copyOfRange(msg, 1, msg.length);
            switch(state.name()){
                case "PREGAME":
                    switch(command){
                        case "NAME": this.name(args);  break;
                        case "TEAM": this.team(args); break;
                        case "AGENT": this.agent(args); break;
                        case "READY": this.ready(); break;
                    }
                case "LOADING":
                    switch(command){
                        case "LOADED": this.loaded(); break;
                    }
                case "INGAME":
                    switch(command){
                        case "SWAP": if(this.alive) this.swap(args); break;
                        case "AIM": if(this.alive) this.aim(args); break;
                        case "MOVE": if(this.alive) this.move(args); break;
                        case "FIRE": if(this.alive) this.fire(args); break;
                        case "UTIL": if(this.alive) this.util(args); break;
                        case "RELOAD": if(this.alive) this.reload(); break;
                        case "BOMB": if(this.alive) this.bomb(); break;
                        case "PICKUP": if(this.alive) this.pickUp(args); break;
                    }
                case "BUYMENU":
                    switch(command){
                        case "BUY": this.buy(args); break;
                    }
            }
        }

        if(this.moving){
            this.move(new String[]{this.moveDirection + ""});
        }
        if(this.firing){
            int[] bullet = this.getHolding().fire();
            if(bullet[0] == 1){
                int bulletDirection = bullet[1] + this.getDirection();
                this.server.shoot(new BulletTracer(this.getX(), this.getY(), bulletDirection, GunModel.valueOf(this.getHolding().getModel()).getDamage()), this);
            }
        }
        try{
            Thread.sleep(10);
        } catch (InterruptedException e){ e.printStackTrace(); }
    }

    public void print(String text) {
        if (this.socket == null) {
            System.out.println("Dead socket, message send failure");
            return;
        }
        this.output.println(text);
        System.out.println(text);
        this.output.flush();
    }
    public void printInformation(){
        this.server.printTeam("NAME " + this.getPlayerId() + " " + this.getName(), this.team);
        if(this.getAgent() != null){
            this.server.printTeam("AGENT " + this.getPlayerId() + " " + this.getAgent().toString(), this.team);
        }
        if(this.getReady()){
            this.server.printTeam("READY " + this.getPlayerId(), this.team);
        }
    }

    public void kill() {
        Gun gunDrop;
        if (this.getPrimGun() != null) {
            gunDrop = this.getPrimGun();
        } else {
            gunDrop = this.getSecGun();
        }
        this.setAlive(false);
        /* TODO: Drop gun function */
    }

    public boolean collides(Obstacle obstacle) {
        return this.getHitbox().intersects(obstacle.getHitbox());
    }
    
    public void resetMovementSpeed() {
        this.movementSpeed = defaultMovementSpeed;
    }

// ------------------------------------------------------------------------------------------------
    // Client to server commands

    private void name(String[] args) {
        if(!this.ready){
            String playerName = "";
            for(String name: args){
                playerName = playerName+name;
            }
            this.name = playerName;
            this.server.printAll("NAME " + this.getPlayerId() + " " + playerName);
        }
    }
    private void team(String[] args){
        if(!this.ready){
            int teamId = Integer.parseInt(args[0]);
            switch(teamId){
                case 0: 
                    if(this.server.redTeam.addPlayer(this)){ 
                        this.setTeam(this.server.redTeam);
                        this.print("JOINED"); 
                        this.server.printAll("TEAM " + this.server.redTeam.getTeamSize() + " " + this.server.blueTeam.getTeamSize());
                        for(Player player: this.server.redTeam.getTeam()){
                            if(player != this) player.printInformation();
                        }
                    } else { System.out.println("team full"); }
                    break;
                case 1:
                    if(this.server.blueTeam.addPlayer(this)){ 
                        this.setTeam(this.server.blueTeam);
                        this.print("JOINED"); 
                        this.server.printAll("TEAM " + server.redTeam.getTeamSize() + " " + server.blueTeam.getTeamSize());
                        for(Player player: this.server.blueTeam.getTeam()){
                            if(player != this) player.printInformation();
                        }
                    } else { System.out.println("team full"); }
                    break;
            }
            this.server.printAll("PLAYER_TEAM " + this.getPlayerId() + " " + this.getTeam());
        }
    }
    private void agent(String[] args){
        if(!this.ready){
            String agentName = args[0];
            this.setAgent(agentName);
            this.server.printTeam("AGENT " + this.getPlayerId() + " " + agentName, this.team);
        }
    }
    private void ready(){
        if(this.agent != null && this.team != null && this.team.addAgent(this.agent)){
            this.setReady();
            this.server.printAll("READY " + this.getPlayerId());
        }
    }

    private void loaded(){
        this.setLoaded();
    }

    private void swap(String[] args){
        int slot = Integer.parseInt(args[0]);
        if(slot != this.holdingSlot){
            switch(slot){
                case Const.PRIMARY_SLOT:
                    if(this.primaryGun != null){
                        this.setHoldingSlot(Const.PRIMARY_SLOT);
                        this.getHolding().takeOut();
                        this.getHolding().setActive(true);
                        this.secondaryGun.setActive(false);
                        this.server.printAll("PLAYER_GUN " + this.getPlayerId() + " " + this.primaryGun.getModel());
                    }
                case 2:
                    this.setHoldingSlot(Const.SECONDARY_SLOT);
                    this.getHolding().takeOut();
                    this.getHolding().setActive(true);
                    this.primaryGun.setActive(false);
                    this.server.printAll("PLAYER_GUN " + this.getPlayerId() + " " + this.secondaryGun.getModel());
            }
        }
    }
    private void aim(String[] args){
        int angle = Integer.parseInt(args[0]);
        this.direction = angle;
        this.server.printAll("PLAYER_TURN " + this.getPlayerId() + " " + this.direction);
    }
    private void move(String[] args) throws InterruptedException{
        this.moveDirection = Integer.parseInt(args[0]);
        switch(this.moveDirection){
            case 0: // not moving
                this.moving = false;
                break;
            case 1: // up
                this.moving = true;
                this.setY(this.y - this.movementSpeed); break;
            case 2: // down
                this.moving = true;
                this.setY(this.y + this.movementSpeed); break;
            case 3: // left
                this.moving = true;
                this.setX(this.x - this.movementSpeed); break;
            case 4: // right
                this.moving = true;
                this.setX(this.x + this.movementSpeed); break;
            case 5: // up-right
                this.moving = true;
                this.setX(this.x + (this.movementSpeed / Math.sqrt(2)));
                this.setY(this.y - (this.movementSpeed / Math.sqrt(2))); break;
            case 6: // up-left
                this.moving = true;
                this.setX(this.x - (this.movementSpeed / Math.sqrt(2))); 
                this.setY(this.y - (this.movementSpeed / Math.sqrt(2))); break;
            case 7: // down-left
                this.moving = true;
                this.setX(this.x - (this.movementSpeed / Math.sqrt(2)));
                this.setY(this.y + (this.movementSpeed / Math.sqrt(2))); break;
            case 8: // down-right
                this.moving = true;
                this.setX(this.x + (this.movementSpeed / Math.sqrt(2)));
                this.setY(this.y + (this.movementSpeed / Math.sqrt(2))); break;
        }
        if(this.moveDirection != 0){
            if((this.x - this.radius) < 0)                         { this.setX(this.radius); }
            if((this.y - this.radius) < 0)                         { this.setY(this.radius); }
            if((this.x + this.radius) > this.getRoom().getWidth()) { this.setX(this.getRoom().getWidth() - this.radius); }
            if((this.y + this.radius) > this.getRoom().getHeight()){ this.setY(this.getRoom().getHeight() - this.radius); }
            /*
            for(Obstacle obstacle: this.room.getObstacles()){
                if(this.collides(obstacle)){
                    Rectangle2D collision = this.getHitbox().createIntersection(obstacle.getHitbox());
                    switch(this.moveDirection){
                        case 1: // up
                            this.setY(obstacle.getDoubleY() + obstacle.getHeight() + this.radius); break;
                        case 2: // down
                            this.setY(obstacle.getDoubleY() + this.radius); break;
                        case 3: // left
                            this.setX(obstacle.getDoubleX() + obstacle.getWidth() + this.radius); break;
                        case 4: // right
                            this.setX(obstacle.getDoubleX() + this.getWidth()); break;
                        case 5:
                            if(collision.getWidth() >= collision.getHeight()){ 
                                this.setY(obstacle.getDoubleY() + obstacle.getHeight() + this.radius); // up
                            } else {
                                this.setX(obstacle.getDoubleX() + this.getWidth()); // right
                            } break;
                        case 6:
                            if(collision.getWidth() >= collision.getHeight()){
                                this.setY(obstacle.getDoubleY() + obstacle.getHeight() + this.radius); // up
                            } else {
                                this.setX(obstacle.getDoubleX() + obstacle.getWidth() + this.radius); // left
                            } break;
                        case 7:
                            if(collision.getWidth() >= collision.getHeight()){
                                this.setY(obstacle.getDoubleY() + this.radius); // down
                            } else {
                                this.setX(obstacle.getDoubleX() + obstacle.getWidth() + this.radius); // left
                            } break;
                        case 8:
                            if(collision.getWidth() >= collision.getHeight()){
                                this.setY(obstacle.getDoubleY() + this.radius); // down
                            } else {
                                this.setX(obstacle.getDoubleX() + this.getWidth()); // right
                            } break;
                    }
                }
            }
            */

            for(Door door: this.room.getDoors()){
                if(this.getHitbox().intersects(door.getHitBox()) && door.cooldown.finished()){
                    this.setRoom(door.getExit().getThisRoom());
                    this.setX(door.getExit().thisExitLocation()[0]);
                    this.setY(door.getExit().thisExitLocation()[1]);
                    door.getExit().exit();
                    System.out.println(door.getWidth() + " " + door.getHeight() + " " + door.getX() + " " + door.getY() + " " + this.room.getId());
                    this.server.printAll("PLAYER_ROOM " + this.getPlayerId() + " " + this.getRoom().getId());
                    break;
                }
            }

            this.server.printAll("PLAYER_LOCATION " + this.getPlayerId() + " " + this.getX() + " " + this.getY());
        }

        Thread.sleep(10);
    }
    private void fire(String[] args){
        int toggle = Integer.parseInt(args[0]); // shooting press and release
        int[] bullet;

        if(this.getHolding().model.getSemiAuto()) {
            this.firing = (toggle == 1);
        } else if(toggle == 1){
            bullet = this.getHolding().fire();
            if(bullet[0] == 1){
                int bulletDirection = bullet[1] + this.getDirection();
                this.server.shoot(new BulletTracer(this.getX(), this.getY(), bulletDirection, GunModel.valueOf(this.getHolding().getModel()).getDamage()), this);
            }
        }
    }
    private void util(String[] args){
        
    }
    private void reload(){
        this.getHolding().reload();
    }
    private void bomb(){ /*TODO: do this */
        
    }
    private void pickUp(String[] args){
        
    }

    private void buy(String[] args){
        String gunName = args[0];
        if(!gunName.equals("0")){
            GunModel gun = GunModel.valueOf(gunName);
            if(gun.getPrice() <= this.getCredits()){
                this.setCredits(this.getCredits() - gun.getPrice());
                this.print("CREDS " + this.getCredits());

                if( gun.toString().equals("Robin") ||
                    gun.toString().equals("Duck") ||
                    gun.toString().equals("Finch") ||
                    gun.toString().equals("Hummingbird") ||
                    gun.toString().equals("Raven")){
                    this.setGun(2, new Gun(gun.toString(), gun.getMaxAmmo()));
                } else {
                    this.setGun(1, new Gun(gun.toString(), gun.getMaxAmmo()));
                }

                this.server.printAll("PLAYER_GUN " + this.getPlayerId() + " " + this.getHolding().getModel());
                this.print("PICKUP " + this.getHolding().getModel());
            }
        }
    }

// ------------------------------------------------------------------------------------------------
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getTeam() {
        return this.team.getTeamNum();
    }
    public void setTeam(Team team) {
        this.team = team;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public Agent getAgent() {
        return this.agent;
    }
    public void setAgent(String agentName) {
        this.agent = Agent.valueOf(agentName);
    }

    public int getCredits() {
        return this.numCredits;
    }
    public void setCredits(int credits) {
        this.numCredits = credits;
    }

    public boolean getAlive() {
        return this.alive;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Gun getPrimGun() {
        return this.primaryGun;
    }
    public Gun getSecGun() {
        return this.secondaryGun;
    }
    public void setGun(int slot, Gun gun) {
        switch (slot) {
            case Const.PRIMARY_SLOT:
                this.primaryGun = gun;
            case Const.SECONDARY_SLOT:
                this.secondaryGun = gun;
        }
    }

    public Gun getHolding() {
        if (this.holdingSlot == Const.PRIMARY_SLOT)
            return primaryGun;
        if (this.holdingSlot == Const.SECONDARY_SLOT)
            return secondaryGun;
        else
            return null;
    }
    public int getHoldingSlot(){
        return this.holdingSlot;
    }
    public void setHoldingSlot(int gunSlot) {
        this.holdingSlot = gunSlot;
    }

    public int getHealth() {
        return this.health;
    }
    public void setHealth(int health) {
        this.health = health;
    }

    public int getSheild(){
        return this.shield;
    }
    public void setShield(int shield){
        this.shield = shield;
    }

    public double getMovementSpeed() {
        return this.movementSpeed;
    }
    public void setMovementSpeed(double newSpeed) {
        this.movementSpeed = newSpeed;
    }

    public int getDirection() {
        return this.direction;
    }
    public void setDirection(int degrees) {
        this.direction = degrees % 360;
    }

    public boolean checkSpike() {
        return this.hasBomb;
    }
    public void setSpike(boolean hasSpike) {
        this.hasBomb = hasSpike;
    }

    public boolean getLoaded(){
        return this.loaded;
    }
    public void setLoaded(){
        this.loaded = true;
    }

    public boolean getReady(){
        return this.ready;
    }
    public void setReady() {
        this.ready = true;
    }

    public Room getRoom() {
        return room;
    }
    public void setRoom(Room room) {
        this.room = room;
    }
}