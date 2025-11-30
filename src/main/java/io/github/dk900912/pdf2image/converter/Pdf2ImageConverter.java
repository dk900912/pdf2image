package io.github.dk900912.pdf2image.converter;

import io.github.dk900912.pdf2image.context.Context;
import io.github.dk900912.pdf2image.exception.Pdf2ImageException;
import io.github.dk900912.pdf2image.processor.DefaultImageProcessor;
import io.github.dk900912.pdf2image.renderer.PdfBoxPageRenderer;
import io.github.dk900912.pdf2image.storage.LocalFileSystemStorage;

/**
 * Interface for PDF to image conversion operations.
 *
 * @author dukui
 */
public interface Pdf2ImageConverter {

    /**
     * Converts a PDF file to images according to the provided configuration.
     *
     * @param context the conversion context
     * @exception Pdf2ImageException if conversion fails
     */
    void convert(Context context);


    static DefaultPdf2ImageConverter createDefaultConverter() {
        return new DefaultPdf2ImageConverter(
                new PdfBoxPageRenderer(),
                new DefaultImageProcessor(),
                new LocalFileSystemStorage()
        );
    }

}