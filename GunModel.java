public enum GunModel {
    /* TODO: implement gun models */;
    // Name of enum constant can be acquired using .toString() method
    private final int damage;
    private final int maxAmmo;
    private final double reloadSpeed;
    private final double fireRate;
    private final double fireError;

    private GunModel(int damage, int maxAmmo, double reloadSpeed, double fireRate, double fireError) {
        this.damage = damage;
        this.maxAmmo = maxAmmo;
        this.reloadSpeed = reloadSpeed;
        this.fireRate = fireRate;
        this.fireError = fireError;
    }

    public int getDamage(){
        return this.damage;
    }
    public int getMaxAmmo() {
        return this.maxAmmo;
    }
    public double getReloadSpeed() {
        return this.reloadSpeed;
    }
    public double getFireRate() {
        return this.fireRate;
    }
    public double getFireError(){
        return this.fireError;
    }
}