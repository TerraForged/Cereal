package com.terraforged.cereal.spec;

import com.terraforged.cereal.Cereal;
import com.terraforged.cereal.value.DataList;
import com.terraforged.cereal.value.DataObject;
import com.terraforged.cereal.value.DataValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataSpec<T> {

    private final String name;
    private final Class<T> type;
    private final boolean ignoreDefaults;
    private final DataFactory<T> constructor;
    private final Map<String, Function<T, ?>> accessors;
    private final Map<String, Supplier<DataValue>> defaults;

    public DataSpec(Builder<T> builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.defaults = builder.defaults;
        this.accessors = builder.accessors;
        this.constructor = builder.constructor;
        this.ignoreDefaults = builder.ignoreDefaults;
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public <V> V get(String key, DataObject holder, Function<DataValue, V> accessor) {
        return accessor.apply(getValue(key, holder));
    }

    public <V> V get(String key, DataObject holder, Class<V> type) {
        return get(key, holder, type, Context.NONE);
    }

    public <V> V get(String key, DataObject holder, Class<V> type, Context context) {
        DataObject value = holder.get(key).asObj();
        return Cereal.deserialize(value, type, context);
    }

    public DataValue serialize(Object value) {
        if (getType().isInstance(value)) {
            T t = getType().cast(value);
            DataObject root = new DataObject(name);
            for (Map.Entry<String, Function<T, ?>> e : accessors.entrySet()) {
                Object o = e.getValue().apply(t);
                DataValue val = DataValue.of(o);
                DataValue def = getDefault(e.getKey());
                if (val.equals(def) && ignoreDefaults) {
                    continue;
                }
                root.add(e.getKey(), val);
            }
            return root;
        }
        return DataValue.NULL;
    }

    public T deserialize(DataObject data) {
        return deserialize(data, Context.NONE);
    }

    public <V> V deserialize(DataObject data, Class<V> type) {
        return deserialize(data, type, Context.NONE);
    }

    public T deserialize(DataObject data, Context context) {
        return constructor.create(data, this, context);
    }

    public <V> V deserialize(DataObject data, Class<V> type, Context context) {
        if (type.isAssignableFrom(getType())) {
            T t = deserialize(data, context);
            if (type.isInstance(t)) {
                return type.cast(t);
            }
            throw new RuntimeException("Invalid type: " + type + " for instance: " + t.getClass());
        }
        throw new RuntimeException("Invalid type: " + type);
    }

    private DataValue getValue(String key, DataObject holder) {
        DataValue value = holder.get(key);
        if (value.isNonNull()) {
            return value;
        }
        return getDefault(key);
    }

    private DataValue getDefault(String name) {
        Supplier<DataValue> value = defaults.get(name);
        if (value == null) {
            return DataValue.NULL;
        }
        return value.get();
    }

    public static <T> Builder<T> builder(Class<T> type, DataFactory<T> constructor) {
        return builder(type.getSimpleName(), type, constructor);
    }

    public static <T> Builder<T> builder(String name, Class<T> type, DataFactory<T> constructor) {
        return new Builder<>(name, type, constructor);
    }

    public static class Builder<T> {

        private final String name;
        private final Class<T> type;
        private final DataFactory<T> constructor;
        private final Map<String, Function<T, ?>> accessors = new LinkedHashMap<>();
        private final Map<String, Supplier<DataValue>> defaults = new LinkedHashMap<>();

        private boolean ignoreDefaults = true;

        public Builder(String name, Class<T> type, DataFactory<T> constructor) {
            this.name = name;
            this.type = type;
            this.constructor = constructor;
        }

        public <V> Builder<T> add(String key, Object value, Function<T, V> accessor) {
            accessors.put(key, accessor);
            return add(key, DataValue.of(value), accessor);
        }

        public <V> Builder<T> add(String key, DataValue value, Function<T, V> accessor) {
            accessors.put(key, accessor);
            defaults.put(key, () -> value);
            return this;
        }

        public <V> Builder<T> addObj(String key, Function<T, V> accessor) {
            accessors.put(key, accessor);
            defaults.put(key, () -> DataObject.NULL_OBJ);
            return this;
        }

        public <V> Builder<T> addList(String key, Function<T, List<V>> accessor) {
            accessors.put(key, accessor);
            defaults.put(key, () -> DataList.NULL_LIST);
            return this;
        }

        public Builder<T> defaults() {
            ignoreDefaults = false;
            return this;
        }

        public DataSpec<T> build() {
            Objects.requireNonNull(constructor, "constructor");
            return new DataSpec<>(this);
        }
    }
}
