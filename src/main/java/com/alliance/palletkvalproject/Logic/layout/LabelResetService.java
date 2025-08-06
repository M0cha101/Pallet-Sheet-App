package com.alliance.palletkvalproject.Logic.layout;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LabelResetService {

    public static void clearAllLabels(
            Label[] lineLabels,
            Label[] orderLabels,
            Label[][] quantityLabels,
            Label[] palletFields,
            TextField barcodeField,
            TextField quantityToAddField,
            Runnable resetState
    ) {
        for (Label label : lineLabels) {
            label.setText("");
        }
        for (Label label : orderLabels) {
            label.setText("");
        }
        for (Label[] row : quantityLabels) {
            for (Label cell : row) {
                cell.setText("");
            }
        }
        for (Label field : palletFields) {
            field.setText("");
        }

        barcodeField.clear();
        quantityToAddField.clear();

        if (resetState != null) {
            resetState.run();  // reset currentLineIndex, refocus, etc.
        }
    }
}
