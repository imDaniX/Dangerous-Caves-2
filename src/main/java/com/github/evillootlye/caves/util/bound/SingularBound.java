package com.github.evillootlye.caves.util.bound;

public class SingularBound implements Bound {
    private final int x;
    private final int z;

    public SingularBound(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean isInside(int x, int z) {
        return this.x == x && this.z == z;
    }

    @Override
    public int hashCode() {
        return (x >>> 15) * (z >>> 31) * 3343;
    }

    @Override
    public boolean equals(Object object) {
        if(!(object instanceof SingularBound)) return false;
        SingularBound b = (SingularBound) object;
        return x == b.x && z == b.z;
    }
}
