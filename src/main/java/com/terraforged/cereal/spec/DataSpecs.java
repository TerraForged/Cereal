package com.terraforged.cereal.spec;

import com.terraforged.cereal.value.DataValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class DataSpecs {

    private static final Map<String, DataSpec<?>> specs = new ConcurrentHashMap<>();

    public static void register(DataSpec<?> spec) {
        specs.put(spec.getName(), spec);
    }

    public static boolean hasSpec(String name) {
        return specs.containsKey(name);
    }

    public static DataSpec<?> getSpec(String name) {
        DataSpec<?> spec = specs.get(name);
        if (spec == null) {
            throw new NullPointerException("Missing spec: " + name);
        }
        return spec;
    }

    public static Supplier<DataValue> getDefault(String name) {
        return new Supplier<DataValue>() {

            private final String typeName = name;

            @Override
            public DataValue get() {
                return specs.get(typeName).createDefault();
            }
        };
    }
}
