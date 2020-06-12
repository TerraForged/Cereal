package com.terraforged.cereal.spec;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    public static <T> List<DataSpec<?>> getSpecs(Class<T> type) {
        List<DataSpec<?>> all = new ArrayList<>(specs.values());
        all.sort(Comparator.comparing(DataSpec::getName));

        List<DataSpec<?>> list = new ArrayList<>(all.size());
        for (DataSpec<?> spec : all) {
            if (type.isAssignableFrom(spec.getType())) {
                list.add(spec);
            }
        }

        return list;
    }
}
