package io.github.dk900912.pdf2image.renderer;

import io.github.dk900912.pdf2image.context.Context;
import io.github.dk900912.pdf2image.exception.Pdf2ImageException;

import java.awt.image.BufferedImage;

/**
 * Interface for rendering PDF pages to images.
 *
 * @author dukui
 */
public interface PageRenderer {

    /**
     * Renders a specific page of a PDF document to a BufferedImage.
     *
     * @param context the conversion context
     * @return the rendered image
     * @exception Pdf2ImageException if rendering fails
     */
    BufferedImage renderPage(Context context);
}