import java.util.*;

public class Team {
    private int teamSize;
    private ArrayList<Player> team;

    Team(){
        this.teamSize = 0;
        this.team = new ArrayList<>();
    }

    public int getTeamSize(){
        return this.teamSize;
    }

    public void addPlayer(Player player){
        team.add(player);
        teamSize++;
    }
    public void removePlayer(Player player){
        team.remove(player);
        teamSize--;
    }
}
