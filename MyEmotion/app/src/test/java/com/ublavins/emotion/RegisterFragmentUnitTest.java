package com.ublavins.emotion;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RegisterFragmentUnitTest {


    private RegisterFragment registerFragment;

    @Before
    public void setupMethod() {
        registerFragment = new RegisterFragment();
    }

    @After
    public void finishMethod() {
        registerFragment = null;
    }

    @Test
    public void test_isWorking() {
        assertEquals(4, 2+2);
    }
}
