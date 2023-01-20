public class Gun {
    public GunModel model;
    private Timer gunCooldown;
    private int ammo;
    private int preReloadAmmo;
    private boolean active;

    Gun(String type, int ammo) {
        this.model = GunModel.valueOf(type);
        this.ammo = ammo;
        this.preReloadAmmo = this.ammo;
        this.active = false;
    }
    
    public boolean ready(){
        boolean ready = gunCooldown.finished();
        if(ready) this.preReloadAmmo = this.ammo;
        return ready;
    }

    public double fire(){
        if(this.ammo > 0 && this.ready()) {
            this.ammo--;
            this.preReloadAmmo--;

            gunCooldown.setTimerLength(1/(model.getFireRate()));
            gunCooldown.start();
            return 1/(model.getFireRate());
        }
        return 0;
    }
    public double reload(){
        if(this.ammo < model.getMaxAmmo() && this.ready()) {
            this.ammo = model.getMaxAmmo();

            gunCooldown.setTimerLength(model.getReloadSpeed());
            gunCooldown.start();
            return (model.getReloadSpeed());
        }
        return 0;
    }
    public double takeOut(){
        gunCooldown.setTimerLength(1/(model.getFireRate()/3));
        gunCooldown.start();
        return (model.getFireRate()/3);
    }
    
    public int getAmmo() {
        return this.ammo; 
    }
    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }
    public boolean isActive() {
        return this.active;
    }
    public void setActive(boolean active) {
        this.active = active;
        if(!this.active && !this.ready()){
            this.ammo = this.preReloadAmmo;
        }
    }
}