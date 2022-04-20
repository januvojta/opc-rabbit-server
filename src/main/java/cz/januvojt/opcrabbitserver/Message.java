package cz.januvojt.opcrabbitserver;

public class Message {
    private final String fieldName;
    private final String[] fieldValues;
    private final String sender;
    private final String receiver;

    public Message(String fieldName, String[] fieldValues, String sender, String receiver) {
        this.fieldName = fieldName;
        this.fieldValues = fieldValues;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String[] getFieldValues() {
        return fieldValues;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
}
