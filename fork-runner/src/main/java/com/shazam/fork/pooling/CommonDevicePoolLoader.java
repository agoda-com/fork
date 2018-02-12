package com.shazam.fork.pooling;

import com.google.common.collect.ImmutableSet;

import com.shazam.fork.device.DeviceLoader;
import com.shazam.fork.model.Device;
import com.shazam.fork.model.Devices;
import com.shazam.fork.model.Pool;

import java.util.Collection;
import java.util.Collections;

import static com.shazam.fork.model.Pool.Builder.aDevicePool;

public class CommonDevicePoolLoader implements DevicePoolLoader {
    private final DeviceLoader deviceLoader;

    public CommonDevicePoolLoader(DeviceLoader deviceLoader) {
        this.deviceLoader = deviceLoader;
    }

    @Override public Collection<String> getPools() {
        return new ImmutableSet.Builder<String>().add("common").build();
    }

    @Override public Devices getDevicesForPool(String name) {
        return deviceLoader.loadDevices();
    }

}