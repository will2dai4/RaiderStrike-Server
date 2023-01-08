public enum Agent {
    /* TODO: implement agents */;
    private final int agentId;
    private final int basicAbility; // ability Id
    private final int ultimateAbility; // ability Id

    Agent(int agentId, String imagePath, int basicAbility, int ultimateAbility) {
        this.agentId = agentId;
        this.basicAbility = basicAbility;
        this.ultimateAbility = ultimateAbility;
    }

    public int getAgentId(){
        return this.agentId;
    }
    public int getBasicAbility(){
        return this.basicAbility;
    }
    public int getUltimateAbility(){
        return this.ultimateAbility;
    }
}