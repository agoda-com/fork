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

import com.shazam.fork.device.DeviceLoader;
import com.shazam.fork.model.*;

import java.util.ArrayList;
import java.util.Collection;

import static com.shazam.fork.model.Pool.Builder.aDevicePool;

/**
 * Create tablets/other pool based on self-reported ro.build.characteristics = tablet
 */
public class DefaultAndTabletDevicePoolLoader implements DevicePoolLoader {

	private static final String DEFAULT_POOL_NAME = "default_pool";
	private static final String TABLETS = "tablets";
    private final DeviceLoader deviceLoader;

    public DefaultAndTabletDevicePoolLoader(DeviceLoader deviceLoader) {
        this.deviceLoader = deviceLoader;
    }

    @Override public Collection<String> getPools() {
        return new ImmutableSet.Builder<String>()
                .add(DEFAULT_POOL_NAME)
                .add(TABLETS)
                .build();
    }

    @Override public Devices getDevicesForPool(String name) {
        Devices.Builder pool = new Devices.Builder();
        for (Device device : deviceLoader.loadDevices().getDevices()) {
            if (device.isTablet() == TABLETS.equals(name)) {
                pool.putDevice(device.getSerial(), device);
            }
        }
        return pool.build();
    }
}
