package me.imdanix.caves.util.bound;

public record SingularBound(int x, int z) implements Bound {
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
        if (!(object instanceof SingularBound b)) return false;
        return x == b.x && z == b.z;
    }
}
