package com.alliance.palletkvalproject;

import com.alliance.palletkvalproject.Logic.barcode.BarcodeHandler;
import com.alliance.palletkvalproject.Logic.date.GetDate;
import com.alliance.palletkvalproject.Logic.image.ImageLoader;
import com.alliance.palletkvalproject.Logic.layout.LabelFactory;
import com.alliance.palletkvalproject.Logic.layout.PalletLayoutCalculator;
import com.alliance.palletkvalproject.Logic.service.PalletService;
import com.alliance.palletkvalproject.Logic.util.SizeUtils;
import com.alliance.palletkvalproject.Model.LineItem;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.application.Platform;
import java.io.IOException;

/**
 * Controller class for the Pallet UI, handling interactions between the UI components and the
 * pallet processing logic. Manages barcode scanning, quantity entry, pallet creation,
 * and saving operations.
 */
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
    private LineItem currentItem;
    private Label[] lineLabels;
    private Label[] orderLabels;
    private Label[][] quantityLabels;
    private final LabelFactory labelFactory = new LabelFactory();
    private int currentLineIndex = 0;
    private String date;
    private final int lengthOfMo = 7;
    private final int customerSlot = 0;
    private final int orderNumberSlot = 1;
    private final int dateSlot = 2;
    private final int doorLimit = 100;

    /**
     * Initializes the controller after all @FXML fields have been injected.
     * Sets up the pallet service, loads images, initializes labels, and sets up event handlers.
     * Displays error alerts if initialization fails.
     */
    @FXML
    private void initialize() {
        try {
            palletService = new PalletService();
        } catch (IOException e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Initialization Error");
                alert.setHeaderText("Could not start application");
                alert.setContentText("Failed to initialize PalletService. Please check the configuration.");
                alert.showAndWait();
            });
            throw new RuntimeException("Failed to initialize PalletService", e);
        }

        date = GetDate.getTodayDate();
        ImageLoader.loadPalletSheet(palletSheetImage);
        palletFields = new Label[]{moLabel, lineLabel, sizeLabel, orderNumberLabel, customerLabel, quantityLabel};
        setUpEventHandlers();

        PalletLayoutCalculator layoutCalculator = new PalletLayoutCalculator();
        Platform.runLater(() -> {
            layoutCalculator.calculateCoordinates(palletSheetImage, quantityGridPane, lineGridPane, orderGridPane);
        });

        setUpAllLabels(); // Creates label arrays to display pallet info on the UI
    }

    /**
     * Sets up event handlers for UI components:
     * - Barcode field action to handle scanning.
     * - Quantity field action to handle quantity input.
     * - Buttons for reset, new pallet, and save all pallets.
     */
    private void setUpEventHandlers() {
        barcodeField.setOnAction(event -> {
            String scannedCode = barcodeField.getText().trim();
            if(scannedCode.isEmpty()){
                showError("Please scan a barcode before hitting enter");
                barcodeField.clear();
                return;
            }else if(scannedCode.length() != lengthOfMo){
                showError("Incorrect length of barcode");
                barcodeField.clear();
                return;
            }

            try {
                currentItem = BarcodeHandler.handleBarcodeScan(scannedCode, palletFields, palletService);
                fillQuantityToAdd();
                barcodeField.clear();
                Platform.runLater(() -> quantityToAddField.requestFocus());
            } catch(Exception e) {
                showError("Unexpected error during barcode scan:\n" + e.getMessage());
                barcodeField.clear();
            }
        });

        setUpQuantityEnter();
        setUpResetButton();
        setUpNewPalletButton();
        setUpSaveAllPalletButton();
    }

    /**
     * Copies the quantity text from the quantity label to the quantity input field.
     */
    private void fillQuantityToAdd() {
        quantityToAddField.setText(quantityLabel.getText());
    }

    /**
     * Sets up the action handler for the quantity input field.
     * Validates the entered quantity and size, adds the item to the pallet via the service,
     * updates the UI labels accordingly, and manages focus.
     */
    private void setUpQuantityEnter() {
        quantityToAddField.setOnAction(event -> {
            String size = sizeLabel.getText().trim();

            if (size.isEmpty()) {
                showError("Size appears to be missing or empty");
                return;
            }
            if (!SizeUtils.isValidSize(size)) {
                showError("Invalid size: " + size);
                quantityToAddField.clear();
                return;
            }

            int sizeIndex = SizeUtils.getSizeColumnIndex(size);
            if (sizeIndex < 0) {
                showError("Size could not be matched to a valid column");
                return;
            }

            String quantityString;
            int quantityEntered;
            try {
                quantityString = quantityToAddField.getText();
                quantityEntered = Integer.parseInt(quantityString);

                if (quantityEntered <= 0 || quantityEntered > doorLimit) {
                    showError("Quantity must be a positive number under: " + doorLimit);
                    quantityToAddField.clear();
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Something went wrong when parsing the quantity");
                quantityToAddField.clear();
                return;
            }

            if (currentLineIndex < 0 || currentLineIndex >= quantityLabels.length) {
                showError("Too many lines added. Cannot add more items to pallet.");
                return;
            }

            boolean added;
            try {
                added = palletService.addItemToPallet(currentItem, quantityEntered);
            } catch (Exception e) {
                showError("Trouble adding this item to the pallet.");
                return;
            }

            if (!added) {
                showError("Failed to add to the pallet. The item may already exist or the pallet is full. The number you entered could also be invalid.");
                return;
            }

            quantityLabels[currentLineIndex][sizeIndex].setText(quantityString);
            lineLabels[currentLineIndex].setText(lineLabel.getText());
            orderLabels[customerSlot].setText(customerLabel.getText());
            orderLabels[orderNumberSlot].setText(orderNumberLabel.getText());
            orderLabels[dateSlot].setText(date);

            currentLineIndex++;
            quantityToAddField.clear();
            barcodeField.requestFocus();
        });
    }

    /**
     * Initializes all label arrays used to display pallet information on the UI.
     * Handles errors during label creation by showing error alerts.
     */
    private void setUpAllLabels() {
        int lineRowsTotal = 14;
        try{
            lineLabels = labelFactory.createLineLabels(lineRowsTotal, lineGridPane);
        }catch (Exception e){
            showError("Internal error: Trouble with setting up the line Labels");
        }

        int orderColumnsTotal = 3;
        try{
            orderLabels = labelFactory.createOrderLabels(orderColumnsTotal, orderGridPane);
        }catch (Exception e){
            showError("Internal error: Trouble with setting up the order Labels");
        }

        int sizeRowsTotal = 14;
        int sizeColumnsTotal = 10;
        try{
            quantityLabels = labelFactory.createQuantityLabels(sizeRowsTotal, sizeColumnsTotal, quantityGridPane);
        }catch (Exception e){
            showError("Internal error: Trouble with setting up the quantity Labels");
        }
    }

    /**
     * Sets up the reset button to clear the current pallet after user confirmation.
     * Displays errors if reset operation fails.
     */
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
                try {
                    clearAllLabels();
                    palletService.clearCurrentPallet();
                } catch (Exception e) {
                    showError("An error occurred when resetting the pallet." + e.getMessage());
                }
            }
        });
    }

    /**
     * Sets up the new pallet button to complete the current pallet and reset the UI after user confirmation.
     * Displays errors if the completion fails.
     */
    private void setUpNewPalletButton() {
        newPalletSheet.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.setTitle("New Pallet Sheet");
            alert.setHeaderText("Are you sure you want to begin the new pallet?");
            alert.getButtonTypes().setAll(yes, cancel);
            alert.showAndWait();

            if (alert.getResult() == yes) {
                try {
                    palletService.completePallet();
                    clearAllLabels();
                } catch (Exception e) {
                    showError("An error occurred when completing the pallet: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Sets up the save all pallets button to finalize and save all pallets,
     * disable the UI, and exit the application after user confirmation.
     * Displays errors if saving fails.
     */
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

                    saveAllPalletButton.setDisable(true);
                    barcodeField.setDisable(true);
                    quantityToAddField.setDisable(true);
                    newPalletSheet.setDisable(true);
                    resetPalletSheet.setDisable(true);

                    String location = palletService.savePallets();

                    Alert saveAlert = new Alert(Alert.AlertType.INFORMATION);
                    saveAlert.setTitle("Saved All Pallets");
                    saveAlert.setHeaderText("All Pallets Saved to " + location);
                    saveAlert.showAndWait();
                    Platform.exit();

                } catch (IOException e) {
                    showError("An error occurred when saving the pallets: " + e.getMessage());
                    saveAllPalletButton.setDisable(false);
                    barcodeField.setDisable(false);
                    quantityToAddField.setDisable(false);
                    newPalletSheet.setDisable(false);
                    resetPalletSheet.setDisable(false);
                }
            }
        });
    }

    /**
     * Clears all labels and text fields in the UI related to pallet display.
     * Resets the current line index and refocuses the barcode input field.
     */
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

    /**
     * Shows an error alert with the specified message.
     *
     * @param message The error message to display.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
