package me.imdanix.caves.util.bound;

public interface Bound {
    boolean isInside(int x, int z);

    static Bound fromString(String boundStr) {
        String[] xzStr = boundStr.split(" ");
        String[] firstCoords = xzStr[0].split(",");
        if (xzStr.length > 1) {
            String[] secondCoords = xzStr[1].split(",");
            try {
                return new DualBound(
                        Integer.parseInt(firstCoords[0]), Integer.parseInt(secondCoords[0]),
                        Integer.parseInt(firstCoords[1]), Integer.parseInt(secondCoords[1])
                );
            } catch (NumberFormatException ignored) {}
        } else {
            try {
                return new SingularBound(
                        Integer.parseInt(firstCoords[0]), Integer.parseInt(firstCoords[1])
                );
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}
