package io.github.dk900912.pdf2image.converter;

import io.github.dk900912.pdf2image.context.Context;

import java.nio.file.Path;

/**
 * Listener for conversion task lifecycle events.
 *
 * @author dukui
 */
public interface ConversionTaskListener {

    /**
     * Invoked after all PDFs in the input directory have been converted.
     *
     * @param context the conversion context
     */
    void onAllTaskCompleted(Context context);

    /**
     * Invoked after a single PDF has been converted successfully.
     *
     * @param context the conversion context
     * @param pdfPath the PDF file path that finished conversion
     */
    default void onTaskCompleted(Context context, Path pdfPath) {
        // Default no-op for backward compatibility
    }
}