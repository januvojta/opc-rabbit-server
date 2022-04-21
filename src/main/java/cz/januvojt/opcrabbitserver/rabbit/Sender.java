package cz.januvojt.opcrabbitserver.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Sender {
    @Value("${rabbit.receiver.queue.name}")
    private String receiver;
    @Value("${rabbit.queue.name}")
    private String sender;
    private final RabbitTemplate template;

    public Sender(RabbitTemplate template) {
        this.template = template;
    }

    public void sendMessage(String fieldName, String content){
        template.convertAndSend(receiver, new Message(fieldName, new String[]{content},sender, receiver));
    }
}
