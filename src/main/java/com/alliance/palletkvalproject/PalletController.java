package com.alliance.palletkvalproject;

import com.alliance.palletkvalproject.Logic.barcode.BarcodeHandler;
import com.alliance.palletkvalproject.Logic.date.getDate;
import com.alliance.palletkvalproject.Logic.image.ImageLoader;
import com.alliance.palletkvalproject.Logic.layout.LabelFactory;
import com.alliance.palletkvalproject.Logic.layout.PalletLayoutCalculator;
import com.alliance.palletkvalproject.Logic.service.PalletService;
import com.alliance.palletkvalproject.Logic.util.SizeUtils;
import com.alliance.palletkvalproject.Model.lineItem;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.application.Platform;

import java.io.IOException;


public class PalletController {

    @FXML
    private Button newPalletSheet;
    @FXML
    private Button resetPalletSheet;
    @FXML
    private Button saveAllPalletButton;
    @FXML
    private TextField barcodeField;
    @FXML
    private ImageView palletSheetImage;
    @FXML
    private Label orderNumberLabel;
    @FXML
    private Label customerLabel;
    @FXML
    private Label moLabel;
    @FXML
    private Label sizeLabel;
    @FXML
    private Label quantityLabel;
    @FXML
    private Label lineLabel;
    @FXML
    private TextField quantityToAddField;
    @FXML
    private GridPane lineGridPane;
    @FXML
    private GridPane quantityGridPane;
    @FXML
    private GridPane orderGridPane;


    private Label[] palletFields;
    private PalletService palletService;
    private lineItem currentItem;
    private Label[] lineLabels;
    private Label[] orderLabels;
    private Label[][] quantityLabels;
    private final LabelFactory labelFactory = new LabelFactory();
    private int currentLineIndex = 0;
    private String date;

    /**
     * TO DO LIST:
     * 1. Make the buttons do things(Reset, New pallet sheet, Save all pallet sheets and exit)
     * 2. Maybe add a pallet counter on the app to keep track of how many you have done
     * 3. Add "Are you sure?" to the buttons so they don't accidentally do something they did not mean to
     * 4. Create all the edge case checks
     * 5. Update the actual currentPallet object with the buttons, like clearing pallet actually clears the current pallet
     */

