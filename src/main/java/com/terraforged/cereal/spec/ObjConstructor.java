package com.terraforged.cereal.spec;

import com.terraforged.cereal.value.DataObject;

public interface ObjConstructor<T> {

    <C extends Context> T create(DataObject data, DataSpec<T> spec, C context);
}
