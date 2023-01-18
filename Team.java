import java.util.*;

public class Team {
    private int maxTeamSize = Const.MAX_TEAM_SIZE;
    private int teamSize;
    private ArrayList<Player> team;

    Team(){
        this.teamSize = 0;
        this.team = new ArrayList<>();
    }

    public int getTeamSize(){
        return this.teamSize;
    }

    public boolean addPlayer(Player player){
        if(this.teamSize < this.maxTeamSize){
            team.add(player);
            teamSize++;
            return true;
        }
        return false;
    }
    public void removePlayer(Player player){
        team.remove(player);
        teamSize--;
    }
}
