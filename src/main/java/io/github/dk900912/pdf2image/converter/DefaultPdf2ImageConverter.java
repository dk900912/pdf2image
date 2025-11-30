package io.github.dk900912.pdf2image.converter;

import io.github.dk900912.pdf2image.config.ConversionConfig;
import io.github.dk900912.pdf2image.context.Context;
import io.github.dk900912.pdf2image.context.ContextBase;
import io.github.dk900912.pdf2image.exception.Pdf2ImageException;
import io.github.dk900912.pdf2image.processor.ImageProcessor;
import io.github.dk900912.pdf2image.renderer.PageRenderer;
import io.github.dk900912.pdf2image.storage.ImageStorage;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Default implementation of PdfToImageConverter.
 *
 * @author dukui
 */
public class DefaultPdf2ImageConverter implements Pdf2ImageConverter {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPdf2ImageConverter.class);

    private final PageRenderer pageRenderer;
    private final ImageProcessor imageProcessor;
    private final ImageStorage imageStorage;

    public DefaultPdf2ImageConverter(
            PageRenderer pageRenderer,
            ImageProcessor imageProcessor,
            ImageStorage imageStorage) {
        if (pageRenderer == null || imageProcessor == null || imageStorage == null) {
            throw new IllegalArgumentException("All dependencies must be non-null");
        }
        this.pageRenderer = pageRenderer;
        this.imageProcessor = imageProcessor;
        this.imageStorage = imageStorage;
    }

    @Override
    public void convert(Context context) {
        ConversionConfig config = (ConversionConfig) ((ContextBase) context).get("config");
        if (config == null) {
            throw new Pdf2ImageException("Conversion configuration cannot be null");
        }

        Path pdfPath = config.getInputDirectory();

        validateInput(pdfPath);

        logger.info("Starting PDF to image conversion: {}", pdfPath);
        logger.info("Configuration - Format: {}, Mode: {}, Resolution: {} DPI",
                config.getImageFormat(),
                config.getImageMode(),
                config.getResolution().getDpi());

        imageStorage.prepare(context);

        try (PDDocument document = loadDocument(pdfPath)) {
            ((ContextBase) context).put("document", document);

            int totalPages = document.getNumberOfPages();
            logger.info("PDF has {} pages", totalPages);

            PageRange pageRange = determinePageRange(config, totalPages);
            ((ContextBase) context).put("page-range", pageRange);
            logger.info("Processing pages {} to {}", pageRange.start, pageRange.end);

            processPage(context);

            logger.info("Successfully converted {} pages", pageRange.end - pageRange.start + 1);
        } catch (IOException e) {
            throw new Pdf2ImageException("Failed to process PDF: " + pdfPath, e);
        } finally {
            imageStorage.cleanup(context);
        }
    }

    /**
     * Validates input parameters.
     */
    private void validateInput(Path pdfPath) {
        if (pdfPath == null) {
            throw new Pdf2ImageException("PDF path cannot be null");
        }
        if (!Files.exists(pdfPath)) {
            throw new Pdf2ImageException("PDF file does not exist: " + pdfPath);
        }
        if (!Files.isRegularFile(pdfPath)) {
            throw new Pdf2ImageException("Path is not a regular file: " + pdfPath);
        }
    }

    /**
     * Loads the PDF document.
     */
    private PDDocument loadDocument(Path pdfPath) throws IOException {
        logger.debug("Loading PDF document: {}", pdfPath);
        return Loader.loadPDF(pdfPath.toFile());
    }

    /**
     * Determines the page range to process based on configuration.
     */
    private PageRange determinePageRange(ConversionConfig config, int totalPages) {
        int start = config.getStartPage().orElse(1) - 1; // Convert to 0-based
        int end = config.getEndPage().orElse(totalPages) - 1; // Convert to 0-based

        // Validate range
        if (start < 0) {
            start = 0;
        }
        if (end >= totalPages) {
            end = totalPages - 1;
        }
        if (start > end) {
            throw new Pdf2ImageException("Invalid page range: start=" + (start + 1) + ", end=" + (end + 1));
        }

        return new PageRange(start, end);
    }

    /**
     * Processes a single page: render, process, and store.
     */
    private void processPage(Context context) {

        PageRange pageRange = (PageRange) ((ContextBase) context).get("page-range");

        for (int pageIndex = pageRange.start; pageIndex <= pageRange.end; pageIndex++) {
            int pageNumber = pageIndex + 1; // 1-based for display
            ((ContextBase) context).put("page-index", pageIndex);
            logger.debug("Processing page {}", pageNumber);

            try {
                // Render the page
                BufferedImage image = pageRenderer.renderPage(context);
                ((ContextBase) context).put("image", image);

                // Process the image (rotation, cropping, etc.)
                BufferedImage processedImage = imageProcessor.process(context);
                ((ContextBase) context).put("image", processedImage);

                // Store the image
                imageStorage.store(context);

                logger.debug("Successfully processed page {}", pageNumber);
            } catch (Exception e) {
                throw new Pdf2ImageException("Failed to process page " + pageNumber, e);
            }
        }
    }

    /**
     * Simple record to hold page range.
     */
    public static record PageRange(int start, int end) { }
}