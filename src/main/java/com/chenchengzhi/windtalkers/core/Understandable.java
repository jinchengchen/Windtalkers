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

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by jinchengchen on 10/29/14.
 */
public interface Understandable {

    public ObjectNode translate();
}
