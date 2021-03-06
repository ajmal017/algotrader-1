package com.unisoft.algotrader.provider.ib;

import com.unisoft.algotrader.provider.ib.api.deserializer.Deserializer;
import com.unisoft.algotrader.provider.ib.api.deserializer.Deserializers;
import com.unisoft.algotrader.provider.ib.api.event.IBEventHandler;
import com.unisoft.algotrader.provider.ib.api.exception.IOStreamException;
import com.unisoft.algotrader.provider.ib.api.model.system.IncomingMessageId;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;

import static com.unisoft.algotrader.provider.ib.InputStreamUtils.readInt;

/**
 * Created by alex on 8/1/15.
 */
public class EventInputStreamConsumer implements Runnable {

    private static final Logger LOG = LogManager.getLogger(EventInputStreamConsumer.class);
    
    private final Deserializers deserializers;
    private final IBEventHandler eventHandler;
    private final IBSocket ibSocket;
    private final InputStream inputStream;
    private boolean running = true;

    public EventInputStreamConsumer(
            IBEventHandler eventHandler,
            IBSocket ibSocket,
            Deserializers deserializers){
        this.eventHandler = eventHandler;
        this.ibSocket = ibSocket;
        this.deserializers = deserializers;
        this.inputStream = ibSocket.getInputStream();
    }

    public void run(){
        try {
            while (running) {
                consumeMessage();
            }
        }
        catch(IOStreamException e){
            LOG.error("fail to consume message", e);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private void consumeMessage() {
        final int messageId = readInt(inputStream);
        final IncomingMessageId incomingMessageId = IncomingMessageId.fromId(messageId);
        LOG.info("consumeMessage, incomingMessageId {}", incomingMessageId);
        final Deserializer serializer =
                deserializers.getDeserializer(incomingMessageId);
        serializer.consume(inputStream, eventHandler);
    }

    public void stop(){
        running = false;
    }

}
