import java.util.ArrayList;
import java.io.*;

public class Map {
    private final String name;
    private final int roomNumber;
    private ArrayList<Room> rooms;
    private BufferedReader input;

    Map(File mapFile) throws Exception{
        input = new BufferedReader(new FileReader(mapFile));
        this.name = input.readLine();
        this.roomNumber = Integer.parseInt(input.readLine());
    }
}
