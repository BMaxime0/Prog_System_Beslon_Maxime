public class Inode {

    private MemoryManager memoryManager;
    private int inodeNumber;

    public static final int INODE_SIZE = 128;
    public static final int DIRECT_POINTERS = 10;

    public Inode(
            MemoryManager memoryManager,
            int inodeNumber) {

        this.memoryManager = memoryManager;
        this.inodeNumber = inodeNumber;
    }

    public int getInodeOffset() {
        return memoryManager.INODE_TABLE_OFFSET + inodeNumber * INODE_SIZE;
    }

    public int getFileType() {
        return Utils.readInt(memoryManager.getFilesystemMemory(), getInodeOffset() + 4);
    }

    public int getFileSize() {
        return Utils.readInt(memoryManager.getFilesystemMemory(), getInodeOffset() + 8);
    }

    public int[] getDirectPointers() {

        byte[] memory =
                memoryManager.getFilesystemMemory();

        int[] pointers =
                new int[DIRECT_POINTERS];

        for (int i = 0 ; i < 10 ; i++) {
            pointers[i] = Utils.readInt(memoryManager.getFilesystemMemory(), getInodeOffset() + 28 + i*4);
        }

        return pointers;
    }
}
