import java.util.ArrayList;
import java.util.HashSet;
import java.io.*;

public class Map {
    private String name;
    private int roomCount;
    private ArrayList<Room> rooms;
    private BufferedReader input;

    Map(File mapFile) throws Exception{
        input = new BufferedReader(new FileReader(mapFile));
    }
    public void buildMap() throws IOException{
        int roomCount = 0;

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
                String shape = input.readLine();
                int obsWidth = Integer.parseInt(input.readLine());
                int obsHeight = Integer.parseInt(input.readLine());
                int obsX = Integer.parseInt(input.readLine());
                int obsY = Integer.parseInt(input.readLine());
                boolean permeable = (Integer.parseInt(input.readLine()) == 1);
                obstacles.add(new Obstacle(shape, obsWidth, obsHeight, obsX, obsY, permeable));
            }

            int doorCount = Integer.parseInt(input.readLine());
            ArrayList<Door> doors = new ArrayList<>();
            for(int j=0;j<doorCount;j++){
                /* TODO: figure out door problem: how door leads to room through another door */
            }
        }
    }
}
