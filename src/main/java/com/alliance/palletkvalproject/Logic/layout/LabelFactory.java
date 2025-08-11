package com.alliance.palletkvalproject.Logic.layout;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LabelFactory {

    public Label[] createLineLabels(int count, GridPane lineGridPane) {
        validateGridPaneAndCount(lineGridPane, count, "lineGridPane");
        Label[] lineLabels = new Label[count];
        for (int i = 0; i < count; i++) {
            lineLabels[i] = createLabel();
            lineGridPane.add(lineLabels[i], 0, i);
        }
        return lineLabels;
    }

    public Label[] createOrderLabels(int count, GridPane orderGridPane) {
        validateGridPaneAndCount(orderGridPane, count, "orderGridPane");
        Label[] orderLabels = new Label[count];
        for (int i = 0; i < count; i++) {
            orderLabels[i] = createLabel();
            orderGridPane.add(orderLabels[i], i, 0);
        }
        return orderLabels;
    }

    public Label[][] createQuantityLabels(int rows, int columns, GridPane quantityGridPane) {
        if (quantityGridPane == null) {
            throw new IllegalArgumentException("quantityGridPane cannot be null");
        }
        if (rows <= 0) {
            throw new IllegalArgumentException("rows must be positive");
        }
        if (columns <= 0) {
            throw new IllegalArgumentException("columns must be positive");
        }
        Label[][] quantityLabels = new Label[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                quantityLabels[row][col] = createLabel();
                quantityGridPane.add(quantityLabels[row][col], col, row);
            }
        }
        return quantityLabels;
    }

    private Label createLabel() {
        Label label = new Label("");
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.BLACK);
        label.setFont(Font.font("System", FontWeight.BOLD, 18));
        return label;
    }

    private void validateGridPaneAndCount(GridPane gridPane, int count, String name) {
        if (gridPane == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
        if (count <= 0) {
            throw new IllegalArgumentException(name + " count must be positive");
        }
    }
}
