package cz.januvojt.opcrabbitserver.rabbit;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
    private final String fieldName;
    private final String[] fieldValues;
    private final String sender;
    private final String receiver;

    public Message(
            @JsonProperty("fieldName") String fieldName,
            @JsonProperty("fieldValues") String[] fieldValues,
            @JsonProperty("sender") String sender,
            @JsonProperty("receiver") String receiver
    ) {
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

    @Override
    public String toString() {
        return "Message{" +
                "fieldName='" + fieldName + '\'' +
                ", fieldValues=" + Arrays.toString(fieldValues) +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                '}';
    }
}
