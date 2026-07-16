package org.quest.exception;

public class QuestDataException extends RuntimeException {
    public QuestDataException(String message) {
        super(message);
    }

    public QuestDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
