public enum GunModel {
    // Sidearms
    Robin(0, 25, 10, 2, 7, 10, false),
    Duck(400, 10, 2, 2, 3, 60, false),
    Finch(400, 30, 15, 2, 7, 8, false),
    Hummingbird(600, 25, 12, 2, 10, 10, true),
    Raven(600, 50, 8, 2.5, 2, 4, false),
    // Primary
    // Light Machine Guns
    Pecker(1000, 20, 20, 2.5, 15, 15, true),
    Swift(1400, 25, 25, 3, 13, 12, true),
    // Rifles
    Crane(2000, 60, 15, 3, 3, 4, false),
    Eagle(2500, 40, 25, 3, 8, 5, true),
    // Shotguns
    Peacock(1200, 15, 8, 3.5, 1, 40, false),
    Turkey(1300, 15, 8, 3.5, 1.5, 20, false),
    // Snipers
    Vulture(1000, 100, 8, 3, 1.5, 2, false),
    Falcon(4000, 150, 1, 2, 1, 1, false),
    // Heavy Machine Guns
    Rhea(2300, 25, 50, 4, 12, 15, true);

    private final int price;
    private final int damage;
    private final int maxAmmo;
    private final double reloadSpeed;
    private final double fireRate;
    private final double fireError;
    private final boolean semiAuto;

    private GunModel(int price, int damage, int maxAmmo, double reloadSpeed, double fireRate, double fireError, boolean semiAuto) {
        this.price = price;
        this.damage = damage;
        this.maxAmmo = maxAmmo;
        this.reloadSpeed = reloadSpeed;
        this.fireRate = fireRate;
        this.fireError = fireError;
        this.semiAuto = semiAuto;
    }

    public int getPrice(){
        return this.price;
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
    public boolean getSemiAuto(){
        return this.semiAuto;
    }
}