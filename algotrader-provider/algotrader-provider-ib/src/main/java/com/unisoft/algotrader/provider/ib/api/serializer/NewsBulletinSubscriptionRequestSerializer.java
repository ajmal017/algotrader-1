package com.unisoft.algotrader.provider.ib.api.serializer;

import com.unisoft.algotrader.provider.ib.api.model.system.OutgoingMessageId;

/**
 * Created by alex on 8/7/15.
 */
public class NewsBulletinSubscriptionRequestSerializer extends Serializer{

    private static final int VERSION = 1;
    public NewsBulletinSubscriptionRequestSerializer(int serverCurrentVersion){
        super(serverCurrentVersion, OutgoingMessageId.NEWS_BULLETIN_SUBSCRIPTION_REQUEST);
    }

    public byte [] serialize(boolean includeExistingDailyNews){
        ByteArrayBuilder builder = getByteArrayBuilder();

        builder.append(messageId.getId());
        builder.append(VERSION);
        builder.append(includeExistingDailyNews);

        return builder.toBytes();
    }
}
