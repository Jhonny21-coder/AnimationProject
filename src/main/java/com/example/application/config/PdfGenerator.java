package com.example.application.config;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class PdfGenerator {

    public byte[] generatePdf(String imageUrl) {

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Load the image
            Image img = Image.getInstance("src/main/resources/META-INF/resources/artwork_images/" + imageUrl);

            // Scale the image to fit within the page size while preserving aspect ratio
            img.scaleToFit(PageSize.A4.getWidth() - document.leftMargin() - document.rightMargin(),
                           PageSize.A4.getHeight() - document.topMargin() - document.bottomMargin());

            // Center the image on the page
            img.setAlignment(Image.ALIGN_CENTER);

            document.add(img);
            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
