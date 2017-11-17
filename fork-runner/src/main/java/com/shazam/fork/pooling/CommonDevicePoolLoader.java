package com.shazam.fork.pooling;

import com.shazam.fork.model.Device;
import com.shazam.fork.model.Devices;
import com.shazam.fork.model.Pool;

import java.util.Collection;
import java.util.Collections;

import static com.shazam.fork.model.Pool.Builder.aDevicePool;

public class CommonDevicePoolLoader implements DevicePoolLoader {
    @Override
    public Collection<Pool> loadPools(Devices devices) {
        Pool.Builder pool = aDevicePool().withName("common");
        for (Device device : devices.getDevices()) {
            pool.addDevice(device);
        }
        return Collections.singleton(pool.build());
    }
}