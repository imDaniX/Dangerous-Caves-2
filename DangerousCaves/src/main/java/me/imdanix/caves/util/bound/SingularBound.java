package me.imdanix.caves.util.bound;

public record SingularBound(int x, int z) implements Bound {
    @Override
    public boolean isInside(int x, int z) {
        return this.x == x && this.z == z;
    }
}
