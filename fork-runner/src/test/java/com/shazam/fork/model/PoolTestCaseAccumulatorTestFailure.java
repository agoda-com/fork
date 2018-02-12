package com.shazam.fork.model;

import com.shazam.fork.pooling.DevicePoolLoader;
import com.shazam.fork.stat.StatServiceLoader;
import com.shazam.fork.stat.TestStatsLoader;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static com.shazam.fork.model.Device.Builder.aDevice;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class PoolTestCaseAccumulatorTestFailure {

    private final Device A_DEVICE = aDevice()
            .withSerial("a_device")
            .build();
    private final Device ANOTHER_DEVICE = aDevice()
            .withSerial("another_device")
            .build();

    private final Pool A_POOL = Pool.Builder.aDevicePool()
            .withDeviceLoader(new DevicePoolLoader() {
                @Override
                public Collection<String> getPools() {
                    return Collections.singleton("a_pool");
                }

                @Override
                public Devices getDevicesForPool(String name) {
                    return new Devices.Builder()
                            .putDevice(A_DEVICE.getSafeSerial(), A_DEVICE)
                            .build();
                }
            })
            .withName("a_pool")
            .build();

    private final Pool ANOTHER_POOL = Pool.Builder.aDevicePool()
            .withDeviceLoader(new DevicePoolLoader() {
                @Override
                public Collection<String> getPools() {
                    return Collections.singleton("another_pool");
                }

                @Override
                public Devices getDevicesForPool(String name) {
                    return new Devices.Builder()
                            .putDevice(ANOTHER_DEVICE.getSafeSerial(), ANOTHER_DEVICE)
                            .build();
                }
            })
            .withName("another_pool")
            .build();

    private final TestCaseEventFactory factory = new TestCaseEventFactory(new TestStatsLoader(new StatServiceLoader("")));
    private final TestCaseEvent A_TEST_CASE = factory.newTestCase("a_method", "a_class", false, emptyList(), emptyMap());
    private final TestCaseEvent ANOTHER_TEST_CASE = factory.newTestCase("another_method", "a_class", false, emptyList(), emptyMap());

    PoolTestCaseFailureAccumulator subject;

    @Before
    public void setUp() throws Exception {
        subject = new PoolTestCaseFailureAccumulator();
    }

    @Test
    public void shouldAggregateCountForSameTestCaseAcrossMultipleDevices() throws Exception {

        subject.record(A_POOL, A_TEST_CASE);
        subject.record(A_POOL, A_TEST_CASE);

        int actualCount = subject.getCount(A_TEST_CASE);

        assertThat(actualCount, equalTo(2));
    }

    @Test
    public void shouldCountTestsPerPool() throws Exception {
        subject.record(A_POOL, A_TEST_CASE);
        subject.record(A_POOL, A_TEST_CASE);

        int actualCount = subject.getCount(A_POOL, A_TEST_CASE);

        assertThat(actualCount, equalTo(2));
    }

    @Test
    public void shouldAggregateCountForSameTestCaseAcrossMultiplePools() throws Exception {

        subject.record(A_POOL, A_TEST_CASE);
        subject.record(ANOTHER_POOL, A_TEST_CASE);

        int actualCount = subject.getCount(A_TEST_CASE);

        assertThat(actualCount, equalTo(2));
    }

    @Test
    public void shouldNotReturnTestCasesForDifferentPool() throws Exception {
        subject.record(A_POOL, A_TEST_CASE);

        int actualCountForAnotherDevice = subject.getCount(ANOTHER_POOL, A_TEST_CASE);

        assertThat(actualCountForAnotherDevice, equalTo(0));
    }

    @Test
    public void shouldAccumulateDifferentTestCasesForSamePool() throws Exception {
        subject.record(A_POOL, A_TEST_CASE);
        subject.record(A_POOL, ANOTHER_TEST_CASE);

        int actualCount = subject.getCount(A_POOL, A_TEST_CASE);
        int anotherActualCount = subject.getCount(A_POOL, ANOTHER_TEST_CASE);

        assertThat(actualCount, equalTo(1));
        assertThat(anotherActualCount, equalTo(1));
    }
}
