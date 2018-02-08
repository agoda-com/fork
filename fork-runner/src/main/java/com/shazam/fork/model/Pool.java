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
package com.shazam.fork.model;

import com.shazam.fork.pooling.DevicePoolLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

/**
 * A grouping of {@link com.shazam.fork.model.Device}s.
 */
public class Pool {
    private final String name;
    private DevicePoolLoader devicePoolLoader;
    private AppendCallback appendCallback;
    private final HashMap<String, Device> historyDevices = new HashMap<>();

    public String getName() {
        return name;
    }

    public void update() {
        Devices devices = devicePoolLoader.getDevicesForPool(name);
        for (Device device : devices.getDevices()) {
            String serial = device.getSafeSerial();
            if (!historyDevices.containsKey(serial)) {
                historyDevices.put(serial, device);
                appendCallback.onAppend(device);
            }
        }
    }

    public void setAppendCallback(AppendCallback appendCallback) {
        this.appendCallback = appendCallback;
    }

    public Collection<Device> getRetrospectiveDevices() {
        return historyDevices.values();
    }

    @Override
    public String toString() {
        return reflectionToString(this, MULTI_LINE_STYLE);
    }

    public static class Builder {
        private String name = "";
        private DevicePoolLoader devicePoolLoader;


        public static Builder aDevicePool() {
            return new Builder();
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDeviceLoader(DevicePoolLoader devicePoolLoader) {
            this.devicePoolLoader = devicePoolLoader;
            return this;
        }

        public Pool build() {
            checkNotNull(name, "Pool name cannot be null");
            checkNotNull(devicePoolLoader, "DevicePoolLoader name cannot be null");
            return new Pool(this);
        }
    }

    public interface AppendCallback {
        void onAppend(Device device);
    }

    private Pool(Builder builder) {
        name = builder.name;
        devicePoolLoader = builder.devicePoolLoader;
    }
}
