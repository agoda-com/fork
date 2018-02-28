/*
 * Copyright 2014 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.shazam.fork.pooling;

import com.google.common.collect.ImmutableSet;

import com.shazam.fork.ComputedPooling;
import com.shazam.fork.device.DeviceLoader;
import com.shazam.fork.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.shazam.fork.model.Pool.Builder.aDevicePool;
import static java.util.stream.Collectors.toCollection;

/**
 * Allocate devices into pools specified by nominated strategy.
 */
public class ComputedDevicePoolLoader implements DevicePoolLoader {
    private static final Logger logger = LoggerFactory.getLogger(ComputedDevicePoolLoader.class);
    private final ComputedPoolsCategorizer computedPoolsCategorizer;
    private final DeviceLoader deviceLoader;

    public ComputedDevicePoolLoader(ComputedPooling computedPooling, DeviceLoader deviceLoader) {
        computedPoolsCategorizer = new ComputedPoolsCategorizer(computedPooling);
        this.deviceLoader = deviceLoader;
    }

    @Override public Collection<String> getPools() {
        ImmutableSet.Builder<String> pools = new ImmutableSet.Builder<>();

        for (Device device : deviceLoader.loadDevices().getDevices()) {
            String poolName = computedPoolsCategorizer.poolForDevice(device);
            if (poolName != null) {
                pools.add(poolName);
            } else {
                logger.warn("Could not infer pool for " + device.getLongName() + ". Not adding to any pools");
            }
        }

        return pools.build();
    }

    @Override public Devices getDevicesForPool(String name) {
        Devices.Builder pool = new Devices.Builder();
        for (Device device : deviceLoader.loadDevices().getDevices()) {
            String poolName = computedPoolsCategorizer.poolForDevice(device);
            if (name.equals(poolName)) {
                logger.debug("Adding {} to pool {}", device.getLongName(), poolName);
                pool.putDevice(device.getSerial(), device);
            }
        }
        return pool.build();
    }
}
