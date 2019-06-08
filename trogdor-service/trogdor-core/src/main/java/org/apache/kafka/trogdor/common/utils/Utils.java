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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public final class Utils {

    private Utils() {}

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    /**
     * Construct a new object using a class name and parameters.
     *
     * @param className                 The full name of the class to construct.
     * @param params                    A sequence of (type, object) elements.
     * @param <T>                       The type of object to construct.
     * @return                          The new object.
     * @throws ClassNotFoundException   If there was a problem constructing the object.
     */
    public static <T> T newParameterizedInstance(String className, Object... params)
            throws ClassNotFoundException {
        Class<?>[] argTypes = new Class<?>[params.length / 2];
        Object[] args = new Object[params.length / 2];
        try {
            Class<?> c = Class.forName(className, true, org.apache.kafka.common.utils.Utils.getContextOrKafkaClassLoader());
            for (int i = 0; i < params.length / 2; i++) {
                argTypes[i] = (Class<?>) params[2 * i];
                args[i] = params[(2 * i) + 1];
            }
            @SuppressWarnings("unchecked")
            Constructor<T> constructor = (Constructor<T>) c.getConstructor(argTypes);
            return constructor.newInstance(args);
        } catch (NoSuchMethodException e) {
            throw new ClassNotFoundException(String.format("Failed to find " +
                    "constructor with %s for %s", org.apache.kafka.common.utils.Utils.join(argTypes, ", "), className), e);
        } catch (InstantiationException e) {
            throw new ClassNotFoundException(String.format("Failed to instantiate " +
                    "%s", className), e);
        } catch (IllegalAccessException e) {
            throw new ClassNotFoundException(String.format("Unable to access " +
                    "constructor of %s", className), e);
        } catch (InvocationTargetException e) {
            throw new ClassNotFoundException(String.format("Unable to invoke " +
                    "constructor of %s", className), e);
        }
    }

    /**
     * Closes {@code closeable} and if an exception is thrown, it is logged at the WARN level.
     */
    public static void closeQuietly(AutoCloseable closeable, String name) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable t) {
                log.warn("Failed to close {} with type {}", name, closeable.getClass().getName(), t);
            }
        }
    }

    /**
     * Get the stack trace from an exception as a string
     */
    public static String stackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     *  Converts a {@code Map} class into a string, concatenating keys and values
     *  Example:
     *      {@code mkString({ key: "hello", keyTwo: "hi" }, "|START|", "|END|", "=", ",")
     *          => "|START|key=hello,keyTwo=hi|END|"}
     */
    public static <K, V> String mkString(Map<K, V> map, String begin, String end,
                                         String keyValueSeparator, String elementSeparator) {
        StringBuilder bld = new StringBuilder();
        bld.append(begin);
        String prefix = "";
        for (Map.Entry<K, V> entry : map.entrySet()) {
            bld.append(prefix).append(entry.getKey()).
                    append(keyValueSeparator).append(entry.getValue());
            prefix = elementSeparator;
        }
        bld.append(end);
        return bld.toString();
    }

}
