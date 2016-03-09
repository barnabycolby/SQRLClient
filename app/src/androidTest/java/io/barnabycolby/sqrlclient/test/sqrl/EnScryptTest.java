package io.barnabycolby.sqrlclient.test.sqrl;

import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.sqrl.EnScrypt;
import io.barnabycolby.sqrlclient.test.Helper;
import io.barnabycolby.sqrlclient.helpers.Lambda;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EnScryptTest {

    private EnScrypt mEnScrypt;

    @Before
    public void setUp() {
        this.mEnScrypt = new EnScrypt();
    }

    @Test
    public void shouldCorrectlyReproduceVerificationVectors() {
        // Verification vectors taken from
        // https://www.grc.com/sqrl/scrypt.htm
        String result = this.mEnScrypt.deriveKey(null, null, 1);
        assertEquals("a8ea62a6e1bfd20e4275011595307aa302645c1801600ef5cd79bf9d884d911c", result);

        result = this.mEnScrypt.deriveKey(null, null, 100);
        assertEquals("45a42a01709a0012a37b7b6874cf16623543409d19e7740ed96741d2e99aab67", result);

        result = this.mEnScrypt.deriveKey(null, null, 1000);
        assertEquals("3f671adf47d2b1744b1bf9b50248cc71f2a58e8d2b43c76edb1d2a2c200907f5", result);

        result = this.mEnScrypt.deriveKey("password", null, 123);
        assertEquals("129d96d1e735618517259416a605be7094c2856a53c14ef7d4e4ba8e4ea36aeb", result);

        result = this.mEnScrypt.deriveKey("password", "0000000000000000000000000000000000000000000000000000000000000000", 123);
        assertEquals("2f30b9d4e5c48056177ff90a6cc9da04b648a7e8451dfa60da56c148187f6a7d", result);
    }

    @Test
    public void shouldDenyPasswordsWithNulValue() throws Exception {
        Helper.assertExceptionThrown(IllegalArgumentException.class, new Lambda() {
            public void run() {
                mEnScrypt.deriveKey("\0", null, 1);
            }
        });

        Helper.assertExceptionThrown(IllegalArgumentException.class, new Lambda() {
            public void run() {
                mEnScrypt.deriveKey("Barney\0", null, 1);
            }
        });

        Helper.assertExceptionThrown(IllegalArgumentException.class, new Lambda() {
            public void run() {
                mEnScrypt.deriveKey("Bar\0ney", null, 1);
            }
        });
    }

    @Test
    public void deriveKeyFor5SecondsShouldTake5Seconds() {
        long startTime = System.currentTimeMillis();
        this.mEnScrypt.deriveKeyFor5Seconds("password", "0000000000000000000000000000000000000000000000000000000000000000");
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        long fourAndAHalfSeconds = 4500;
        long fiveAndAHalfSeconds = 5500;
        assertTrue(duration > fourAndAHalfSeconds && duration < fiveAndAHalfSeconds);
    }
}
