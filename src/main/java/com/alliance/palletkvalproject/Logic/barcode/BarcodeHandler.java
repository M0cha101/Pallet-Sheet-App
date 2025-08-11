package com.alliance.palletkvalproject.Logic.barcode;
import com.alliance.palletkvalproject.Logic.service.PalletService;
import com.alliance.palletkvalproject.Logic.service.PalletSheetMethods;
import com.alliance.palletkvalproject.Model.LineItem;
import javafx.scene.control.Label;

public class BarcodeHandler {

    public static LineItem handleBarcodeScan(String scannedCode, Label[] palletFields, PalletService palletService) {
        String[] values = PalletSheetMethods.findMoInFile(scannedCode);
        if (values != null && values.length == palletFields.length) {
            for (int i = 0; i < values.length; i++) {
                palletFields[i].setText(values[i]);
            }
            return palletService.findMoItem(scannedCode);
        }
        return null;
    }
}
