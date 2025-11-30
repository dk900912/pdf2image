package io.github.dk900912.pdf2image.processor;

import io.github.dk900912.pdf2image.config.ConversionConfig;
import io.github.dk900912.pdf2image.config.ImageMode;
import io.github.dk900912.pdf2image.context.Context;
import io.github.dk900912.pdf2image.context.ContextBase;
import io.github.dk900912.pdf2image.exception.Pdf2ImageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Default implementation of ImageProcessor.
 * Handles rotation, cropping, and color mode conversion.
 *
 * @author dukui
 */
public class DefaultImageProcessor implements ImageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DefaultImageProcessor.class);

    @Override
    public BufferedImage process(Context context) {
        BufferedImage image = (BufferedImage) ((ContextBase) context).get("image");
        ConversionConfig config = (ConversionConfig) ((ContextBase) context).get("config");

        if (image == null) {
            throw new Pdf2ImageException("Image cannot be null");
        }

        BufferedImage processed = image;

        // Apply rotation if needed
        if (config.getRotationDegrees() != 0) {
            processed = rotate(processed, config.getRotationDegrees());
            logger.debug("Applied rotation: {} degrees", config.getRotationDegrees());
        }

        // Apply cropping if enabled
        if (config.isEnableCropping()) {
            // Cropping logic can be extended based on specific requirements
            // For now, we'll keep the full image
            logger.debug("Cropping is enabled but not yet implemented");
        }

        // Convert to target image mode if needed
        processed = convertImageMode(processed, config.getImageMode());

        return processed;
    }

    /**
     * Rotates an image by the specified degrees.
     *
     * @param image the source image
     * @param degrees the rotation angle in degrees
     * @return the rotated image
     */
    private BufferedImage rotate(BufferedImage image, int degrees) {
        double radians = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));

        int width = image.getWidth();
        int height = image.getHeight();

        int newWidth = (int) Math.floor(width * cos + height * sin);
        int newHeight = (int) Math.floor(height * cos + width * sin);

        BufferedImage rotated = new BufferedImage(
                newWidth,
                newHeight,
                image.getType()
        );

        Graphics2D g2d = rotated.createGraphics();

        // Apply rendering hints for quality
        g2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );
        g2d.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        AffineTransform at = new AffineTransform();
        at.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0);
        at.rotate(radians, width / 2.0, height / 2.0);

        g2d.setTransform(at);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    /**
     * Converts an image to the target color mode if needed.
     *
     * @param image the source image
     * @param targetMode the target image mode
     * @return the converted image
     */
    private BufferedImage convertImageMode(
            BufferedImage image,
            ImageMode targetMode
    ) {
        int targetType = targetMode.getBufferedImageType();

        // If already in target mode, return as-is
        if (image.getType() == targetType) {
            return image;
        }

        // Create new image with target type
        BufferedImage converted = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                targetType
        );

        Graphics2D g2d = converted.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        logger.debug("Converted image mode to: {}", targetMode);
        return converted;
    }
}