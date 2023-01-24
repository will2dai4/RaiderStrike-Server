import java.util.ArrayList;
import java.util.HashSet;
import java.io.*;
/*
 * Map class that handles holding map attributes and building the map 
 * from map files
 */

public class Map {
    private String name;
    private int roomCount;
    private int roomId;
    private ArrayList<Room> rooms;
    private ArrayList<Door> allDoors;
    private Room defenderRoom;
    private Room attackerRoom;
    private BufferedReader input;

    Map(String pathName) throws Exception{
        File file = new File(pathName);
        this.input = new BufferedReader(new FileReader(file));

        this.roomCount = 0;
        this.roomId = 0;
        this.rooms = new ArrayList<>();
        this.allDoors = new ArrayList<>();
    }
    // Build the Map through the Map File
    public void buildMap() throws IOException{
        this.name = input.readLine();
        this.roomCount = Integer.parseInt(input.readLine()); 
        int defenderRoom = Integer.parseInt(input.readLine());
        int attackerRoom = Integer.parseInt(input.readLine());
        String[] bombRooms = input.readLine().split(" ");
        int bombRoom1 = Integer.parseInt(bombRooms[0]);
        int bombRoom2 = Integer.parseInt(bombRooms[1]);

        for(int i=0;i<this.roomCount && input.ready();i++){
            String roomColor = input.readLine();
            String obsColor = input.readLine();
            String penObsColor = input.readLine();
            int width = Integer.parseInt(input.readLine());
            int height = Integer.parseInt(input.readLine());
            // Instantiate Obstacles
            int obsCount = Integer.parseInt(input.readLine());
            ArrayList<Obstacle> obstacles = new ArrayList<>();
            for(int j=0;j<obsCount;j++){
                String[] obsLine = input.readLine().split(" ");
                String shape = obsLine[0];
                int obsX = Integer.parseInt(obsLine[1]);
                int obsY = Integer.parseInt(obsLine[2]);
                int obsWidth = Integer.parseInt(obsLine[3]);
                int obsHeight = Integer.parseInt(obsLine[4]);
                boolean permeable = obsLine[5].equals("1");

                obstacles.add(new Obstacle(shape, obsWidth, obsHeight, obsX, obsY, permeable));
            }
            // Instantiate Doors
            int doorCount = Integer.parseInt(input.readLine());
            ArrayList<Door> doors = new ArrayList<>();
            for(int j=0;j<doorCount;j++){
                String[] doorLine = input.readLine().split(" ");
                int doorWidth = Integer.parseInt(doorLine[0]);
                int doorX = Integer.parseInt(doorLine[1]);
                int doorY = Integer.parseInt(doorLine[2]);
                int idToRoom = Integer.parseInt(doorLine[3]);
                int doorId = Integer.parseInt(doorLine[4]);
                int direction = Integer.parseInt(doorLine[5]);
                
                Door door = new Door(doorWidth, doorX, doorY, idToRoom, doorId, direction);
                if(door != null){

                    allDoors.add(door);
                    doors.add(door);
                }
            }
            // Create Room
            Room room;
            if(i == defenderRoom || i == attackerRoom){
                String[] spawns = input.readLine().split(" ");
                int[] spawnLocations = new int[Const.MAX_PLAYER_COUNT];
                for(int k = 0; k < Const.MAX_PLAYER_COUNT; k++){
                    spawnLocations[k] = Integer.parseInt(spawns[k]);
                }
                room = new SpawnRoom(roomId++, width, height, obstacles, doors, spawnLocations);
                if(i == defenderRoom) this.defenderRoom = room; else
                if(i == attackerRoom) this.attackerRoom = room;
            } else if(i == bombRoom1 || i == bombRoom2){
                room = new BombRoom(roomId++, width, height, obstacles, doors);
            } else {
                room = new Room(roomId++, width, height, obstacles, doors);
            }
            rooms.add(room);
            for(Door door: doors){
                door.setThisRoom(room);
            }
        }

        for(Door door: allDoors){
            door.setExit(rooms.get(door.getIdToRoom()).getDoors().get(door.getIdToDoor()));
        }
    }

    public String getName() {
        return this.name;
    }
    public Room getDefenderRoom(){
        return this.defenderRoom;
    }
    public Room getAttackerRoom(){
        return this.attackerRoom;
    }
}
