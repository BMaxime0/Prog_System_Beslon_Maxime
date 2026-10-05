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

    public void writeToMemory(
        int fileType,
        int fileSize,
        long creationTime,
        long modificationTime,
        int[] directPointers,
        int indirectPointer,
        short permissions,
        int linkCount) {

    if (directPointers == null || directPointers.length != 10) {
        throw new IllegalArgumentException(
                "Il faut exactement 10 pointeurs directs");
    }

    byte[] memory = memoryManager.getFilesystemMemory();

    int offset = getInodeOffset();   // curseur

    // 1. Numéro d'inode (4 octets)
    Utils.writeInt(memory, offset, inodeNumber);
    offset += 4;

    // 2. Type (4 octets)
    Utils.writeInt(memory, offset, fileType);
    offset += 4;

    // 3. Taille (4 octets)
    Utils.writeInt(memory, offset, fileSize);
    offset += 4;

    // 4. Création (8 octets)
    Utils.writeLong(memory, offset, creationTime);
    offset += 8;

    // 5. Modification (8 octets)
    Utils.writeLong(memory, offset, modificationTime);
    offset += 8;

    // 6. 10 pointeurs directs (10 x 4 octets)
    for (int i = 0; i < 10; i++) {
        Utils.writeInt(memory, offset, directPointers[i]);
        offset += 4;
    }

    // 7. Pointeur indirect (4 octets)
    Utils.writeInt(memory, offset, indirectPointer);
    offset += 4;

    // 8. Permissions (2 octets)
    Utils.writeShort(memory, offset, permissions);
    offset += 2;

    // 9. Nombre de liens (4 octets)
    Utils.writeInt(memory, offset, linkCount);
    offset += 4;
}
}
