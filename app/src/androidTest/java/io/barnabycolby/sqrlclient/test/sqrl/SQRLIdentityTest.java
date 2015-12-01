package io.barnabycolby.sqrlclient.test.sqrl;

import android.support.test.runner.AndroidJUnit4;
import org.junit.*;
import org.junit.runner.RunWith;

import io.barnabycolby.sqrlclient.sqrl.*;

@RunWith(AndroidJUnit4.class)
public class SQRLIdentityTest {
    @Test
    public void correctlySignMessageUsingIdentityPrivateKey() throws Exception {
        SQRLIdentity sqrlIdentity = new SQRLIdentity();
        String actual = sqrlIdentity.signUsingIdentityPrivateKey("bakedbeans");
        String expected = "dqyBNmVYClimJA3tzRyzzYZlwcphlcMnQ9OkVzeKjozpIAeT3QL60JcJ7w3UdEheyWEOOMsIXgYKh4DqVLYLDA";
        Assert.assertEquals(expected, actual);
    }
}
