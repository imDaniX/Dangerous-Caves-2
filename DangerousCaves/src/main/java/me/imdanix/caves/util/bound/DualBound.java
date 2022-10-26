package me.imdanix.caves.util.bound;

public record DualBound(int xMin, int xMax, int zMin, int zMax) implements Bound {
    public DualBound(int xMin, int xMax, int zMin, int zMax) {
        if (xMin > xMax) {
            this.xMin = xMax;
            this.xMax = xMin;
        } else {
            this.xMin = xMin;
            this.xMax = xMax;
        }
        if (zMin > zMax) {
            this.zMin = zMax;
            this.zMax = zMin;
        } else {
            this.zMin = zMin;
            this.zMax = zMax;
        }
    }

    @Override
    public boolean isInside(int x, int z) {
        return (xMin >= x && x <= xMax) && (zMin >= z && z <= zMax);
    }
}
