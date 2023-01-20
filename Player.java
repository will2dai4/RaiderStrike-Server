import java.net.*;
import java.util.*;

import javafx.scene.shape.Circle;

import java.io.*;
import java.awt.*;

public class Player extends GameObject implements Runnable {
    private String name;
    private Team team; // 0 -> Red Team | 1 -> Blue Team
    private int playerId;
    private Agent agent;
    private boolean ready;
    private boolean loaded;

    private Gun primaryGun;
    private Gun secondaryGun;
    private int holdingSlot;

    private int health;
    private int shield;
    private boolean alive;
    private int numCredits;

    private boolean hasBomb;
    private double movementSpeed;
    private final double defaultMovementSpeed;
    private int direction; // degrees

    private Room room;

    private Circle hitbox;
    private Rectangle collisionBox;

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
        this.holdingSlot = 0;
        this.defaultMovementSpeed = Const.PLAYER_MOVEMENT_SPEED;
        this.movementSpeed = this.defaultMovementSpeed;

        this.hitbox = new Circle(super.getX(), super.getY(), Const.UNIT_SIZE/2);
        double collisionBoxSize = (Const.PLAYER_RADIUS)/(Const.COLLISION_BOX_RATIO);
        this.collisionBox = new Rectangle((int)collisionBoxSize, (int)collisionBoxSize, this.getWidth(), this.getHeight());

        this.state = this.server.state;
        this.messages = new LinkedList<String>();
    }

    public void run() {
        try {
            while (true) {
                String msg = input.readLine();
                if (msg.length() > 0) {
                    System.out.println("input: " + msg);
                    this.messages.add(msg);
                }
            }
        } catch (Exception e) {
            /* THIS IS WHAT IS RUN WHEN THE SOCKET CLOSES! TODO: implement player disconnect */
        }
    }

    public void update(){
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
                        case "SWAP": this.swap(args); break;
                        case "AIM": this.aim(args); break;
                        case "MOVE": this.move(args); break;
                        case "FIRE": this.fire(); break;
                        case "UTIL": this.util(args); break;
                        case "RELOAD": this.reload(); break;
                        case "BOMB": this.bomb(); break;
                        case "PICKUP": this.pickUp(args); break;
                    }
                case "BUYMENU":
                    switch(command){
                        case "BUY": this.buy(args); break;
                    }
            }
        }
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
        return this.collisionBox.intersects(obstacle.getHitbox());
    }
    public boolean collides(Room room){
        return false;
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
                    } else {System.out.println("team full");}
                    break;
                case 1:
                    if(this.server.blueTeam.addPlayer(this)){ 
                        this.setTeam(this.server.blueTeam);
                        this.print("JOINED"); 
                        this.server.printAll("TEAM " + server.redTeam.getTeamSize() + " " + server.blueTeam.getTeamSize());
                        for(Player player: this.server.blueTeam.getTeam()){
                            if(player != this) player.printInformation();
                        }
                    }
                    break;
            }
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
            case 0:
                this.setHoldingSlot(0);
                this.getHolding().takeOut();
                this.getHolding().setActive(true);
                this.primaryGun.setActive(false);
                this.server.printAll("PLAYER_GUN " + this.getPlayerId() + " " + this.secondaryGun);

            case 1:
                if(this.primaryGun != null){
                    this.setHoldingSlot(1);
                    this.getHolding().takeOut();
                    this.getHolding().setActive(true);
                    this.secondaryGun.setActive(false);
                    this.server.printAll("PLAYER_GUN " + this.getPlayerId() + " " + this.primaryGun);
                }
            }
        }
        
    }
    private void aim(String[] args){
        int angle = Integer.parseInt(args[0]);
        this.direction = angle;
    }
    private void move(String[] args){
        int moveOption = Integer.parseInt(args[0]);
        switch(moveOption){
            case 0: // not moving
            case 1: // up
                this.setY((int)(this.getY() - this.movementSpeed)); break;
            case 2: // down
                this.setY((int)(this.getY() + this.movementSpeed)); break;
            case 3: // left
                this.setX((int)(this.getX() - this.movementSpeed)); break;
            case 4: // right
                this.setX((int)(this.getX() + this.movementSpeed)); break;
            case 5: // up-right
                this.setX((int)(this.getX() + (this.movementSpeed / Math.sqrt(2))));
                this.setY((int)(this.getY() - (this.movementSpeed / Math.sqrt(2)))); break;
            case 6: // up-left
                this.setX((int)(this.getX() - (this.movementSpeed / Math.sqrt(2)))); 
                this.setY((int)(this.getY() - (this.movementSpeed / Math.sqrt(2)))); break;
            case 7: // down-left
                this.setX((int)(this.getX() - (this.movementSpeed / Math.sqrt(2))));
                this.setY((int)(this.getY() + (this.movementSpeed / Math.sqrt(2)))); break;
            case 8: // down-right
                this.setX((int)(this.getX() + (this.movementSpeed / Math.sqrt(2))));
                this.setY((int)(this.getY() + (this.movementSpeed / Math.sqrt(2)))); break;
        }

    }
    private void fire(){
        this.getHolding().fire();
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

    }

// ------------------------------------------------------------------------------------------------
    // getters and setters

    public String getName() {
        return this.name;
    }

    public int getTeam() {
        return this.team.getTeamNum();
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public Agent getAgent() {
        return this.agent;
    }

    public int getCredits() {
        return this.numCredits;
    }

    public boolean getAlive() {
        return this.alive;
    }

    public Gun getPrimGun() {
        return this.primaryGun;
    }

    public Gun getSecGun() {
        return this.secondaryGun;
    }

    public int getHealth() {
        return this.health;
    }

    public int getSheild(){
        return this.shield;
    }

    public double getMovementSpeed() {
        return this.movementSpeed;
    }

    public boolean checkSpike() {
        return this.hasBomb;
    }

    public int getDirection() {
        return this.direction;
    }

    public Gun getHolding() {
        if (this.holdingSlot == 1)
            return primaryGun;
        if (this.holdingSlot == 0)
            return secondaryGun;
        else
            return null;
    }

    public boolean getLoaded(){
        return this.loaded;
    }

    public boolean getReady(){
        return this.ready;
    }

    public Room getRoom() {
        return room;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setAgent(String agentName) {
        this.agent = Agent.valueOf(agentName);
    }

    public void setReady() {
        this.ready = true;
    }

    public void setCredits(int credits) {
        this.numCredits = credits;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setGun(int slot, Gun gun) {
        switch (slot) {
            case 0:
                this.primaryGun = gun;
            case 1:
                this.secondaryGun = gun;
        }
    }

    public void setMovementSpeed(double newSpeed) {
        this.movementSpeed = newSpeed;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setShield(int shield){
        this.shield = shield;
    }

    public void setSpike(boolean hasSpike) {
        this.hasBomb = hasSpike;
    }

    public void setDirection(int degrees) {
        this.direction = degrees % 360;
    }

    public void setDirection(int deltaX, int deltaY) {
        this.direction = (int) (Math.atan2(deltaY, deltaX));
    }

    public void setHoldingSlot(int gunSlot) {
        this.holdingSlot = gunSlot;
    }

    public void setLoaded(){
        this.loaded = true;
    }

    public void setRoom(Room room) {
        this.room = room;
    }


    @Override
    public boolean collides() {
        // TODO Auto-generated method stub
        return false;
    }
}