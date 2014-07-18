package serverlist;

import lombok.extern.java.Log;
import net.year4000.serverlist.messages.MessageFactory;
import org.junit.Test;

import java.util.Date;

@Log
public class MessageFactoryTest {

    @Test
    public void testMessages(){
        // Today's date
        log.info("Today's top 5 messages");
        display(new MessageFactory(new Date()));

        // On Christmas
        log.info("Christmas's top 5 messages");
        display(new MessageFactory(new Date(1419465600)));
    }

    private void display(MessageFactory factory) {
        for(int i = 0; i < 5; i++){
            log.info("Message picked: " + factory.getMessage() + "\n");
        }
    }
}
