package com.terraforged.cereal.spec;

import com.terraforged.cereal.value.DataObject;

public interface ObjConstructor<T> {

    T create(DataObject data, DataSpec<T> spec, Context context);
}
