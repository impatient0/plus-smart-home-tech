package ru.yandex.practicum.telemetry.serializer;

import org.apache.avro.generic.GenericContainer;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BaseAvroSerializer<T extends GenericContainer> implements Serializer<T> {

    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) {
            return null;
        }

        try {
            DatumWriter<T> datumWriter = new SpecificDatumWriter<>(data.getSchema());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);

            datumWriter.write(data, encoder);
            encoder.flush();

            byte[] result = outputStream.toByteArray();
            outputStream.close();
            return result;

        } catch (IOException e) {
            throw new SerializationException("Error serializing Avro message for topic " + topic, e);
        }
    }
}