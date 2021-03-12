package module;

import java.util.BitSet;

public class Transmission {

    BitSet bits; //BitSet do przechowywania wiadomosci w bitach
    //Macierz H, wiersze niezależne liniowo
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
    //Konstruktor przyjmujący tekst i wczytujący go do zmiennej bits
    //Argument addParityBits określa czy mają zostać obliczone bity parzystosc
    public Transmission(String plainText, boolean addParityBits) {
        String bitsAsString = StringToBits(plainText); //bity zapisane jako string
        bits = new BitSet(bitsAsString.length());   //tworzymy BitSet o dlugosc string
        for(int i = 0; i < bitsAsString.length(); i++) {
            if (bitsAsString.charAt(i) == '1') {
                bits.set(bitsAsString.length() - i - 1); //jeżeli bit jest 1 to ustaw (od konca)
            }
        }
        if(addParityBits) {
            addParityBits(bits); //dodawnie bitow parzystosci
        }
    }

    public void addParityBits(BitSet bits) {
        String bitsString = getBitsAsString(bits);
        BitSet newBitSet = new BitSet(bitsString.length() * 2); //miejsca na 8bit wiadomosci i 8bit parzystosci
        int amountOfBytes = bitsString.length()/8; //ilosc bajtow w wiadomosc
        for(int n = 0; n < amountOfBytes; n++) { //iteracja przez kazdy bajt
            BitSet oneByte = bits.get(8 * n, 8 * n + 8); //wyciagniecie bajtu z wiadomosci
            for(int i = 0; i < 8; i++) {
                newBitSet.set(n * 16 + i, oneByte.get(i)); //dodanie do zmiennej 16 bitowej, pierwszych 8 bitow wiadomosci
            }
            for(int i = 0; i < 8; i++){ // mnożenie 8bit wiadomosci przez macierz H
                int rowSum = 0; // suma jedynek w wierszu
                for(int j = 0; j < 8; j++) {
                    int bit = oneByte.get(j)?1:0; // wyciegniecie wartosci bita
                    rowSum = rowSum + bit * H[i][j]; // sumowanie jedynek w wierszu
                }
                int parityBit = rowSum % 2; // obliczanie bitu parzystosci z konkretnego wiersza
                newBitSet.set(8 + (n) * 16 + i, parityBit == 1); // ustawienie bita parzystosci
            }
        }
    }

    public void correctBits() { // wykrywanie i korygowanie bledow transmisji
        String bitsString = getBitsAsString(bits);
        int amountOfBytes = bitsString.length()/16; //liczba 16bitowych partii (8bit wiadomosc + 8bit parzystosci)
        for(int n = 0; n < amountOfBytes; n++) {
            BitSet newBitSet = new BitSet(8); // miejsce na wektor bledu
            BitSet twoByte = bits.get(16 * n, 16 * n + 16); // wyciągnięcie dwóch bajtów (wiadomosc + parzystosci)
            for(int i = 0; i < 8; i++) { // mnożenie 16bit przez macierz H w celu znalezienia wektora błędu
                int rowSum = 0; // zmienna do sumowania jedynek w wierszu
                for(int j = 0; j < 16; j++) {
                    int bit = twoByte.get(j)?1:0;
                    rowSum = rowSum + bit * H[i][j]; // sumowanie jedynek w wierszu
                }
                int bit = rowSum % 2; // suma jednyek modulo 2
                newBitSet.set(i, bit == 1);// ustawianie obliczonego bitu
            }
            if(newBitSet.length() == 0) { // jeżeli wektor błędu == 0 to brak błędu
                continue;
            }

            for (int j = 0; j < 16; j++) { // szukanie kolumny pasującej do wektora błedu
                BitSet column = new BitSet(8);
                for (int i = 0; i < 8; i++) {
                    column.set(i, H[i][j] == 1);
                }
                if(column.equals(newBitSet)) { // jezeli kolumna == wektor bledu
                    System.out.println("Blad na indeksie " + (j + (n * 16))); // wypisz indeks
                    boolean errorBit = bits.get(j + (n * 16)); // sprawdz jaki bit wystepuje na tym indeksie
                    bits.set(j + (n * 16), !errorBit); // zmien bit na przeciwny
                }
            }

            for (int j = 0; j < 16; j++) { // szukanie sumy dwoch kolumn pasujacej do wektora bledu
                for (int j2 = j + 1; j2 < 16; j2++) {
                    BitSet column1 = new BitSet(8); // kolumna 1
                    BitSet column2 = new BitSet(8); // kolumna 2
                    for (int i = 0; i < 8; i++) {
                        column1.set(i, H[i][j] == 1);
                        column2.set(i, H[i][j2] == 1);
                    }
                    column1.xor(column2); // xor kolumn
                    if(column1.equals(newBitSet)) { // jezeli kolumna1 + kolumna2 == wektor bledu
                        System.out.println("Blad na indeksach " + (j + (n*16)) + " " + (j2 + (n*16)));
                        boolean errorBit1 = bits.get(j + (n * 16)); // bit na indeksie 1 kolumny
                        boolean errorBit2 = bits.get(j2 + (n * 16));// bit na indeksie 2 kolumny
                        bits.set(j + (n * 16), !errorBit1); // zamiana bitu
                        bits.set(j2 + (n * 16), !errorBit2);// zamina bitu
                    }
                }
            }
        }
        System.out.println(getBitsAsString(bits));
    }

    public void delParityBits() { // usuwanie bitow parzystosci w celu zapisu bitow do pliku
        String bitsString = getBitsAsString(bits);
        BitSet newBitSet = new BitSet(bitsString.length() / 2); // nowy BitSet o polowe krotszy
        System.out.println(bitsString + " - poczatek");
        int amountOfBytes = bitsString.length()/16; // liczba 16bitow (wiadomosc + parzystosci)
        for (int i = 0; i < amountOfBytes; i++) {
            for (int j = 0; j < 8; j++) { // ustawienie tylko 8bitow wiadomosci, pominiecie bitow parzystosci
                newBitSet.set(j + (i * 8), bits.get(j + (i * 16)));
            }
        }
        System.out.println(getBitsAsString(newBitSet) + " - po usunieciu parzystosci");
        setBits(newBitSet); // ustawienie nowego BitSetu
    }

    public String StringToBits(String plainText) { // Zamiana tekstu na kod binarny w stringu
        StringBuilder sb = new StringBuilder();
        for(char c : plainText.toCharArray()) {
            sb.append(
                    String.format("%8s", Integer.toBinaryString(c))
                            .replaceAll(" ", "0")
            );
        }
        return sb.toString();
    }

    public BitSet getBits() { // zwraca BitSet
        return bits;
    }

    public String getBitsAsString(BitSet bits) { // Zamiana tekstu na kod binarny w stringu
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

    public void setBits(BitSet bits) { // ustawia nowy BitSet
        this.bits = bits;
    }

    public void setBitsFromString(String bitsString) { // ustawia BitSet z przekazanego stringa

        bits = new BitSet(bitsString.length());
        for(int i = 0; i < bitsString.length(); i++) {
            if(bitsString.charAt(i) == '1') {
                bits.set(i);
            }
        }
    }

    public void reverse(byte[] array) { // odwraca tablice bajtow
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
