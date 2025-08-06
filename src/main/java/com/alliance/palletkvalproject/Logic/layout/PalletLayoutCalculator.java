package com.alliance.palletkvalproject.Logic.layout;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class PalletLayoutCalculator {
    // Fields: keep as instance variables (no static)
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

    public void positionGridPanes(GridPane quantityGridPane, GridPane lineGridPane, GridPane orderGridPane) {
        quantityGridPane.setLayoutX(quantityX);
        quantityGridPane.setLayoutY(quantityY);

        lineGridPane.setLayoutX(lineX);
        lineGridPane.setLayoutY(lineY);

        orderGridPane.setLayoutX(orderNumberX);
        orderGridPane.setLayoutY(orderNumberY);
    }

    public void calculateCoordinates(ImageView palletSheetImage, GridPane quantityGridPane, GridPane lineGridPane, GridPane orderGridPane) {
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

            positionGridPanes(quantityGridPane, lineGridPane, orderGridPane);
    }
}
