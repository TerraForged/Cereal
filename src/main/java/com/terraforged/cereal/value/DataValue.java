package com.terraforged.cereal.value;

import com.terraforged.cereal.spec.DataSpecs;
import com.terraforged.cereal.spec.SpecName;
import com.terraforged.cereal.serial.DataWriter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataValue {

    public static final DataValue NULL = new DataValue(null);

    protected final Object value;

    protected DataValue(Object value) {
        this.value = value;
    }

    public boolean isObj() {
        return this instanceof DataObject;
    }

    public boolean isList() {
        return this instanceof DataList;
    }

    public boolean isNull() {
        return this == NULL;
    }

    public boolean isNonNull() {
        return !isNull();
    }

    public Number asNum() {
        return value instanceof Number ? (Number) value : 0;
    }

    public DataValue inc(int amount) {
        return new DataValue(asInt() + amount);
    }

    public DataValue inc(double amount) {
        return new DataValue(asDouble() + amount);
    }

    public byte asByte() {
        return asNum().byteValue();
    }

    public int asInt() {
        return asNum().intValue();
    }

    public short aShort() {
        return asNum().shortValue();
    }

    public long asLong() {
        return asNum().longValue();
    }

    public float asFloat() {
        return asNum().floatValue();
    }

    public double asDouble() {
        return asNum().doubleValue();
    }

    public String asString() {
        return value == null ? "null" : value.toString();
    }

    public <E extends Enum<E>> E asEnum(Class<E> type) {
        return Enum.valueOf(type, asString());
    }

    public DataList asList() {
        return this instanceof DataList ? (DataList) this : DataList.NULL_LIST;
    }

    public DataObject asObj() {
        return this instanceof DataObject ? (DataObject) this : DataObject.NULL_OBJ;
    }

    public DataList toList() {
        return this instanceof DataList ? (DataList) this : new DataList().add(this);
    }

    public boolean asBool() {
        if (value instanceof Boolean) {
            return (boolean) value;
        }
        if (value instanceof String) {
            return value.toString().equalsIgnoreCase("true");
        }
        return asNum().byteValue() == 1;
    }

    public void appendTo(DataWriter writer) throws IOException {
        writer.value(value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataValue value1 = (DataValue) o;
        return Objects.equals(value, value1.value);
    }

    @Override
    public String toString() {
        return asString();
    }

    public static DataValue of(Object value) {
        if (value instanceof DataValue) {
            return (DataValue) value;
        }
        if (value instanceof Number) {
            return new DataValue(value);
        }
        if (value instanceof String) {
            return new DataValue(value);
        }
        if (value instanceof Boolean) {
            return new DataValue(value);
        }

        if (value instanceof SpecName) {
            String name = ((SpecName) value).getSpecName();
            return DataSpecs.getSpec(name).serialize(value);
        }
        if (value instanceof Enum<?>) {
            return new DataValue(((Enum<?>) value).name());
        }
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            DataList data = new DataList(list.size());
            for (Object o : list) {
                data.add(DataValue.of(o));
            }
            return data;
        }
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            DataObject data = new DataObject();
            for (Map.Entry<?, ?> e : map.entrySet()) {
                data.add(e.getKey().toString(), DataValue.of(e.getValue()));
            }
            return data;
        }
        if (value != null) {
            String name = value.getClass().getSimpleName();
            if (DataSpecs.hasSpec(name)) {
                return DataSpecs.getSpec(name).serialize(value);
            }
        }
        return NULL;
    }
}
