import java.util.*;

public class VirtualFileSystem {

    private MemoryManager memoryManager;

    public VirtualFileSystem() {
        this.memoryManager = new MemoryManager();
    }

    private int allocateInode() {

        byte[] memory =
                memoryManager.getFilesystemMemory();

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
}