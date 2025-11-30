package io.github.dk900912.pdf2image.config;

import java.awt.image.BufferedImage;

/**
 * Image color modes for conversion.
 *
 * @author dukui
 */
public enum ImageMode {
    /**
     * Full color RGB mode
     */
    COLOR(BufferedImage.TYPE_INT_RGB),

    /**
     * Grayscale mode
     */
    GRAYSCALE(BufferedImage.TYPE_BYTE_GRAY),

    /**
     * Black and white binary mode
     */
    BLACK_AND_WHITE(BufferedImage.TYPE_BYTE_BINARY);

    private final int bufferedImageType;

    ImageMode(int bufferedImageType) {
        this.bufferedImageType = bufferedImageType;
    }

    public int getBufferedImageType() {
        return bufferedImageType;
    }
}