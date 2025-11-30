package io.github.dk900912.pdf2image.config;

/**
 * Supported image output formats.
 *
 * @author dukui
 */
public enum ImageFormat {
    PNG("png"),
    JPEG("jpeg"),
    JPG("jpg");

    private final String extension;

    ImageFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    /**
     * Get the format name for PDFBox ImageIO operations.
     *
     * @return format name
     */
    public String getFormatName() {
        return this == JPG ? "jpeg" : extension;
    }
}