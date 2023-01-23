public class Round {
    Timer roundTimer;
    private Team defendTeam;
    private Team attackTeam;

    Round(int defendTeam, int attackTeam){
        this.roundTimer = new Timer();
        this.defendTeam = new Team(defendTeam);
        this.attackTeam = new Team(attackTeam);
    }
    public void start(){
        this.roundTimer.setTimerLength(Const.BUY_PERIOD_TIME);
        this.roundTimer.start();
    }
    public void finishBuy(){
        this.roundTimer.setTimerLength(Const.ROUND_PERIOD_TIME);
        this.roundTimer.start();
    }
}
