package io.github.dk900912.pdf2image;

import io.github.dk900912.pdf2image.config.ConversionConfig;
import io.github.dk900912.pdf2image.config.ImageFormat;
import io.github.dk900912.pdf2image.config.ImageMode;
import io.github.dk900912.pdf2image.config.RenderingConfig;
import io.github.dk900912.pdf2image.config.Resolution;
import io.github.dk900912.pdf2image.context.Context;
import io.github.dk900912.pdf2image.context.ContextBase;
import io.github.dk900912.pdf2image.converter.ConversionTaskListener;
import io.github.dk900912.pdf2image.converter.Pdf2ImageConverter;
import io.github.dk900912.pdf2image.storage.PrefixOutputPathStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for Pdf2ImageService.
 * Demonstrates usage patterns and validates functionality.
 *
 * @author dukui
 */
public class Pdf2ImageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void testConfigurationBuilder() {
        // Test configuration building
        ConversionConfig config = ConversionConfig.builder()
                .inputDirectory(tempDir)
                .outputDirectory(tempDir)
                .imageFormat(ImageFormat.PNG)
                .imageMode(ImageMode.COLOR)
                .resolution(Resolution.HIGH)
                .pageRange(1, 5)
                .rotation(90)
                .build();

        assertNotNull(config);
        assertEquals(ImageFormat.PNG, config.getImageFormat());
        assertEquals(ImageMode.COLOR, config.getImageMode());
        assertEquals(Resolution.HIGH, config.getResolution());
        assertEquals(90, config.getRotationDegrees());
        assertTrue(config.getStartPage().isPresent());
        assertEquals(1, config.getStartPage().get());
    }

    @Test
    void testConfigurationWithDefaults() {
        // Test that builder provides sensible defaults
        ConversionConfig config = ConversionConfig.builder()
                .inputDirectory(tempDir)
                .outputDirectory(tempDir)
                .build();

        assertNotNull(config);
        assertEquals(ImageFormat.PNG, config.getImageFormat());
        assertEquals(ImageMode.COLOR, config.getImageMode());
        assertEquals(Resolution.MEDIUM, config.getResolution());
        assertEquals(0, config.getRotationDegrees());
        assertFalse(config.isEnableCropping());
    }

    @Test
    void testRenderingConfigBuilder() {
        // Test rendering configuration
        RenderingConfig config = RenderingConfig.builder()
                .enableAntiAliasing(true)
                .enableTextAntiAliasing(true)
                .enableFractionalMetrics(false)
                .build();

        assertTrue(config.isEnableAntiAliasing());
        assertTrue(config.isEnableTextAntiAliasing());
        assertFalse(config.isEnableFractionalMetrics());
    }

    @Test
    void testDefaultRenderingConfig() {
        RenderingConfig config = RenderingConfig.defaultConfig();
        assertTrue(config.isEnableAntiAliasing());
        assertTrue(config.isEnableTextAntiAliasing());
        assertTrue(config.isEnableFractionalMetrics());
    }

    @Test
    void testFastRenderingConfig() {
        RenderingConfig config = RenderingConfig.fastConfig();
        assertFalse(config.isEnableAntiAliasing());
        assertFalse(config.isEnableTextAntiAliasing());
        assertFalse(config.isEnableFractionalMetrics());
    }

    /**
     * Example integration test (requires actual PDF file).
     * Uncomment and provide a real PDF path to test actual conversion.
     */
    @Test
    void testActualConversion() throws IOException, URISyntaxException {
        Path pdfPath = Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("sample.pdf")).toURI());

        Path inputDir = tempDir.resolve("input");
        Files.createDirectories(inputDir);
        Path inputPdf = inputDir.resolve(pdfPath.getFileName());
        Files.copy(pdfPath, inputPdf);

        Path outputDir = tempDir.resolve("output");

        int startPage = 1;
        int endPage = 2;

        ConversionConfig config = ConversionConfig.builder()
                .inputDirectory(inputDir)
                .outputDirectory(outputDir)
                .imageFormat(ImageFormat.PNG)
                .imageMode(ImageMode.COLOR)
                .resolution(Resolution.HIGH)
                .pageRange(startPage, endPage)
                .build();
        ContextBase context = new ContextBase();
        context.put("config", config);
        Pdf2ImageConverter pdf2ImageConverter = Pdf2ImageConverter.createDefaultConverter();
        pdf2ImageConverter.convert(context);

        // Verify output files were created
        Path pdfOutputDir = outputDir.resolve("sample");
        try (Stream<Path> listed = Files.list(pdfOutputDir)) {
            assertEquals(endPage - startPage + 1, listed.count());
        }
    }

//    @Test
    void testApp() {
        ContextBase context = new ContextBase();
        ConversionConfig config = ConversionConfig.builder()
                .inputDirectory(Paths.get("C:\\Users\\dk900\\Documents\\实质审查案子\\13"))
                .outputDirectory(Paths.get("C:\\Users\\dk900\\Pictures\\专利审查指南"))
                .imageFormat(ImageFormat.JPEG)
                .imageMode(ImageMode.COLOR)
                .resolution(Resolution.HIGH)
                .outputPathStrategy(new PrefixOutputPathStrategy("fuck-"))
                .taskListener(new ConversionTaskListener() {
                    @Override
                    public void onTaskCompleted(Context context, Path pdfPath) {
                        System.out.println("Completed: " + pdfPath.getFileName());
                    }

                    @Override
                    public void onAllTaskCompleted(Context context) {
                        System.out.println(context);
                        System.out.println("All tasks completed.");
                    }
                })
                .renderingConfig(RenderingConfig.builder()
                        .enableAntiAliasing(true)
                        .enableTextAntiAliasing(true)
                        .enableFractionalMetrics(true)
                        .build()
                )
                .build();
        context.put("config", config);
        Pdf2ImageConverter pdf2ImageConverter = Pdf2ImageConverter.createDefaultConverter();
        pdf2ImageConverter.convert(context);
    }
}