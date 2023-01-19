import java.util.ArrayList;
import java.util.HashSet;
import java.io.*;

public class Map {
    private String name;
    private int roomCount;
    private ArrayList<Room> rooms;
    private BufferedReader input;

    Map(String pathName) throws Exception{
        File file = new File(pathName);
        this.input = new BufferedReader(new FileReader(file));

        this.roomCount = 0;
        this.rooms = new ArrayList<>();
    }
    public void buildMap() throws IOException{
        this.name = input.readLine();
        this.roomCount = Integer.parseInt(input.readLine());
        int defenderRoom = Integer.parseInt(input.readLine());
        int attackerRoom = Integer.parseInt(input.readLine());
        String[] bombRooms = input.readLine().split(" ");
        int bombRoom1 = Integer.parseInt(bombRooms[0]);
        int bombRoom2 = Integer.parseInt(bombRooms[1]);

        for(int i=0;i<this.roomCount;i++){
            String roomColor = input.readLine();
            String obsColor = input.readLine();
            String penObsColor = input.readLine();
            int width = Integer.parseInt(input.readLine());
            int height = Integer.parseInt(input.readLine());

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
                
                doors.add(new Door(doorWidth, doorX, doorY, idToRoom, doorId, direction));
            }

            Room room;
            if(i == defenderRoom || i == attackerRoom){
                String[] spawns = input.readLine().split(" ");
                int[] spawnLocations = new int[Const.MAX_PLAYER_COUNT];
                for(int k = 0; k < Const.MAX_PLAYER_COUNT; k++){
                    spawnLocations[k] = Integer.parseInt(spawns[k]);
                }
                room = new SpawnRoom(roomCount++, width, height, obstacles, doors, spawnLocations);
            } else {
                room = new Room(roomCount++, width, height, obstacles, doors);
                if(room.getId() == bombRoom1 || room.getId() == bombRoom2){
                    room.setBombRoom();
                }
            }
            rooms.add(room);
        }
    }
}
