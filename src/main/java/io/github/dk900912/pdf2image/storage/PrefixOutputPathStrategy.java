package io.github.dk900912.pdf2image.storage;

import io.github.dk900912.pdf2image.config.ImageFormat;

import java.nio.file.Path;

/**
 * Output path strategy that prepends a prefix to the page number.
 * Format: {outputDirectory}/{prefix}{pageNumber}.{extension}
 *
 * @author dukui
 */
public class PrefixOutputPathStrategy implements OutputPathStrategy {
    private final String prefix;

    /**
     * Creates a prefix-based output path strategy.
     * Format: {outputDirectory}/{prefix}{pageNumber}.{extension}
     *
     * @param prefix the prefix to prepend before the page number
     */
    public PrefixOutputPathStrategy(String prefix) {
        this.prefix = prefix != null ? prefix : "";
    }

    /**
     * Resolves the output file path for the specified page using the configured prefix.
     *
     * @param outputDirectory the base directory where images are stored
     * @param pageNumber the 1-based page number used for naming
     * @param format the image format used to determine file extension
     * @return the fully resolved output path for the page
     */
    @Override
    public Path resolve(Path outputDirectory, int pageNumber, ImageFormat format) {
        if (outputDirectory == null) {
            throw new IllegalArgumentException("Output Directory must be non-null");
        }
        if (format == null) {
            throw new IllegalArgumentException("Image Format must be non-null");
        }
        String filename = prefix + pageNumber + "." + format.getExtension();
        return outputDirectory.resolve(filename);
    }
}
