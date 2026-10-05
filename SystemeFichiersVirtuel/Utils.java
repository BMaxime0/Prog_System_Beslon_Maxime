public class Utils {

    public static int writeInt(byte[] memory, int offset, int value) {
        memory[offset]= (byte)((value >> 24) & 0xFF);
        memory[offset+1]= (byte)((value >> 16) & 0xFF);
        memory[offset+2]= (byte)((value >> 8) & 0xFF);
        memory[offset+3]= (byte)(value & 0xFF);
        return 4;
    }

    public static int readInt(byte[] memory, int offset) {
        int value = memory[offset] & 0xFF;
        value = (value << 8) | (memory[offset + 1] & 0xFF);
        value = (value << 8) | (memory[offset + 2] & 0xFF);
        value = (value << 8) | (memory[offset + 3] & 0xFF);
        return value;
    }

    public static int writeShort(byte[] memory, int offset, short value) {
        memory[offset]= (byte)((value >> 8) & 0xFF);
        memory[offset+1]= (byte)(value & 0xFF);
        return 2;
    }

    public static short readShort(byte[] memory, int offset) {
        int value = memory[offset] & 0xFF;
        value = (value << 8) | (memory[offset + 1] & 0xFF);
        return (short) value;
    }

    public static int writeLong(byte[] memory, int offset, long value) {
        for (int i = 7; i >= 0; i--) {
            memory[offset + (7 - i)] = (byte) ((value >> (i * 8)) & 0xFF);
        }
        return 8;
    }

    public static long readLong(byte[] memory, int offset) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value = (value << 8) | (memory[offset + i] & 0xFF);
        }
        return value;
    }

    public static int writeString(
            byte[] memory,
            int offset,
            String str,
            int maxLength) {

        byte[] bytes = str.getBytes();
        int length = Math.min(bytes.length, maxLength);

        for (int i = 0; i < length; i++) { 
            memory[offset + i] = bytes[i]; 
        }
        for (int i = length; i < maxLength; i++) {
            memory[offset + i] = 0; 
        }

        return maxLength;
    }

    public static String readString(
            byte[] memory,
            int offset,
            int maxLength) {

        int length = 0;
        while (length < maxLength && memory[offset + length] != 0) {
            length++; 
        }
        return new String(memory, offset, length);
    }   
}