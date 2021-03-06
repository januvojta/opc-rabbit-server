package cz.januvojt.opcrabbitserver.rabbit;

import cz.januvojt.opcrabbitserver.constants.NodeNames;
import cz.januvojt.opcrabbitserver.OpcOutputs;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class Receiver {
    private final OpcOutputs outputs;
    @Value("${rabbit.receiver.queue.name}")
    private String navServerName;

    public Receiver(OpcOutputs outputs) {
        this.outputs = outputs;
    }

    @RabbitListener(queues = {"${rabbit.queue.name}"})
    public void receiveMsg(Message message) {
        log.info(message.toString());
        if (message.getSender().equals(navServerName)) {
            if (message.getFieldName().equals(NodeNames.operationState)) {
                outputs.setOperationState(message.getFieldValues()[0]);
            } else if (message.getFieldName().equals(NodeNames.operationStarted)) {
                outputs.setOperationStarted(Boolean.parseBoolean(message.getFieldValues()[0]));
            } else if (message.getFieldName().equals(NodeNames.operationFinished)) {
                outputs.setOperationFinished(Boolean.parseBoolean(message.getFieldValues()[0]));
            } else if (message.getFieldName().equals(NodeNames.errorNumber)) {
                outputs.setErrorNumber(Integer.parseInt(message.getFieldValues()[0]));
            } else if (message.getFieldName().equals(NodeNames.dataReady)) {
                outputs.setDataReady(Boolean.parseBoolean(message.getFieldValues()[0]));
            } else if (message.getFieldName().equals(NodeNames.errorString)) {
                outputs.setErrorString(message.getFieldValues()[0]);
            } else {
                log.error("Unknown field name: " + message.getFieldName());
            }
        }else {
            log.error("Unknown sender: " + message.getSender());
        }

    }
}
