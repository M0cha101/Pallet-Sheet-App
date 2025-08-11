package com.alliance.palletkvalproject.Logic.service;

import com.alliance.palletkvalproject.Logic.export.PalletSheetExporter;
import com.alliance.palletkvalproject.Model.LineItem;
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
    private static final int MO_INDEX = 0;
    private static final int LINE_INDEX = 1;
    private static final int SIZE_INDEX = 2;
    private static final int ORDER_INDEX = 3;
    private static final int CUSTOMER_INDEX = 4;
    private static final int QUANTITY_INDEX = 5;

    private List<LineItem> currentPallet;
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
    public LineItem findMoItem(String mo) {
        String[] values = PalletSheetMethods.findMoInFile(mo);

        if (values == null || values.length != AMOUNT_OF_PALLET_FIELDS || PalletSheetMethods.anyFieldEmpty(values)) {
            return null; // GUI can handle the error display
        }

        try {
            int quantity = Integer.parseInt(values[QUANTITY_INDEX]);
            return new LineItem(values[MO_INDEX], values[LINE_INDEX], values[SIZE_INDEX],
                    values[ORDER_INDEX], values[CUSTOMER_INDEX], quantity);

        } catch (NumberFormatException e) {
            return null; // GUI can handle the error display
        }
    }

    // Add item to current pallet with specified quantity
    public boolean addItemToPallet(LineItem item, int quantityToAdd) {
        if (currentLineIndex >= MAX_Lines) {
            return false; //Maybe change this to throw an exception or something later?
        }
        //Uses findMoItem method to get the LineItem
        if (quantityToAdd < 1 || quantityToAdd > item.getQuantity()) {
            return false; // Invalid quantity
        }

        LineItem partial = new LineItem(item.getMo(), item.getLineNumber(), item.getDoorSize(),
                item.getOrderNumber(), item.itemGetCustomer(), quantityToAdd);
        currentPallet.add(partial);
        currentLineIndex++;
        return true;
    }

    // Get current pallet contents (for GUI display)
    public List<LineItem> getCurrentPallet() {
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
        LineItem firstItem = currentPallet.getFirst();
        PalletSheetExporter.addPalletToDocument(document, background, currentPallet,
                palletNumber, firstItem.getOrderNumber(),
                firstItem.itemGetCustomer(), firstItem.getLineNumber());

        currentPallet.clear();
        palletNumber++;
        currentLineIndex = 0;
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