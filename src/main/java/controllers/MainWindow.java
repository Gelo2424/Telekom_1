package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import module.Transmission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;

public class MainWindow {

    private final FileChooser fileChooser = new FileChooser(); //zmienna do wczytywania/zapisywania plikow
    private Transmission transmission; //klasa slużąca do operacji na bitach transmisji

    @FXML
    public Button savePlainTextButton; //zapis tekstu jawnego
    @FXML
    public Button checkBitsButton; //korygowanie bledu
    @FXML
    public Button saveBitsButton; //zapis bitow
    @FXML
    public Label isPlainTextReadedLabel;
    @FXML
    public Label isBitsFileReadedLabel;
    @FXML
    public TextArea bitsTextField; //pole z bitami
    @FXML
    public Button saveBitsButton1; //zapisz bity w pamięci


    public void initialize() {
        savePlainTextButton.setDisable(true);
        checkBitsButton.setDisable(true);
    }


    public void readPlainText(ActionEvent actionEvent) throws IOException {
        fileChooser.setTitle("Wybierz plik z tekstem jawnym");
        File file = fileChooser.showOpenDialog(null); // okno do wyboru pliku
        if(file != null) { // jezeli wybrano plik
            isPlainTextReadedLabel.setText("Plik został wczytany");
            isPlainTextReadedLabel.setStyle("-fx-text-fill: green");
            isBitsFileReadedLabel.setText("Wiadomosc w pamięci");
            isBitsFileReadedLabel.setStyle("-fx-text-fill: green");
            saveBitsButton.setDisable(false);
            checkBitsButton.setDisable(false);
            String content = Files.readString(Path.of(file.getPath()), StandardCharsets.US_ASCII); //wczytanie tekstu
            transmission = new Transmission(content, true); // ustawiamy bity transmisji
            bitsTextField.setText(transmission.getBitsAsString(transmission.getBits())); // wyswietlamy bity w polu
        }
    }

    public void checkBits(ActionEvent actionEvent) {
        savePlainTextButton.setDisable(false);
        transmission.correctBits(); // metoda to wykrywania i korygowania bledow transmisji
    }

    public void saveBits(ActionEvent actionEvent) {
        String bitsString = bitsTextField.getText();
        transmission.setBitsFromString(bitsString); // zapis bitow z pola tekstowego
    }

//    public void readBits(ActionEvent actionEvent) throws IOException {
//        fileChooser.setTitle("Wybierz plik z bitami");
//        File file = fileChooser.showOpenDialog(null);
//        if(file != null) {
//            isBitsFileReadedLabel.setText("Plik został wczytany");
//            isBitsFileReadedLabel.setStyle("-fx-text-fill: green");
//            checkBitsButton.setDisable(false);
//            byte[] message = Files.readAllBytes(Path.of(file.getPath()));
//            String mess = new String(message);
//            System.out.println(mess);
//            transmission = new Transmission(mess, false);
//            System.out.println(transmission.StringToBits(mess));
//        }
//    }

    public void savePlainText(ActionEvent actionEvent) { //zapis tekstu po korekcji
        transmission.delParityBits(); // usywanie bitow parzystosci
        fileChooser.setTitle("Wybierz plik do zapisu bitow");
        File file = fileChooser.showSaveDialog(null);
        if(file != null) {
            BitSet bits = transmission.getBits(); // pobieranie bitow z transmisji
            byte[] bytes = bits.toByteArray(); //  bity do tablicy bajtow
            transmission.reverse(bytes); // odwrocenie tablicy bajtow
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(bytes); // zapis bajtow do pliku
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveBitsToFile() { // zapis bitow do pliku
        fileChooser.setTitle("Wybierz plik do zapisu bitow");
        File file = fileChooser.showSaveDialog(null);
        if(file != null) {
            String bitsString = bitsTextField.getText(); // pobranie bitow z pola tekstowego
            transmission.setBitsFromString(bitsString); // ustawienie bitow z pola tekstowego
            BitSet bits = transmission.getBits(); // pobranie bitow
            byte[] bytes = bits.toByteArray(); // zamiana na tablice bajtow
//            transmission.reverse(bytes);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(bytes); // zapis bajtow
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
