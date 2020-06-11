package com.terraforged.cereal.spec;

import com.terraforged.cereal.value.DataObject;

import java.util.concurrent.atomic.AtomicInteger;

public class Context {

    public static final Context NONE = new Context(-1, DataObject.NULL_OBJ);

    private final DataObject data;
    private final AtomicInteger id = new AtomicInteger();

    public Context() {
        this(0, new DataObject());
    }

    public Context(int id) {
        this(id, new DataObject());
    }

    public Context(int id, DataObject data) {
        this.id.set(id);
        this.data = data;
    }

    public int getId() {
        return id.get();
    }

    public int nextId() {
        return id.incrementAndGet();
    }

    public DataObject getData() {
        return data;
    }
}
