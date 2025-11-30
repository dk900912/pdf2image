package io.github.dk900912.pdf2image.storage;

import io.github.dk900912.pdf2image.config.ConversionConfig;
import io.github.dk900912.pdf2image.config.ImageFormat;
import io.github.dk900912.pdf2image.context.Context;
import io.github.dk900912.pdf2image.context.ContextBase;
import io.github.dk900912.pdf2image.exception.Pdf2ImageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Implementation of ImageStorage that saves images to local file system.
 *
 * @author dukui
 */
public class LocalFileSystemStorage implements ImageStorage {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileSystemStorage.class);

    @Override
    public void prepare(Context context) {
        ConversionConfig config = (ConversionConfig) ((ContextBase) context).get("config");
        Path outputDirectory = config.getOutputDirectory();
        try {
            if (!Files.exists(outputDirectory)) {
                Files.createDirectories(outputDirectory);
                logger.info("Created output directory: {}", outputDirectory);
            } else {
                try (Stream<Path> listed = Files.list(outputDirectory)) {
                    // remove existing files
                    listed.filter(Files::isRegularFile)
                            .forEach(file -> {
                                try {
                                    Files.deleteIfExists(file);
                                    logger.debug("Deleted existing file: {}", file);
                                } catch (IOException e) {
                                    logger.warn("Failed to delete file: {}", file, e);
                                }
                            });
                } catch (IOException e) {
                    throw new Pdf2ImageException("Failed to list files in directory: " + outputDirectory, e);
                }
            }
        } catch (IOException e) {
            throw new Pdf2ImageException("Failed to create output directory: " + outputDirectory, e);
        }
    }

    @Override
    public void store(Context context) {
        BufferedImage image = (BufferedImage) ((ContextBase) context).get("image");
        int pageNumber = (int) ((ContextBase) context).get("page-index") + 1;
        ConversionConfig config = (ConversionConfig) ((ContextBase) context).get("config");
        Path outputDirectory = config.getOutputDirectory();
        ImageFormat format = config.getImageFormat();

        if (image == null) {
            throw new Pdf2ImageException("Image cannot be null");
        }
        if (pageNumber < 1) {
            throw new Pdf2ImageException("Page number must be positive");
        }

        Path outputPath = generateOutputPath(outputDirectory, pageNumber, format);

        try {
            ImageIO.write(image, format.getFormatName(), outputPath.toFile());
            logger.debug("Saved page {} to: {}", pageNumber, outputPath);
        } catch (IOException e) {
            throw new Pdf2ImageException("Failed to write image for page " + pageNumber, e);
        }
    }

    /**
     * Generates the output file path for a given page number.
     * Format: {outputDirectory}/{pageNumber}.{extension}
     *
     * @param pageNumber the page number (1-based)
     * @param format the image format
     * @return the complete output path
     */
    private Path generateOutputPath(Path outputDirectory, int pageNumber, ImageFormat format) {
        String filename = pageNumber + "." + format.getExtension();
        return outputDirectory.resolve(filename);
    }
}