import java.net.*;
import java.util.*;
import java.io.*;

public class Player extends GameObject implements Runnable {
    private String name;
    private Team team; // 0 -> Red Team | 1 -> Blue Team
    private int playerId;
    private Agent agent;
    private boolean ready;
    private boolean loaded;

    private int itemsHolding;
    private Gun primaryGun;
    private Gun secondaryGun;
    private int holdingSlot;
    private int health;
    private boolean alive;
    private int numCredits;
    private boolean hasBomb;
    private double movementSpeed;
    private final double defaultMovementSpeed;
    private int direction; // degrees

    private Server server;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    private GameState state;
    private Queue<String> messages;

    Player(int playerId, Socket socket, Server server) throws IOException {
        this.playerId = playerId;
        this.loaded = false;

        this.server = server;
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream());

        this.holdingSlot = 2;
        this.defaultMovementSpeed = Const.PLAYER_MOVEMENT_SPEED;
        this.movementSpeed = defaultMovementSpeed;

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

    public boolean collides(GameObject other) {
        return false;
    }

    public void resetMovementSpeed() {
        this.movementSpeed = defaultMovementSpeed;
    }

// ------------------------------------------------------------------------------------------------
    // Client to server commands
    /* TODO: this */
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
                        this.server.printAll("TEAM " + server.redTeam.getTeamSize() + " " + server.blueTeam.getTeamSize());
                    }
                    break;
                case 1:
                    if(this.server.blueTeam.addPlayer(this)){ 
                        this.setTeam(this.server.blueTeam);
                        this.print("JOINED"); 
                        this.server.printAll("TEAM " + server.redTeam.getTeamSize() + " " + server.blueTeam.getTeamSize());
                    }
                    break;
            }
            
        }
    }
    private void agent(String[] args){
        if(!this.ready){
            String agentName = args[0];
            this.setAgent(agentName);
            this.server.printAll("AGENT " + this.getPlayerId() + " " + agentName);
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
        this.server.printAll("LOADED " + this.getPlayerId());
    }

    private void swap(String[] args){
        int slot = Integer.parseInt(args[0]);
        this.setHolding(slot);
        this.server.printAll("PLAYER_GUN " + this.getPlayerId() + " " + this.getItemsHolding()); /* TODO: edit get items holding */
    }
    private void aim(String[] args){
        
    }
    private void move(String[] args){
        
    }
    private void fire(){

    }
    private void util(String[] args){
        
    }
    private void reload(){
        
    }
    private void bomb(){
        
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

    public boolean checkReady() {
        return this.ready;
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
        if (this.holdingSlot == 2)
            return secondaryGun;
        else
            return null;
    }

    public int getItemsHolding() {
        return this.itemsHolding;
    }

    public boolean getLoaded(){
        return this.loaded;
    }

    public boolean getReady(){
        return this.ready;
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

    public void setHealth(boolean damage, int amount) {
        if (damage)
            this.health -= amount;
        if (!damage)
            this.health += amount;
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

    public void setHolding(int gunSlot) {
        switch (gunSlot) {
            case 1:
                this.holdingSlot = gunSlot;
            case 2:
                this.holdingSlot = gunSlot;
        }
    }

    public void switchWeaponUp() {
        this.holdingSlot = (this.holdingSlot + 1) % itemsHolding;
    }

    public void switchWeaponDown() {
        if (this.holdingSlot == 1)
            this.holdingSlot = this.itemsHolding;
        else
            this.holdingSlot--;
    }

    public void setItemsHolding(int numItems) {
        this.itemsHolding = numItems;
    }

    public void setLoaded(){
        this.loaded = true;
    }
}