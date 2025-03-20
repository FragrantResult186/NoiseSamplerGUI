package fragrant.components.mapviewer.model;

public record ChunkKey(int x, int y, int z) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkKey key)) return false;
        return x == key.x && y == key.y && z == key.z;
    }

}
