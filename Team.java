import java.util.*;

public class Team {
    private int teamId;

    private int maxTeamSize = Const.MAX_TEAM_SIZE;
    private int teamSize;
    private ArrayList<Player> team;
    private HashSet<Agent> agentsSelected;
    private int role; // 0 - defenders, 1 - attackers

    Team(int teamNum){
        this.teamId = teamNum;
        this.teamSize = 0;
        this.team = new ArrayList<>();
        this.agentsSelected = new HashSet<>();
    }

    public int getTeamNum(){
        return this.teamId;
    }
    public int getTeamSize(){
        return this.teamSize;
    }
    public ArrayList<Player> getTeam(){
        return this.team;
    }
    public int getRole(){
        return this.role;
    }
    public Player getPlayer(int index){
        return this.team.get(index);
    }
    public void setRole(int roleNum){
        this.role = roleNum;
    }

    public boolean addAgent(Agent agent){
        if(agentsSelected.contains(agent)){
            return false;
        }
        agentsSelected.add(agent);
        return true;
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
