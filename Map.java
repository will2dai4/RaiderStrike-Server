import java.util.ArrayList;
import java.util.HashSet;
import java.io.*;

public class Map {
    private String name;
    private int roomNumber;
    private ArrayList<Room> rooms;
    private BufferedReader input;

    Map(File mapFile) throws Exception{
        input = new BufferedReader(new FileReader(mapFile));
    }
    public void buildMap() throws IOException{
        this.name = input.readLine();
        this.roomNumber = Integer.parseInt(input.readLine());
        HashSet<Door> doorList = new HashSet<>();
        for(int i=0;i<roomNumber;i++){
            boolean isSpawn = (Integer.parseInt(input.readLine()) == 1);
            int width = Integer.parseInt(input.readLine());
            int height = Integer.parseInt(input.readLine());
            int[] spawnLocations = new int[Const.MAX_PLAYER_COUNT];
            if(isSpawn){
                for(int j=0;j<spawnLocations.length;j++){spawnLocations[j] = Integer.parseInt(input.readLine());}
            }

            int numObstacles = Integer.parseInt(input.readLine());
            ArrayList<Obstacle> obstacles = new ArrayList<>();
            for(int j=0;j<numObstacles;j++){
                int obsX = Integer.parseInt(input.readLine());
                int obsY = Integer.parseInt(input.readLine());
                int obsWidth = Integer.parseInt(input.readLine());
                int obsHeight = Integer.parseInt(input.readLine());
                boolean permeable = (Integer.parseInt(input.readLine()) == 1);
                obstacles.add(new Obstacle(obsX, obsY, obsWidth, obsHeight, permeable));
            }

            int numDoors = Integer.parseInt(input.readLine());
            ArrayList<Door> doors = new ArrayList<>();
            for(int j=0;j<numDoors;j++){
                /* TODO: Create doors and with all parameters (wait for room dimensions) */
            }
            rooms.add(new Room(width, height, obstacles, doors));
        }
        /* TODO: Connect doors between rooms (use diagram as reference) */
    }
}
