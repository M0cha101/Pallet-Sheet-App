package com.alliance.palletkvalproject.Logic.barcode;
import com.alliance.palletkvalproject.Logic.service.PalletService;
import com.alliance.palletkvalproject.Logic.service.palletSheetMethods;
import com.alliance.palletkvalproject.Model.lineItem;
import javafx.scene.control.Label;

public class BarcodeHandler {

    public static lineItem handleBarcodeScan(String scannedCode, Label[] palletFields, PalletService service) {
        String[] values = palletSheetMethods.findMoInFile(scannedCode);
        if (values != null && values.length == palletFields.length) {
            for (int i = 0; i < values.length; i++) {
                palletFields[i].setText(values[i]);
            }
            return service.findMoItem(scannedCode);
        }
        return null;
    }
}
