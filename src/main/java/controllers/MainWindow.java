package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import module.Transmission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.BitSet;

public class MainWindow {

    private final FileChooser fileChooser = new FileChooser();
    private Transmission transmission;

    @FXML
    public Button savePlainTextButton;
    @FXML
    public Button checkBitsButton;
    @FXML
    public Button saveBitsButton;
    @FXML
    public Label isPlainTextReadedLabel;
    @FXML
    public Label isBitsFileReadedLabel;
    @FXML
    public TextArea bitsTextField;
    @FXML
    public Button saveBitsButton1;


    public void initialize() {
        savePlainTextButton.setDisable(true);
        saveBitsButton.setDisable(true);
    }


    public void readPlainText(ActionEvent actionEvent) throws IOException {
        fileChooser.setTitle("Wybierz plik z tekstem jawnym");
        File file = fileChooser.showOpenDialog(null);
        if(file != null) {
            isPlainTextReadedLabel.setText("Plik został wczytany");
            isPlainTextReadedLabel.setStyle("-fx-text-fill: green");
            saveBitsButton.setDisable(false);
            String content = Files.readString(Path.of(file.getPath()), StandardCharsets.US_ASCII);
            transmission = new Transmission(content, true);
            bitsTextField.setText(transmission.getBitsAsString(transmission.getBits()));
        }
    }

    public void checkBits(ActionEvent actionEvent) {
        savePlainTextButton.setDisable(false);
        transmission.correctBits();
    }

    public void saveBits(ActionEvent actionEvent) {
        String bitsString = bitsTextField.getText();
        transmission.setBitsFromString(bitsString);
    }

    public void readBits(ActionEvent actionEvent) throws IOException {
        fileChooser.setTitle("Wybierz plik z bitami");
        File file = fileChooser.showOpenDialog(null);
        if(file != null) {
            isBitsFileReadedLabel.setText("Plik został wczytany");
            isBitsFileReadedLabel.setStyle("-fx-text-fill: green");
            checkBitsButton.setDisable(false);
            byte[] message = Files.readAllBytes(Path.of(file.getPath()));
            String mess = new String(message);
            System.out.println(mess);
            transmission = new Transmission(mess, false);
            System.out.println(transmission.StringToBits(mess));
        }
    }

    public void savePlainText(ActionEvent actionEvent) {
    }

    public void saveBitsToFile(ActionEvent actionEvent) {
        fileChooser.setTitle("Wybierz plik do zapisu bitow");
        File file = fileChooser.showSaveDialog(null);
        if(file != null) {
            String bitsString = bitsTextField.getText();
            transmission.setBitsFromString(bitsString);
            BitSet bits = transmission.getBits();
            byte[] bytes = bits.toByteArray();
//            transmission.reverse(bytes);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
