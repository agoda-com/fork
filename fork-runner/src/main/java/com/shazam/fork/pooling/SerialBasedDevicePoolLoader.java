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

import com.shazam.fork.ManualPooling;
import com.shazam.fork.device.DeviceLoader;
import com.shazam.fork.model.Device;
import com.shazam.fork.model.Devices;

import java.util.Collection;

/**
 * Load pools specified by -Dfork.pool.NAME=Serial_1,Serial_2
 */
public class SerialBasedDevicePoolLoader implements DevicePoolLoader {
    private final ManualPooling manualPooling;
    private final DeviceLoader deviceLoader;

    public SerialBasedDevicePoolLoader(ManualPooling manualPooling, DeviceLoader deviceLoader) {
        this.manualPooling = manualPooling;
        this.deviceLoader = deviceLoader;
    }

    @Override public Collection<String> getPools() {
        return manualPooling.groupings.keySet();
    }

    @Override public Devices getDevicesForPool(String name) {
        Devices.Builder pool = new Devices.Builder();
        if (manualPooling.groupings.containsKey(name)) {
            Devices devices = deviceLoader.loadDevices();
            for (String serial : manualPooling.groupings.get(name)) {
                Device device = devices.getDevice(serial);
                if (device != null) {
                    pool.putDevice(serial, device);
                }
            }
        }
        return pool.build();
    }
}
