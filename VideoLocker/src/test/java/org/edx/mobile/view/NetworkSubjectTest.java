package org.openedu.www.view;

import tw.openedu.www.base.BaseFragmentActivity;
import tw.openedu.www.interfaces.NetworkObserver;
import tw.openedu.www.interfaces.NetworkSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Test NetworkSubject implementations for correctness
 */
@RunWith(Parameterized.class)
public class NetworkSubjectTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { new BaseFragmentActivity() {} }
        });
    }

    @Parameterized.Parameter
    public NetworkSubject networkSubject;

    @Test
    public void test() {
        NetworkObserverTest networkObserver = new NetworkObserverTest();
        networkSubject.registerNetworkObserver(networkObserver);
        assertTrue(networkObserver.isOnline);
        networkSubject.notifyNetworkDisconnect();
        assertFalse(networkObserver.isOnline);
        networkSubject.notifyNetworkConnect();
        assertTrue(networkObserver.isOnline);
        networkSubject.unregisterNetworkObserver(networkObserver);
        assertTrue(networkObserver.isOnline);
        networkSubject.notifyNetworkDisconnect();
        assertTrue(networkObserver.isOnline);
    }

    private static class NetworkObserverTest implements NetworkObserver {
        boolean isOnline = true;

        @Override
        public void onOnline() {
            isOnline = true;
        }

        @Override
        public void onOffline() {
            isOnline = false;
        }
    }
}
