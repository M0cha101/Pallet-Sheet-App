package com.alliance.palletkvalproject;

import com.alliance.palletkvalproject.Logic.PalletSheetExporter;
import com.alliance.palletkvalproject.Logic.palletSheetMethods;
import com.alliance.palletkvalproject.Model.PalletService;
import com.alliance.palletkvalproject.Model.lineItem;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PalletController {

    @FXML
    private Button newPalletSheet;
    @FXML
    private Button resetPalletSheet;
    @FXML
    private Button savePalletButton;
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
    private PalletSheetExporter palletSheetExporter;
    private lineItem currentItem;//Should this be a global variable??

    String[] sizes = {"36x80", "34x80", "32x80", "30x80", "28x80", "26x80", "24x80", "22x80", "20x80", "18x80"};//TODO: change to ENUMs
    Label[][] quantityLabels = new Label[14][10];
    Label[] lineLabels = new Label[14];
    Label[] orderLabels = new Label[3];
    private int currentLineIndex = 0;
    private String date;

    public final int originalWidth = 3300;
    public final int originalHeight = 2550;
    public double currentWidth = 0;
    public double currentHeight = 0;
    public double scaleX = 0;
    public double scaleY = 0;
    public int originalQuantityX = 575;
    public int originalQuantityY = 415;
    public int originalLineX = 130;
    public int originalLineY = 420;
    public int originalOrderNumberX = 400;
    public int originalOrderNumberY = 135;
    public double quantityX = 0;
    public double quantityY = 0;
    public double lineX = 0;
    public double lineY = 0;
    public double orderNumberX = 0;
    public double orderNumberY = 0;

    /**
     * TO DO LIST:
     * 1. Make the buttons do things(Reset, New pallet sheet, Save all pallet sheets and exit)
     * 2. Maybe add a pallet counter on the app to keep track of how many you have done
     * 3. Add "Are you sure?" to the buttons so they don't accidentally do something they did not mean to
     * 4. Create all the edge case checks
     * 5. Update the actual currentPallet object with the buttons, like clearing pallet actually clears the current pallet
     * 6. Remove System.Out.Println() and use proper logging, additionally user friendly error messages should be shown to the user in the GUI
     */

    /**
     * PLACE TO WRITE OUT THOUGHT PROCESS:
     * For quantity entered, it is now adding it to List of line items that make up a pallet
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

        date = getTodayDate();
        loadPalletSheet(); //Loads pallet sheet duh
        setUpEventHandlers(); //Barcode and quantity enter
        calculateCoordinates(); //Gets coordinates figured out for the pallet sheet and sets where first boxes will be
        setUpAllLabels(); //Gets labels created that will be filled upon entering a quantity from an order
    }

    private void positionGridPanes() {
        quantityGridPane.setLayoutX(quantityX);
        quantityGridPane.setLayoutY(quantityY);

        lineGridPane.setLayoutX(lineX);
        lineGridPane.setLayoutY(lineY);

        orderGridPane.setLayoutX(orderNumberX);
        orderGridPane.setLayoutY(orderNumberY);
    }
    private void calculateCoordinates(){
        Platform.runLater(() -> {
            currentWidth = palletSheetImage.getFitWidth();
            currentHeight = palletSheetImage.getFitHeight();
            scaleX = currentWidth / originalWidth;
            scaleY = currentHeight / originalHeight;
            quantityX = originalQuantityX * scaleX;
            quantityY = originalQuantityY * scaleY;
            lineX = originalLineX * scaleX;
            lineY = originalLineY * scaleY;
            orderNumberX = originalOrderNumberX * scaleX;
            orderNumberY = originalOrderNumberY * scaleY;

            positionGridPanes();
            //Needed to use run later and have this method in here because we need to calculate the coordinates later
            // when the app has initialized a bit so we can get proper measurements of the imageview since
            // we do not know the size of image view until we are in app, this is better for the future
        });
    }


    private void loadPalletSheet() {
        try {
            InputStream imageStream = getClass().getResourceAsStream("/blank_pallet_sheet.png");
            if (imageStream == null) {
                System.out.println("Image not found at path!");
                return;
            }

            Image image = new Image(imageStream);
            if (image.isError()) {
                System.out.println("Image error: " + image.getException().getMessage());
                return;
            }

            palletSheetImage.setImage(image);
            System.out.println("Image loaded successfully!");

        } catch (Exception e) {
            System.out.println("Exception loading image: " + e.getMessage());
        }
    }

    private void displayPalletFields(String[] values) {
        for (int i = 0; i < values.length; i++) {
            palletFields[i].setText(values[i]); //This is displaying inside the app
        }
        fillQuantityToAdd();
    }

    private void setUpEventHandlers() {
        setUpBarCodeField();
        quantityEnter();
        setUpResetButton();
        setUpNewPalletButton();
        //setup buttons and also what happens when you hit enter after scanned and stuff
    }

    private void setUpBarCodeField() {
        palletFields = new Label[]{moLabel, lineLabel, sizeLabel, orderNumberLabel, customerLabel, quantityLabel};
        barcodeField.setOnAction(event -> {
            String scannedCode = barcodeField.getText();
            String[] values = palletSheetMethods.findMoInFile(scannedCode);

            
            // TODO: The if statement here should change slightly, 
            // there should be separate messages for length mismatch and sending an MO that does not exist
            // Also it would be helpful if there was a dialog box or other message on the GUI to tell the user that the MO was invalid
             

            if (values != null && values.length == palletFields.length) {
                displayPalletFields(values);
                currentItem = palletService.findMoItem(scannedCode);
                quantityToAddField.requestFocus();
            } else {
                System.out.println("Invalid scan result or length mismatch.");
            }

            barcodeField.clear();
        });
    }

    private void fillQuantityToAdd() {
        quantityToAddField.setText(quantityLabel.getText());
    }

    private void quantityEnter(){ //NEED TO UPDATE THIS SO THAT IT ALSO ADDS IT TO THE PALLET
        quantityToAddField.setOnAction(event -> {
            String size = String.valueOf(sizeLabel.getText());
            int sizeIndex = getSizeColumnIndex(size);

            String quantityString = quantityToAddField.getText();
            int quantityEntered = Integer.parseInt(quantityString);//TODO: Surround with try catch block in the event a NumberFormatException is thrown

            quantityLabels[currentLineIndex][sizeIndex].setText(quantityString);
            lineLabels[currentLineIndex].setText(lineLabel.getText());
            orderLabels[0].setText(customerLabel.getText());
            orderLabels[1].setText(orderNumberLabel.getText());
            orderLabels[2].setText(date);

            boolean added = palletService.addItemToPallet(currentItem, quantityEntered);//TODO: add null check for currentItem and quantityEntered

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

    private void setUpLineLabels() {
        for (int i = 0; i < lineLabels.length; i++) {
            lineLabels[i] = new Label("");
            lineLabels[i].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            lineLabels[i].setAlignment(Pos.CENTER);
            lineLabels[i].setTextFill(Color.BLACK);
            lineLabels[i].setFont(Font.font("System", FontWeight.BOLD, 18));
            lineGridPane.add(lineLabels[i], 0, i); //Column 0, row i
        }
    }

    private void setUpOrderLabels() {
        for (int i = 0; i < orderLabels.length; i++) {
            orderLabels[i] = new Label("");
            orderLabels[i].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            orderLabels[i].setAlignment(Pos.CENTER);
            orderLabels[i].setTextFill(Color.BLACK);
            orderLabels[i].setFont(Font.font("System", FontWeight.BOLD, 18));
            orderGridPane.add(orderLabels[i], i, 0); //Column i, row 0
        }
    }
    private void setUpQuantityLabels() {
        for (int row = 0; row < quantityLabels.length; row++) { //First loops through the first bracket[14] or the rows
            for (int column = 0; column < quantityLabels[row].length; column++) { //Then loops the 2nd bracket[10] or the columns
                quantityLabels[row][column] = new Label("");
                quantityLabels[row][column].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                quantityLabels[row][column].setAlignment(Pos.CENTER);
                quantityLabels[row][column].setTextFill(Color.BLACK);
                quantityLabels[row][column].setFont(Font.font("System", FontWeight.BOLD, 18));
                quantityGridPane.add(quantityLabels[row][column], column, row);
            }
            // The column < quantityLabels[row] is always maxing out at 10 since each row is 10 long
            // this is the case because quantityLabels.length = 14 and quantityLabels[] = 10, without brackets it assumes
            // that it is the first bracket, with bracket is means the second bracket which is 10
        }
    }
    private void setUpAllLabels() {
        setUpQuantityLabels();
        setUpLineLabels();
        setUpOrderLabels();
    }
    private int getSizeColumnIndex(String size){
        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i].equals(size)) {
                return i;
            }
        }
        return -1;
        // This method just finds the index of the column that its respective size needs to be at
    }

    private String getTodayDate(){
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
        return sdf.format(today);
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

            if (alert.getResult() == ButtonType.YES) {
                clearAllLabels();
                palletService.clearCurrentPallet();
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

}