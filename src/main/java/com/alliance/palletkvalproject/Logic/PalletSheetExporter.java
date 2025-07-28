package com.alliance.palletkvalproject.Logic;

import com.alliance.palletkvalproject.Model.lineItem;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PalletSheetExporter {

    private static final float BASE_X = 160f;
    private static final float X_STEP = 61f;
    private static final float BASE_Y = 490f;
    private static final float Y_STEP = 26.5f;
    private static final float LINE_X = 65f;
    private static final int FONT_SIZE_SMALL = 12;
    private static final int FONT_SIZE_MEDIUM = 16;
    private static final int FONT_SIZE_LARGE = 100;


    public static void addPalletToDocument(PDDocument doc, PDImageXObject background, List<lineItem> pallet, int palletNumber, String orderNumber, String Customer, String lineNumber) {

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
        String todayDate = sdf.format(today);

        try {

            PDPage page = new PDPage(new PDRectangle(PDRectangle.LETTER.getHeight(), PDRectangle.LETTER.getWidth()));
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.drawImage(background, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());

            //Small Pallet Number location
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, FONT_SIZE_SMALL);
            content.newLineAtOffset(25, 575);
            content.showText("Pallet #" + palletNumber);
            content.endText();

            //Customer name location
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, FONT_SIZE_MEDIUM);
            content.newLineAtOffset(110,550);
            content.showText(Customer);
            content.endText();

            //Order Number location
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, FONT_SIZE_MEDIUM);
            content.newLineAtOffset(320,550);
            content.showText(orderNumber);
            content.endText();

            //Date location
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, FONT_SIZE_MEDIUM);
            content.newLineAtOffset(550,550);
            content.showText(todayDate);
            content.endText();

            //Big Customer Number location
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, FONT_SIZE_LARGE);
            content.setCharacterSpacing(20f);
            content.newLineAtOffset(140,40);
            content.showText(orderNumber);
            content.endText();

            //This is what sets the base x and the x step how far it moves
            Map<String, Float> sizeXMap = new HashMap<>();

            //Creates a list of all the possible door sizes so you can use the map properly
            String[] sizes = {"36x80", "34x80", "32x80", "30x80", "28x80", "26x80", "24x80", "22x80", "20x80", "18x80"};

            //Assigns all the X locations per size
            for (int i = 0; i < sizes.length; i++) {
                sizeXMap.put(sizes[i], BASE_X + (i * X_STEP));
            }

            //Assigns first Y location, the step between rows, and creates the row index variable to keep track of row
            int rowIndex = 0;

            //Gets door size for every line item on the pallet
            for (lineItem item : pallet) {
                String size = item.getDoorSize();
                if (!sizeXMap.containsKey(size)) {
                    System.out.println("Unknown door size: " + size);
                    continue;
                }


                //Assigns X location for door quantity and line number and their
                float quantityX = sizeXMap.get(size);
                float rowY = BASE_Y - (rowIndex * Y_STEP);

                //Inserts the quantity in respective location
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, FONT_SIZE_MEDIUM);
                content.newLineAtOffset(quantityX, rowY);
                content.showText(String.valueOf(item.getQuantity()));
                content.endText();

                //Inserts the line number in respective location
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, FONT_SIZE_MEDIUM);
                content.newLineAtOffset(LINE_X, rowY);
                content.showText(String.valueOf(item.getLineNumber()));
                content.endText();

                rowIndex++;
            }

            content.close();
            System.out.println("Pallet #" + palletNumber + " added to PDF");

        } catch (IOException e) {
            System.out.println("Pallet #" + palletNumber + " could not be added to PDF: " + e.getMessage());
        }
    }
}
