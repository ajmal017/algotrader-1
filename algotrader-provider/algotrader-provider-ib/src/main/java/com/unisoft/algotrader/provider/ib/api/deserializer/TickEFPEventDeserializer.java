package com.unisoft.algotrader.provider.ib.api.deserializer;

import com.unisoft.algotrader.provider.ib.api.IBConstants;
import com.unisoft.algotrader.provider.ib.api.IBSession;
import com.unisoft.algotrader.provider.ib.api.IncomingMessageId;

import java.io.InputStream;

import static com.unisoft.algotrader.provider.ib.api.InputStreamUtils.*;


/**
 * Created by alex on 8/13/15.
 */
public class TickEFPEventDeserializer extends Deserializer {


    public TickEFPEventDeserializer(){
        super(IncomingMessageId.TICK_EXCHANFE_FOR_PHYSICAL);
    }

    @Override
    public void consumeVersionLess(final int version, final InputStream inputStream, final IBSession ibSession) {
        final int requestId = readInt(inputStream);
        final int tickType = readInt(inputStream);
        final double basisPoints = readDouble(inputStream);
        final String formattedBasisPoints = readString(inputStream);
        final double impliedFuturePrice = readDouble(inputStream);
        final int holdDays = readInt(inputStream);
        final String futureExpiry = readString(inputStream);
        final double dividendImpact = readDouble(inputStream);
        final double dividendToExpiry = readDouble(inputStream);
        
        ibSession.onTickEfpEvent(requestId, IBConstants.TickType.fromValue(tickType),  basisPoints,
        formattedBasisPoints, impliedFuturePrice, holdDays,futureExpiry, dividendImpact, dividendToExpiry);
    }
}