package io.github.dk900912.pdf2image.config;

/**
 * Resolution levels for image conversion.
 * DPI (Dots Per Inch) settings for different quality levels.
 *
 * @author dukui
 */
public enum Resolution {
    /**
     * Standard resolution - suitable for screen display
     */
    STANDARD(96),

    /**
     * Medium resolution - balanced quality and file size
     */
    MEDIUM(150),

    /**
     * High resolution - suitable for printing and detailed viewing
     */
    HIGH(300);

    private final int dpi;

    Resolution(int dpi) {
        this.dpi = dpi;
    }

    public int getDpi() {
        return dpi;
    }
}