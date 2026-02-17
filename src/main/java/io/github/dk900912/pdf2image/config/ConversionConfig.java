package io.github.dk900912.pdf2image.config;

import java.nio.file.Path;
import java.util.Optional;

import io.github.dk900912.pdf2image.converter.ConversionTaskListener;
import io.github.dk900912.pdf2image.storage.OutputPathStrategy;
import io.github.dk900912.pdf2image.storage.PrefixOutputPathStrategy;

/**
 * Configuration for PDF to image conversion.
 * Immutable configuration object following builder pattern.
 *
 * @author dukui
 */
public final class ConversionConfig {

    /**
     * Input directory containing the PDF files to convert.
     */
    private final Path inputDirectory;

    /**
     * Output directory where the converted images will be saved.
     */
    private final Path outputDirectory;

    /**
     * Image format to use for the converted images.
     */
    private final ImageFormat imageFormat;

    /**
     * Image mode to use for the converted images.
     */
    private final ImageMode imageMode;

    /**
     * Resolution to use for the converted images.
     */
    private final Resolution resolution;

    /**
     * Rendering configuration to use for the converted images.
     */
    private final RenderingConfig renderingConfig;

    /**
     * Page range to convert.
     * If not specified, all pages will be converted.
     */
    private final Integer startPage;
    private final Integer endPage;

    /**
     * Whether to enable cropping of the converted images.
     */
    private final boolean enableCropping;

    /**
     * Rotation angle to apply to the converted images.
     */
    private final int rotationDegrees;

    /**
     * Output path strategy to use for naming converted images.
     */
    private final OutputPathStrategy outputPathStrategy;

    /**
     * Optional listener invoked when all PDFs are converted.
     */
    private final ConversionTaskListener taskListener;

    private ConversionConfig(Builder builder) {
        this.inputDirectory = builder.inputDirectory;
        this.outputDirectory = builder.outputDirectory;
        this.imageFormat = builder.imageFormat;
        this.imageMode = builder.imageMode;
        this.resolution = builder.resolution;
        this.renderingConfig = builder.renderingConfig;
        this.startPage = builder.startPage;
        this.endPage = builder.endPage;
        this.enableCropping = builder.enableCropping;
        this.rotationDegrees = builder.rotationDegrees;
        this.outputPathStrategy = builder.outputPathStrategy;
        this.taskListener = builder.taskListener;
    }

    public Path getInputDirectory() {
        return inputDirectory;
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }

    public ImageFormat getImageFormat() {
        return imageFormat;
    }

    public ImageMode getImageMode() {
        return imageMode;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public RenderingConfig getRenderingConfig() {
        return renderingConfig;
    }

    public Optional<Integer> getStartPage() {
        return Optional.ofNullable(startPage);
    }

    public Optional<Integer> getEndPage() {
        return Optional.ofNullable(endPage);
    }

    public boolean isEnableCropping() {
        return enableCropping;
    }

    public int getRotationDegrees() {
        return rotationDegrees;
    }

    public OutputPathStrategy getOutputPathStrategy() {
        return outputPathStrategy;
    }

    public ConversionTaskListener getTaskListener() {
        return taskListener;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Path inputDirectory;
        private Path outputDirectory;
        private ImageFormat imageFormat = ImageFormat.PNG;
        private ImageMode imageMode = ImageMode.COLOR;
        private Resolution resolution = Resolution.MEDIUM;
        private RenderingConfig renderingConfig = RenderingConfig.defaultConfig();
        private Integer startPage;
        private Integer endPage;
        private boolean enableCropping = false;
        private int rotationDegrees = 0;
        private OutputPathStrategy outputPathStrategy = new PrefixOutputPathStrategy("");
        private ConversionTaskListener taskListener;

        public Builder inputDirectory(Path path) {
            this.inputDirectory = path;
            return this;
        }

        public Builder outputDirectory(Path path) {
            this.outputDirectory = path;
            return this;
        }

        public Builder imageFormat(ImageFormat format) {
            this.imageFormat = format;
            return this;
        }

        public Builder imageMode(ImageMode mode) {
            this.imageMode = mode;
            return this;
        }

        public Builder resolution(Resolution resolution) {
            this.resolution = resolution;
            return this;
        }

        public Builder renderingConfig(RenderingConfig config) {
            this.renderingConfig = config;
            return this;
        }

        public Builder pageRange(int start, int end) {
            this.startPage = start;
            this.endPage = end;
            return this;
        }

        public Builder enableCropping(boolean enable) {
            this.enableCropping = enable;
            return this;
        }

        public Builder rotation(int degrees) {
            this.rotationDegrees = degrees % 360;
            return this;
        }

        public Builder outputPathStrategy(OutputPathStrategy outputPathStrategy) {
            this.outputPathStrategy = outputPathStrategy;
            return this;
        }

        public Builder taskListener(ConversionTaskListener taskListener) {
            this.taskListener = taskListener;
            return this;
        }

        public ConversionConfig build() {
            if (inputDirectory == null) {
                throw new IllegalStateException("Input directory must be specified");
            }
            if (outputDirectory == null) {
                throw new IllegalStateException("Output directory must be specified");
            }
            return new ConversionConfig(this);
        }
    }
}