package com.dragonslair.bcintredux.bigcommerce.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class BigCommerceDateSerializer extends StdSerializer<ZonedDateTime> {

    protected BigCommerceDateSerializer(Class<ZonedDateTime> t) {
        super(t);
    }

    public BigCommerceDateSerializer() {
        this(null);
    }

    private static final long serialVersionUID = 667195122379001266L;
    private DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider arg2)
            throws IOException, JsonProcessingException {
        gen.writeString(formatter.format(value));
    }
}