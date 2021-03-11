package module;

import java.util.BitSet;

public class Transmission {

    BitSet bits;

    int[][] H = {
            {1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0},
            {1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0},
            {1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1}
    };

    public Transmission(String plainText) {

        String bitsAsString = StringToBits(plainText);
        bits = new BitSet(bitsAsString.length());
        for(int i = 0; i < bitsAsString.length(); i++) {
            if (bitsAsString.charAt(i) == '1') {
                bits.set(bitsAsString.length() - i - 1);
            }
        }
        addParityBits(bits);
    }

    public void addParityBits(BitSet bits) {
        StringBuilder sb = new StringBuilder();
        String bitsString = getBitsAsString(bits);
        BitSet newBitSet = new BitSet(bitsString.length() * 2);
        System.out.println(bitsString + " - poczatek");
        int amountOfBits = bitsString.length()/8;
        System.out.println("amountOfBits - " + amountOfBits);
        for(int n = 0; n < amountOfBits; n++) {
//            String oneByte = bitsString.substring(8 * n, 8 * n + 8);
            BitSet oneByte = bits.get(8 * n, 8 * n + 8);

            for(int i = 0; i < 8; i++) {
                newBitSet.set(n * 16 + i, oneByte.get(i));
            }
            System.out.println(getBitsAsString(newBitSet) + " - newBitSet1");
            for(int i = 0; i < 8; i++){
                int rowSum = 0;
                for(int j = 0; j < 8; j++) {
                    int bit = oneByte.get(j)?1:0;
//                    System.out.println(bit + " * " + H[i][j]);
                    rowSum = rowSum + bit * H[i][j];
                }
                int parityBit = rowSum % 2;
                newBitSet.set(8 + (n) * 16 + i, parityBit == 1);
            }
            System.out.println(getBitsAsString(newBitSet) + " - newBitSet2");
        }
        setBits(newBitSet);
        System.out.println();
        System.out.println(getBitsAsString(getBits()));
    }




    public String StringToBits(String plainText) {
        StringBuilder sb = new StringBuilder();
        for(char c : plainText.toCharArray()) {
            sb.append(
                    String.format("%8s", Integer.toBinaryString(c))
                            .replaceAll(" ", "0")
            );
        }
        return sb.toString();
    }

    public BitSet getBits() {
        return bits;
    }

    public String getBitsAsString(BitSet bits) {
        StringBuilder sb = new StringBuilder();
        int counter = bits.length();
        for(int i = 0; i < bits.length(); i++){
            sb.append(bits.get(i)?1:0);
        }
        if(counter % 8 != 0) {
            int r = 8 - (counter % 8);
            sb.append("0".repeat(r));
        }
        return sb.toString();
    }

    public void setBits(BitSet bits) {
        this.bits = bits;
    }

    public void setBitsFromString(String bitsString) {

        bits = new BitSet(bitsString.length());
        for(int i = 0; i < bitsString.length(); i++) {
            if(bitsString.charAt(i) == '1') {
                bits.set(i);
            }
        }
    }

    public void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }
}