public enum GunModel {
    /* TODO: implement gun models */;
    // Name of enum constant can be acquired using .toString() method
    private final int maxAmmo;
    private final double reloadSpeed;
    private final double fireRate;

    private GunModel(int maxAmmo, double reloadSpeed, double fireRate) {
        this.maxAmmo = maxAmmo;
        this.reloadSpeed = reloadSpeed;
        this.fireRate = fireRate;
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
}