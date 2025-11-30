package io.github.dk900912.pdf2image.processor;

import io.github.dk900912.pdf2image.context.Context;

import java.awt.image.BufferedImage;

/**
 * Interface for post-processing rendered images.
 * Handles operations like rotation, cropping, and color conversion.
 *
 * @author dukui
 */
public interface ImageProcessor {

    /**
     * Processes a rendered image according to the configuration.
     *
     * @param context the conversion context
     * @return the processed image
     */
    BufferedImage process(Context context);
}