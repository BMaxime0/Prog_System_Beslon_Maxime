public class Utils {

    public static int writeInt(byte[] memory, int offset, int value) {
        // TODO: Écrire les 4 octets de 'value' dans 'memory'
        // à partir de 'offset', en big-endian.

        memory[offset + 3] = (byte) (value & 0xFF);
        memory[offset + 2] = (byte) (value >> 8 & 0xFF);
        memory[offset + 1] = (byte) (value >> 16 & 0xFF);
        memory[offset] = (byte) (value >> 24 & 0xFF);

        return 4;
    }

    public static int readInt(byte[] memory, int offset) {

        int F0 = (int) memory[offset];
        int A1 = (int) memory[offset + 1] << 8;
        int B2 = (int) memory[offset + 2] << 16;
        int C3 = (int) memory[offset + 3] << 24;

        return F0 + A1 + B2 + C3;
    }

    public static int writeShort(byte[] memory, int offset, short value) {
        
        memory[offset + 1] = (byte) (value & 0xFF);
        memory[offset] = (byte) (value >> 8 & 0xFF);

        return 2;
    }

    public static short readShort(byte[] memory, int offset) {
        
        short F0 = (short) memory[offset];
        short A1 = (short) memory[offset+1] << 8;

        return F0 + A1;
    }
}