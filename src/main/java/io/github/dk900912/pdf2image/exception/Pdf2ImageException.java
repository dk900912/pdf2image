package io.github.dk900912.pdf2image.exception;

/**
 * Base exception for PDF to image conversion operations.
 *
 * @author dukui
 */
public class Pdf2ImageException extends RuntimeException {

    public Pdf2ImageException(String message) {
        super(message);
    }

    public Pdf2ImageException(String message, Throwable cause) {
        super(message, cause);
    }

    public Pdf2ImageException(Throwable cause) {
        super(cause);
    }
}