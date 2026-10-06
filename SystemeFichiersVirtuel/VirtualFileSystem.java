import java.util.*;

public class VirtualFileSystem {

    private MemoryManager memoryManager;

    public VirtualFileSystem() {
        this.memoryManager = new MemoryManager();
    }

    private int allocateInode() {

        int MAX_INODES = MemoryManager.INODE_TABLE_SIZE / MemoryManager.INODE_SIZE;

        // Parcours de la table : le premier inode dont le type vaut 0 est libre
        for (int i = 0; i < MAX_INODES; i++) {
            Inode inode = new Inode(memoryManager, i);
            if (inode.getFileType() == 0) {
                return i;
            }
        }

        return -1; // table pleine
    }

    public boolean createFile(
            String directory,
            String filename) {

        int inodeNum = allocateInode();

        if (inodeNum == -1) {
            return false;
        }

        Inode inode = new Inode(memoryManager, inodeNum);

        long now = System.currentTimeMillis();

        inode.writeToMemory(
                1,                // type : fichier
                0,                // taille : fichier vide
                now,              // création
                now,              // modification
                new int[10],      // aucun bloc de données
                0,                // pas de pointeur indirect
                (short) 0644,     // permissions par défaut
                1);               // un seul lien

        return true;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }

    public boolean writeFile(int inodeNum, byte[] data) {
        int blocksNeeded =
                (data.length + MemoryManager.BLOCK_SIZE - 1)
                / MemoryManager.BLOCK_SIZE;

        if (blocksNeeded > Inode.DIRECT_POINTERS) {
            return false;
        }

        int[] blockPointers = new int[Inode.DIRECT_POINTERS];

        // Allocation des blocs, avec rollback si la mémoire est pleine
        for (int i = 0; i < blocksNeeded; i++) {
            int block = memoryManager.allocateBlock();
            if (block == -1) {
                for (int j = 0; j < i; j++) {
                    memoryManager.setBlockUsed(blockPointers[j], false);
                }
                return false;
            }
            blockPointers[i] = block;
        }

        byte[] memory = memoryManager.getFilesystemMemory();

        int bytesRemaining = data.length;
        int dataSrcOffset = 0;

        for (int i = 0; i < blocksNeeded; i++) {
            int toCopy = Math.min(bytesRemaining, MemoryManager.BLOCK_SIZE);
            int blockOffset = blockPointers[i] * MemoryManager.BLOCK_SIZE;

            System.arraycopy(data, dataSrcOffset, memory, blockOffset, toCopy);

            dataSrcOffset += toCopy;
            bytesRemaining -= toCopy;
        }

        Inode inode = new Inode(memoryManager, inodeNum);

        // Réécriture : on libère les anciens blocs une fois les nouveaux écrits
        int[] oldPointers = inode.getDirectPointers();
        int oldBlocks = (inode.getFileSize() + MemoryManager.BLOCK_SIZE - 1)
                        / MemoryManager.BLOCK_SIZE;
        for (int i = 0; i < oldBlocks; i++) {
            memoryManager.setBlockUsed(oldPointers[i], false);
        }

        inode.writeToMemory(
                inode.getFileType(),
                data.length,
                inode.getCreationTime(),
                System.currentTimeMillis(),
                blockPointers,
                inode.getIndirectPointer(),
                inode.getPermissions(),
                inode.getLinkCount());

        return true;
    }

    public byte[] readFile(int inodeNum) {

        Inode inode = new Inode(memoryManager, inodeNum);

        int fileSize = inode.getFileSize();

        if (fileSize == 0) {
            return new byte[0];
        }

        byte[] fileData = new byte[fileSize];
        byte[] memory = memoryManager.getFilesystemMemory();
        int[] blockPointers = inode.getDirectPointers();

        int blocksUsed =
                (fileSize + MemoryManager.BLOCK_SIZE - 1)
                / MemoryManager.BLOCK_SIZE;

        int bytesRemaining = fileSize;
        int destOffset = 0;

        for (int i = 0; i < blocksUsed; i++) {
            int toCopy = Math.min(bytesRemaining, MemoryManager.BLOCK_SIZE);
            int blockOffset = blockPointers[i] * MemoryManager.BLOCK_SIZE;

            System.arraycopy(memory, blockOffset, fileData, destOffset, toCopy);

            destOffset += toCopy;
            bytesRemaining -= toCopy;
        }

        return fileData;
    }

    public boolean deleteFile(int inodeNum) {

        if (inodeNum < 0 || inodeNum >= MemoryManager.MAX_INODES) {
            return false;
        }

        Inode inode = new Inode(memoryManager, inodeNum);

        if (inode.getFileType() == 0) {
            return false;   // inode déjà libre : rien à supprimer
        }

        // 1. Libérer les blocs de données dans le bitmap
        int[] pointers = inode.getDirectPointers();
        int blocksUsed =
                (inode.getFileSize() + MemoryManager.BLOCK_SIZE - 1)
                / MemoryManager.BLOCK_SIZE;

        for (int i = 0; i < blocksUsed; i++) {
            memoryManager.setBlockUsed(pointers[i], false);
        }

        // 2. Invalider l'inode (type = 0 => libre, taille et pointeurs remis à zéro)
        inode.writeToMemory(
                0,              // type : libre
                0,              // taille
                0L,             // création
                0L,             // modification
                new int[10],    // plus aucun bloc
                0,              // pas de pointeur indirect
                (short) 0,      // permissions
                0);             // liens

        return true;
    }

}