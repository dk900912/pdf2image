package io.github.dk900912.pdf2image.storage;

import io.github.dk900912.pdf2image.context.Context;
import io.github.dk900912.pdf2image.exception.Pdf2ImageException;

/**
 * Strategy interface for storing converted images.
 * Allows different storage implementations (local, remote database, cloud storage, etc.)
 *
 * @author dukui
 */
public interface ImageStorage {

    /**
     * Stores an image with the specified page number.
     *
     * @param context the conversion context
     * @exception Pdf2ImageException if storage fails
     */
    void store(Context context);

    /**
     * Prepares the storage for a batch operation (optional operation).
     * This can be used to create directories, establish connections, etc.
     */
    default void prepare(Context context) {
        // Default implementation does nothing
    }

    /**
     * Cleans up resources after storage operations (optional operation).
     */
    default void cleanup(Context context) {
        // Default implementation does nothing
    }
}