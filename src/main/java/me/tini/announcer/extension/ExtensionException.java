package me.tini.announcer.extension;

public class ExtensionException extends RuntimeException {
    private static final long serialVersionUID = -6471335909735343803L;

    public ExtensionException(String message) {
        super(message);
    }

    public ExtensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