    @FXML
    private void initialize() {
        try {
            palletService = new PalletService();
        } catch (IOException e) {
            // Show an alert so the user knows something went wrong
            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Initialization Error");
                alert.setHeaderText("Could not start application");
                alert.setContentText("Failed to initialize PalletService. Please check the configuration.");
                alert.showAndWait();
            });
            throw new RuntimeException("Failed to initialize PalletService", e);
        }

        date = getDate.getTodayDate();
        ImageLoader.loadPalletSheet(palletSheetImage);
        palletFields = new Label[]{moLabel, lineLabel, sizeLabel, orderNumberLabel, customerLabel, quantityLabel};
        setUpEventHandlers();

        PalletLayoutCalculator layoutCalculator = new PalletLayoutCalculator();
        Platform.runLater(() -> {
            layoutCalculator.calculateCoordinates(palletSheetImage, quantityGridPane, lineGridPane, orderGridPane);
        });

        setUpAllLabels(); //Gets labels created that will be filled upon entering a quantity from an order
    }

    private void setUpEventHandlers() {
        barcodeField.setOnAction(event -> {
            String scannedCode = barcodeField.getText();
            currentItem = BarcodeHandler.handleBarcodeScan(scannedCode, palletFields, palletService);

            fillQuantityToAdd();
            barcodeField.clear();
            Platform.runLater(() -> quantityToAddField.requestFocus());
        });

        quantityEnter();
        setUpResetButton();
        setUpNewPalletButton();
        setUpSaveAllPalletButton();
    }


    private void fillQuantityToAdd() {
        quantityToAddField.setText(quantityLabel.getText());
    }

    private void quantityEnter(){ //NEED TO UPDATE THIS SO THAT IT ALSO ADDS IT TO THE PALLET
        quantityToAddField.setOnAction(event -> {
            String size = String.valueOf(sizeLabel.getText());
            int sizeIndex = SizeUtils.getSizeColumnIndex(size);

            String quantityString = quantityToAddField.getText();
            int quantityEntered = Integer.parseInt(quantityString);

            quantityLabels[currentLineIndex][sizeIndex].setText(quantityString);
            lineLabels[currentLineIndex].setText(lineLabel.getText());
            orderLabels[0].setText(customerLabel.getText());
            orderLabels[1].setText(orderNumberLabel.getText());
            orderLabels[2].setText(date);

            boolean added = palletService.addItemToPallet(currentItem, quantityEntered);

            if(added) {
                System.out.println("\nItem added successfully!");
                System.out.println(currentItem.toString());

                System.out.println("\n---Current Pallet Contents---");
                for (lineItem item : palletService.getCurrentPallet()) {
                    System.out.println(item);
                }
            } else {
                System.out.println("Pallet content not added!");
            }

            if(currentLineIndex < quantityLabels.length - 1){
                currentLineIndex++;
            }
            quantityToAddField.clear();
            barcodeField.requestFocus();
        });
    }

    private void setUpAllLabels() {
        int lineRowsTotal = 14;
        lineLabels = labelFactory.createLineLabels(lineRowsTotal, lineGridPane);

        int orderColumnsTotal = 3;
        orderLabels = labelFactory.createOrderLabels(orderColumnsTotal, orderGridPane);

        int sizeRowsTotal = 14;
        int sizeColumnsTotal = 10;
        quantityLabels = labelFactory.createQuantityLabels(sizeRowsTotal, sizeColumnsTotal, quantityGridPane);
    }
    private void setUpResetButton() {
        resetPalletSheet.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.setTitle("Reset Pallet");
            alert.setHeaderText("Are you sure you want to reset the pallet?");
            alert.getButtonTypes().setAll(yes, cancel);
            alert.showAndWait();

            if (alert.getResult() == yes) {
                clearAllLabels();
                palletService.clearCurrentPallet();
            }
        });
    }
    private void setUpNewPalletButton() {
        // So when I click new pallet I want the pallet I was working on to be added to the pdf
        // I want the current pallet display to go away
        // I also want an Alert to ask if you are sure
        newPalletSheet.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.setTitle("New Pallet Sheet");
            alert.setHeaderText("Are you sure you want to begin the new pallet?");
            alert.getButtonTypes().setAll(yes, cancel);
            alert.showAndWait();

            if (alert.getResult() == yes) {
                palletService.completePallet();
                clearAllLabels();
            }

        });
    }
    private void setUpSaveAllPalletButton() {
        saveAllPalletButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.setTitle("Save All Pallets");
            alert.setHeaderText("Are you sure you want to save all pallets and be done?");
            alert.getButtonTypes().setAll(yes, cancel);
            alert.showAndWait();

            if (alert.getResult() == yes) {
                try {
                    palletService.completePallet();
                    clearAllLabels();
                    String location = palletService.savePallets();

                    Alert saveAlert = new Alert(Alert.AlertType.INFORMATION);
                    saveAlert.setTitle("Saved All Pallets");
                    saveAlert.setHeaderText("All Pallets Saved to " + location);
                    saveAlert.showAndWait();
                    Platform.exit();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private void clearAllLabels() {
        for (Label label : lineLabels) {
            label.setText("");
        }
        for (Label orderLabel : orderLabels) {
            orderLabel.setText("");
        }
        for (Label[] label : quantityLabels) {
            for (Label value : label) {
                value.setText("");
            }
        }
        for (Label palletField : palletFields) {
            palletField.setText("");
        }

        barcodeField.clear();
        quantityToAddField.clear();

        currentLineIndex = 0;
        barcodeField.requestFocus();
    }

}