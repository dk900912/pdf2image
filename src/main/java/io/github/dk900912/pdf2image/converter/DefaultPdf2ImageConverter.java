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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        Path inputDirectory = config.getInputDirectory();

        validateInputDirectory(inputDirectory);

        List<Path> pdfFiles = listPdfFiles(inputDirectory);
        if (pdfFiles.isEmpty()) {
            logger.warn("No PDF files found in directory: {}", inputDirectory);
            return;
        }

        logger.info("Starting PDF to image conversion in directory: {}", inputDirectory);
        logger.info("Configuration - Format: {}, Mode: {}, Resolution: {} DPI",
                config.getImageFormat(),
                config.getImageMode(),
                config.getResolution().getDpi());

        for (Path pdfPath : pdfFiles) {
            validateInputFile(pdfPath);
            Path pdfOutputDirectory = resolvePdfOutputDirectory(config.getOutputDirectory(), pdfPath);
            ((ContextBase) context).put("input-pdf", pdfPath);
            ((ContextBase) context).put("output-directory", pdfOutputDirectory);
            List<String> outputDirectories = (List<String>) ((ContextBase) context).get("output-directories");
            if (outputDirectories == null) {
                ((ContextBase) context).put("output-directories", new ArrayList<String>());
            } else {
                outputDirectories.add(pdfOutputDirectory.getFileName().toString());
                ((ContextBase) context).put("output-directories", outputDirectories);
            }

            logger.info("Processing PDF: {}", pdfPath);
            imageStorage.prepare(context);

            boolean convertedSuccessfully = false;
            try (PDDocument document = loadDocument(pdfPath)) {
                ((ContextBase) context).put("document", document);

                int totalPages = document.getNumberOfPages();
                logger.info("PDF has {} pages", totalPages);

                PageRange pageRange = determinePageRange(config, totalPages);
                ((ContextBase) context).put("page-range", pageRange);
                logger.info("Processing pages {} to {}", pageRange.start, pageRange.end);

                processPage(context);

                logger.info("Successfully converted {} pages", pageRange.end - pageRange.start + 1);
                convertedSuccessfully = true;
            } catch (IOException e) {
                throw new Pdf2ImageException("Failed to process PDF: " + pdfPath, e);
            } finally {
                imageStorage.cleanup(context);
                if (convertedSuccessfully) {
                    ConversionTaskListener taskListener = config.getTaskListener();
                    if (taskListener != null) {
                        taskListener.onTaskCompleted(context, pdfPath);
                    }
                }
            }
        }

        ConversionTaskListener taskListener = config.getTaskListener();
        if (taskListener != null) {
            taskListener.onAllTaskCompleted(context);
        }
    }

    /**
     * Validates input directory parameters.
     */
    private void validateInputDirectory(Path inputDirectory) {
        if (inputDirectory == null) {
            throw new Pdf2ImageException("Input directory cannot be null");
        }
        if (!Files.exists(inputDirectory)) {
            throw new Pdf2ImageException("Input directory does not exist: " + inputDirectory);
        }
        if (!Files.isDirectory(inputDirectory)) {
            throw new Pdf2ImageException("Path is not a directory: " + inputDirectory);
        }
    }

    /**
     * Validates input file parameters.
     */
    private void validateInputFile(Path pdfPath) {
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
     * Lists all PDF files in the input directory.
     */
    private List<Path> listPdfFiles(Path inputDirectory) {
        try (Stream<Path> listed = Files.list(inputDirectory)) {
            return listed
                    .filter(Files::isRegularFile)
                    .filter(this::isPdfFile)
                    .sorted(Comparator.comparing(Path::getFileName))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new Pdf2ImageException("Failed to list files in directory: " + inputDirectory, e);
        }
    }

    /**
     * Checks if the given path is a PDF file.
     */
    private boolean isPdfFile(Path path) {
        String filename = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return filename.endsWith(".pdf");
    }

    private Path resolvePdfOutputDirectory(Path outputDirectory, Path pdfPath) {
        String filename = pdfPath.getFileName().toString();
        int extensionIndex = filename.lastIndexOf('.');
        String baseName = extensionIndex > 0 ? filename.substring(0, extensionIndex) : filename;
        return outputDirectory.resolve(baseName);
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
     * Note: start and end are 0-based indices representing the actual page positions in the document
     * (e.g., first page is 0, second page is 1, etc.)
     *
     * @param start 0-based index of the first page to process
     * @param end 0-based index of the last page to process (inclusive)
     */
    public static record PageRange(int start, int end) { }
}
