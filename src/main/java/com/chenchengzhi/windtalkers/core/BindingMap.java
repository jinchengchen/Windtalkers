/*
 * Copyright (C) 2014
 * Author: chen jincheng <loocalvinci@gmail.com>
 * Web: http://chenchengzhi.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */

package com.chenchengzhi.windtalkers.core;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Key;

/**
 * Created by jinchengchen on 10/28/14.
 */
public class BindingMap {

    private final Map<Key, Object> values;

    public BindingMap() {
        values = Maps.newHashMap();
    }

    public <T> void put(Key<T> key, T value) {
        values.put(key, value);
    }

    public <T> void put(Class<T> klass, T value) {
        Preconditions.checkNotNull(klass);
        Preconditions.checkNotNull(value);
        Key<T> key = Key.get(klass);
        put(key, value);
    }

    public <T> T get(Key<T> key) {
        Preconditions.checkNotNull(key);
        return (T) values.get(key);
    }

    public <T> T get(Class<T> klass) {
        Preconditions.checkNotNull(klass);
        Key<T> key = Key.get(klass);
        return get(key);
    }

    public <T> void remove(Key<T> key) {
        Preconditions.checkNotNull(key);
        values.remove(key);
    }

    public <T> void remove(Class<T> klass) {
        Preconditions.checkNotNull(klass);
        values.remove(Key.get(klass));
    }
}
