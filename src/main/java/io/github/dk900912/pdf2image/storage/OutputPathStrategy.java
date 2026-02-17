package io.github.dk900912.pdf2image.storage;

import io.github.dk900912.pdf2image.config.ImageFormat;

import java.nio.file.Path;

/**
 * Strategy interface for determining the output path for stored images.
 * Allows different strategies (e.g., prefix-based, date-based, etc.)
 *
 * @author dukui
 */
public interface OutputPathStrategy {
    Path resolve(Path outputDirectory, int pageNumber, ImageFormat format);
}