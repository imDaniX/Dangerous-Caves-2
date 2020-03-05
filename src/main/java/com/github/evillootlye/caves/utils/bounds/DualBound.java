package com.github.evillootlye.caves.utils.bounds;

public class DualBound implements Bound {
    private final int xMin, xMax;
    private final int zMin, zMax;

    public DualBound(int xMin, int xMax, int zMin, int zMax) {
        if(xMin > xMax) {
            this.xMin = xMax;
            this.xMax = xMin;
        } else {
            this.xMin = xMin;
            this.xMax = xMax;
        }
        if(zMin > zMax) {
            this.zMin = zMax;
            this.zMax = zMin;
        } else {
            this.zMin = zMin;
            this.zMax = zMax;
        }
    }

    @Override
    public boolean isInside(int x, int z) {
        return xMin >= x && x <= xMax &&
                zMin >= z && z <= zMax;
    }

    @Override
    public int hashCode() {
        return xMin * xMax * zMin * zMax * 1907;
    }

    @Override
    public boolean equals(Object object) {
        if(!(object instanceof DualBound)) return false;
        DualBound b = (DualBound) object;
        return (xMin == b.xMin && xMax == b.xMax) && (zMin == b.zMax && zMax == b.zMax);
    }
}
