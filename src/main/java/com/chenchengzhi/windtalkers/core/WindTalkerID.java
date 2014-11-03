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

/**
 * Created by jinchengchen on 11/2/14.
 */
public enum WindTalkerID {
    TALKER_LEADER(0, "TalkerLeader"),
    ;

    private int id;
    private String name;

    WindTalkerID(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public static WindTalkerID getByName(String name) {
        for (WindTalkerID talker : WindTalkerID.values()) {
            if (talker.name.equalsIgnoreCase(name)) {
                return talker;
            }
        }
        return null;
    }
}
