package com.terraforged.cereal;

import com.terraforged.cereal.serial.DataReader;
import com.terraforged.cereal.serial.DataWriter;
import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataSpecs;
import com.terraforged.cereal.value.DataList;
import com.terraforged.cereal.value.DataObject;
import com.terraforged.cereal.value.DataValue;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Cereal {

    public static <T> T read(Reader reader, Class<T> type) throws IOException {
        DataValue data = new DataReader(reader).read();
        return deserialize(data.asObj(), type, Context.NONE);
    }

    public static <T> T read(Reader reader, Class<T> type, Context context) throws IOException {
        DataValue data = new DataReader(reader).read();
        return deserialize(data.asObj(), type, context);
    }

    public static <T> List<T> readList(Reader reader, Class<T> type) throws IOException {
        DataValue data = new DataReader(reader).read();
        return deserialize(data.asList(), type, Context.NONE);
    }

    public static <T> List<T> readList(Reader reader, Class<T> type, Context context) throws IOException {
        DataValue data = new DataReader(reader).read();
        return deserialize(data.asList(), type, context);
    }

    public static void write(Object object, Writer writer) throws IOException {
        write(object, writer, Context.NONE);
    }

    public static void write(Object object, Writer writer, Context context) throws IOException {
        DataWriter dataWriter = new DataWriter(writer);
        DataValue value = serialize(object, context);
        dataWriter.write(value);
    }

    public static void write(Object object, String type, Writer writer) throws IOException {
        write(object, type, writer, Context.NONE);
    }

    public static void write(Object object, String type, Writer writer, Context context) throws IOException {
        DataWriter dataWriter = new DataWriter(writer);
        DataValue value = serialize(type, object, context);
        dataWriter.write(value);
    }

    public static DataValue serialize(Object value) {
        return serialize(value, Context.NONE);
    }

    public static DataValue serialize(Object value, Context context) {
        return DataValue.of(value, context);
    }

    public static DataValue serialize(String type, Object value) {
        return serialize(type, value, Context.NONE);
    }

    public static DataValue serialize(String type, Object value, Context context) {
        return DataSpecs.getSpec(type).serialize(value, context);
    }

    public static Object deserialize(DataObject data) {
        return deserialize(data, Context.NONE);
    }

    public static Object deserialize(DataObject data, Context context) {
        return DataSpecs.getSpec(data.getType()).deserialize(data, context);
    }

    public static <T> T deserialize(DataObject data, Class<T> type, Context context) {
        return DataSpecs.getSpec(data.getType()).deserialize(data, type, context);
    }

    public static <T> List<T> deserialize(DataList data, Class<T> type) {
        return deserialize(data, type, Context.NONE);
    }

    public static <T> List<T> deserialize(DataList data, Class<T> type, Context context) {
        List<T> list = new ArrayList<>(data.size());
        for (DataValue value : data) {
            if (value.isObj()) {
                list.add(deserialize(value.asObj(), type, context));
            }
        }
        return list;
    }
}
