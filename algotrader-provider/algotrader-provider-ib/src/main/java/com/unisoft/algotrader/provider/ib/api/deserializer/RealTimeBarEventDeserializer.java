package com.unisoft.algotrader.provider.ib.api.deserializer;

import com.unisoft.algotrader.provider.ib.api.event.IBEventHandler;
import com.unisoft.algotrader.provider.ib.api.event.RealTimeBarEvent;
import com.unisoft.algotrader.provider.ib.api.model.system.IncomingMessageId;

import java.io.InputStream;

import static com.unisoft.algotrader.provider.ib.InputStreamUtils.*;

/**
 * Created by alex on 8/13/15.
 */
public class RealTimeBarEventDeserializer extends Deserializer<RealTimeBarEvent> {


    public RealTimeBarEventDeserializer(int serverCurrentVersion){
        super(IncomingMessageId.REAL_TIME_BAR, serverCurrentVersion);
    }

    @Override
    public void consumeMessageContent(final int version, final InputStream inputStream, final IBEventHandler eventHandler) {
        final int requestId = readInt(inputStream);
        final long timestamp = readLong(inputStream);
        final double open = readDouble(inputStream);
        final double high = readDouble(inputStream);
        final double low = readDouble(inputStream);
        final double close = readDouble(inputStream);
        final long volume = readLong(inputStream);
        final double weightedAveragePrice = readDouble(inputStream);
        final int tradeNumber = readInt(inputStream);
        eventHandler.onRealTimeBarEvent(requestId, timestamp, open,
        high, low, close, volume, weightedAveragePrice,  tradeNumber);
    }
}