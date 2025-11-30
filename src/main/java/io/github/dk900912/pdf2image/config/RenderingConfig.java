package io.github.dk900912.pdf2image.config;

/**
 * Configuration for rendering quality settings.
 * Includes anti-aliasing and other rendering hints.
 *
 * @author dukui
 */
public final class RenderingConfig {
    private final boolean enableAntiAliasing;
    private final boolean enableTextAntiAliasing;
    private final boolean enableFractionalMetrics;

    private RenderingConfig(Builder builder) {
        this.enableAntiAliasing = builder.enableAntiAliasing;
        this.enableTextAntiAliasing = builder.enableTextAntiAliasing;
        this.enableFractionalMetrics = builder.enableFractionalMetrics;
    }

    public boolean isEnableAntiAliasing() {
        return enableAntiAliasing;
    }

    public boolean isEnableTextAntiAliasing() {
        return enableTextAntiAliasing;
    }

    public boolean isEnableFractionalMetrics() {
        return enableFractionalMetrics;
    }

    /**
     * Creates a default configuration with all quality enhancements enabled.
     *
     * @return default rendering configuration
     */
    public static RenderingConfig defaultConfig() {
        return builder()
                .enableAntiAliasing(true)
                .enableTextAntiAliasing(true)
                .enableFractionalMetrics(true)
                .build();
    }

    /**
     * Creates a fast configuration with quality enhancements disabled.
     *
     * @return fast rendering configuration
     */
    public static RenderingConfig fastConfig() {
        return builder()
                .enableAntiAliasing(false)
                .enableTextAntiAliasing(false)
                .enableFractionalMetrics(false)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean enableAntiAliasing = true;
        private boolean enableTextAntiAliasing = true;
        private boolean enableFractionalMetrics = true;

        public Builder enableAntiAliasing(boolean enable) {
            this.enableAntiAliasing = enable;
            return this;
        }

        public Builder enableTextAntiAliasing(boolean enable) {
            this.enableTextAntiAliasing = enable;
            return this;
        }

        public Builder enableFractionalMetrics(boolean enable) {
            this.enableFractionalMetrics = enable;
            return this;
        }

        public RenderingConfig build() {
            return new RenderingConfig(this);
        }
    }
}