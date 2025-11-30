package io.github.dk900912.pdf2image.renderer;

import io.github.dk900912.pdf2image.config.ConversionConfig;
import io.github.dk900912.pdf2image.config.ImageMode;
import io.github.dk900912.pdf2image.config.RenderingConfig;
import io.github.dk900912.pdf2image.context.Context;
import io.github.dk900912.pdf2image.context.ContextBase;
import io.github.dk900912.pdf2image.exception.Pdf2ImageException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * PDFBox implementation of PageRenderer.
 * Handles the actual rendering of PDF pages using PDFBox library.
 *
 * @author dukui
 */
public class PdfBoxPageRenderer implements PageRenderer {

    private static final Logger logger = LoggerFactory.getLogger(PdfBoxPageRenderer.class);

    @Override
    public BufferedImage renderPage(Context context) {

        PDDocument document = (PDDocument) ((ContextBase) context).get("document");
        int pageIndex = (int) ((ContextBase) context).get("page-index");
        ConversionConfig config = (ConversionConfig) ((ContextBase) context).get("config");

        try {
            PDFRenderer renderer = createRenderer(document, config.getRenderingConfig());
            float scale = calculateScale(config.getResolution().getDpi());
            ImageType imageType = mapImageMode(config.getImageMode());

            logger.debug("Rendering page {} with DPI {} and image type {}",
                    pageIndex + 1, config.getResolution().getDpi(), imageType);

            return renderer.renderImage(pageIndex, scale, imageType);
        } catch (IOException e) {
            throw new Pdf2ImageException("Failed to render page " + (pageIndex + 1), e);
        }
    }

    /**
     * Creates a PDFRenderer with appropriate rendering hints.
     */
    private PDFRenderer createRenderer(PDDocument document, RenderingConfig config) {
        return new PDFRenderer(document);
    }

    /**
     * Calculates the scale factor from DPI.
     * Standard DPI is 72, so scale = targetDPI / 72
     */
    private float calculateScale(int dpi) {
        return dpi / 72f;
    }

    /**
     * Maps our ImageMode enum to PDFBox ImageType.
     */
    private ImageType mapImageMode(ImageMode mode) {
        return switch (mode) {
            case COLOR -> ImageType.RGB;
            case GRAYSCALE -> ImageType.GRAY;
            case BLACK_AND_WHITE -> ImageType.BINARY;
        };
    }
}