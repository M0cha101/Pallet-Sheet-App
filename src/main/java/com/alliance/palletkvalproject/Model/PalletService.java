package com.alliance.palletkvalproject.Model;

import com.alliance.palletkvalproject.Logic.PalletSheetExporter;
import com.alliance.palletkvalproject.Logic.palletSheetMethods;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class PalletService {

    private static final int KILOBYTE = 1024;
    private static final int AMOUNT_OF_PALLET_FIELDS = 6;
    private static final int MAX_Lines = 14;
    private int currentLineIndex = 0;

    private List<lineItem> currentPallet;
    private int palletNumber;
    private PDDocument document;
    private PDImageXObject background;

    public PalletService() throws IOException {
        this.currentPallet = new ArrayList<>();
        this.palletNumber = 1;
        initializePdfDocument();
    }

    private void initializePdfDocument() throws IOException {
        document = new PDDocument();
        InputStream imageStream = getClass().getResourceAsStream("/blank_pallet_sheet.png");
        if (imageStream == null) {
            throw new IOException("Could not find pallet sheet image. Make sure it's in src/main/resources/");
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[KILOBYTE];
        int nRead;
        while ((nRead = imageStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        byte[] imageBytes = buffer.toByteArray();
        background = PDImageXObject.createFromByteArray(document, imageBytes, "background");
    }

    // Core business method - find MO and return line item
    public lineItem findMoItem(String mo) {
        String[] values = palletSheetMethods.findMoInFile(mo);

        if (values == null || values.length != AMOUNT_OF_PALLET_FIELDS || palletSheetMethods.anyFieldEmpty(values)) {
            return null; // GUI can handle the error display
        }

        try {
            int quantity = Integer.parseInt(values[5]);
            return new lineItem(values[0], values[1], values[2], values[3], values[4], quantity);
        } catch (NumberFormatException e) {
            return null; // GUI can handle the error display
        }
    }

    // Add item to current pallet with specified quantity
    public boolean addItemToPallet(lineItem item, int quantityToAdd) {
        if (currentLineIndex >= MAX_Lines) {
            return false; //Maybe change this to throw an exception or something later?
        }
        //Uses findMoItem method to get the lineItem
        if (quantityToAdd < 1 || quantityToAdd > item.getQuantity()) {
            return false; // Invalid quantity
        }

        lineItem partial = new lineItem(item.getMo(), item.getLineNumber(), item.getDoorSize(),
                item.getOrderNumber(), item.itemGetCustomer(), quantityToAdd);
        //Need to check to make sure the currentLineIndex is not greater than 14, or maybe you just forgot that it
        // already checks it somewhere else to make sure the amount of lines cannot be greater than 14
        currentPallet.add(partial);
        currentLineIndex++;
        return true;
    }

    // Get current pallet contents (for GUI display)
    public List<lineItem> getCurrentPallet() {
        return new ArrayList<>(currentPallet); // Return copy to prevent external modification
    }

    // Get current pallet number
    public int getCurrentPalletNumber() {
        return palletNumber;
    }

    // Complete current pallet and start new one
    public void completePallet() {
        if (currentPallet.isEmpty()) {
            return; // Nothing to complete
        }

        // Use the first item for order info (they should all be the same order)
        lineItem firstItem = currentPallet.get(0);
        PalletSheetExporter.addPalletToDocument(document, background, currentPallet,
                palletNumber, firstItem.getOrderNumber(),
                firstItem.itemGetCustomer(), firstItem.getLineNumber());

        currentPallet.clear();
        palletNumber++;
    }

    // Clear current pallet without saving
    public void clearCurrentPallet() {
        currentPallet.clear();
    }

    // Check if there are any completed pallets
    public boolean hasCompletedPallets() {
        return document.getNumberOfPages() > 0;
    }

    // Save all pallets to PDF
    public String savePallets() throws IOException {
        if (document.getNumberOfPages() == 0) {
            throw new IOException("No pallets have been completed yet. Nothing to save.");
        }

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String filename = "pallet_sheets_" + today + ".pdf";

        document.save(filename);
        return new File(filename).getAbsolutePath();
    }

    // Save to specific location
    public void savePallets(String filepath) throws IOException {
        if (document.getNumberOfPages() == 0) {
            throw new IOException("No pallets have been completed yet. Nothing to save.");
        }

        document.save(filepath);
    }

    // Clean up resources
    public void cleanup() {
        try {
            if (document != null) {
                document.close();
            }
        } catch (IOException e) {
            // Log error but don't throw - cleanup should be safe
            System.err.println("Error closing document: " + e.getMessage());
        }
    }
}