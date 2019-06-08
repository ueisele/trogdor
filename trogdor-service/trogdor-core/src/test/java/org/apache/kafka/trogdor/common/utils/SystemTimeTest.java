/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.trogdor.common.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SystemTimeTest extends TimeTest {
    
    @Override
    protected Time createTime() {
        return Time.SYSTEM;
    }

    @Test
    public void testDummy1() {
        // Visual Studio Code requires at least two test methods in order to run tests from super class ?!
        assertEquals(true, true);
    }

    @Test
    public void testDummy2() {
        assertEquals(true, true);
    }
}
