package com.alliance.palletkvalproject.Logic.image;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;

public class ImageLoader {
    public static void loadPalletSheet(ImageView palletSheetImage) {
        try {
            InputStream imageStream = ImageLoader.class.getResourceAsStream("/blank_pallet_sheet.png");
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
}
