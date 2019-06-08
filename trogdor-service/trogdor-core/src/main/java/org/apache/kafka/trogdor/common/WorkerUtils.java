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

package org.apache.kafka.trogdor.common;

import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

/**
 * Utilities for Trogdor TaskWorkers.
 */
public final class WorkerUtils {
    /**
     * Handle an exception in a TaskWorker.
     *
     * @param log               The logger to use.
     * @param what              The component that had the exception.
     * @param exception         The exception.
     * @param doneFuture        The TaskWorker's doneFuture
     * @throws TrogdorException   A wrapped version of the exception.
     */
    public static void abort(Logger log, String what, Throwable exception,
                             CompletableFuture<String> doneFuture) throws TrogdorException {
        log.warn("{} caught an exception", what, exception);
        if (exception.getMessage() == null || exception.getMessage().isEmpty()) {
            doneFuture.complete(exception.getClass().getCanonicalName());
        } else {
            doneFuture.complete(exception.getMessage());
        }
        throw new TrogdorException(exception);
    }

    /**
     * Convert a rate expressed per second to a rate expressed per the given period.
     *
     * @param perSec            The per-second rate.
     * @param periodMs          The new period to use.
     * @return                  The rate per period.  This will never be less than 1.
     */
    public static int perSecToPerPeriod(float perSec, long periodMs) {
        float period = ((float) periodMs) / 1000.0f;
        float perPeriod = perSec * period;
        perPeriod = Math.max(1.0f, perPeriod);
        return (int) perPeriod;
    }

}
