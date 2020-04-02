package com.ublavins.emotion;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValidateRegistrationTest {

    private ValidateRegistration validateRegistration;
    private String mockFirstName;
    private String mockLastName;
    private String mockEmail;
    private String mockPassword;
    private String mockDob;
    private String mockGender;

    @Before
    public void setUp() {
        mockFirstName = "";
        mockLastName = "";
        mockEmail = "";
        mockPassword = "";
        mockDob = "";
        mockGender = "";
        validateRegistration = new ValidateRegistration(
                mockFirstName, mockLastName, mockEmail,
                mockPassword, mockDob, mockGender);
    }

    @After
    public void tearDown() {
        validateRegistration = null;
    }

    @Test
    public void setGetFirstName() {
        String expected = "test";
        validateRegistration.setFirstNameVal(expected);
        String sut = validateRegistration.getFirstNameVal();
        assertEquals(expected, sut);
    }

    @Test
    public void setGetLastName() {
        String expected = "test";
        validateRegistration.setLastNameVal(expected);
        String sut = validateRegistration.getLastNameVal();
        assertEquals(expected, sut);
    }

    @Test
    public void setGetEmail() {
        String expected = "test";
        validateRegistration.setEmailVal(expected);
        String sut = validateRegistration.getEmailVal();
        assertEquals(expected, sut);
    }

    @Test
    public void setGetPassword() {
        String expected = "test";
        validateRegistration.setPasswordVal(expected);
        String sut = validateRegistration.getPasswordVal();
        assertEquals(expected, sut);
    }

    @Test
    public void setGetDob() {
        String expected = "test";
        validateRegistration.setDobVal(expected);
        String sut = validateRegistration.getDobVal();
        assertEquals(expected, sut);
    }

    @Test
    public void setGetGender() {
        String expected = "test";
        validateRegistration.setGenderVal(expected);
        String sut = validateRegistration.getGenderVal();
        assertEquals(expected, sut);
    }

    @Test
    public void validateEmptyFirstName() {
        String fakeFirstName = "";
        validateRegistration.setFirstNameVal(fakeFirstName);
        ValidateRegistration.ValidationMessage sut = validateRegistration.validateFirstName();
        assertFalse(sut.getCheck());
        assertEquals("Field must not be empty", sut.getMessage());
    }

    @Test
    public void validateInvalidFirstName() {
        String fakeFirstName = "12345";
        validateRegistration.setFirstNameVal(fakeFirstName);
        ValidateRegistration.ValidationMessage sut = validateRegistration.validateFirstName();
        assertFalse(sut.getCheck());
        assertEquals("First name must contain letters", sut.getMessage());
    }

    @Test
    public void validateValidFirstName() {
        String fakeFirstName = "Test";
        validateRegistration.setFirstNameVal(fakeFirstName);
        ValidateRegistration.ValidationMessage sut = validateRegistration.validateFirstName();
        assertTrue(sut.getCheck());
        assertEquals("", sut.getMessage());
    }

    @Test
    public void validateEmptyLastName() {
        String fakeLastName = "";
        validateRegistration.setLastNameVal(fakeLastName);
        ValidateRegistration.ValidationMessage sut = validateRegistration.validateLastName();
        assertFalse(sut.getCheck());
        assertEquals("Field must not be empty", sut.getMessage());
    }

    @Test
    public void validateInvalidLastName() {
        String fakeLastName = "12345";
        validateRegistration.setLastNameVal(fakeLastName);
        ValidateRegistration.ValidationMessage sut = validateRegistration.validateLastName();
        assertFalse(sut.getCheck());
        assertEquals("Last name must contain letters", sut.getMessage());
    }

    @Test
    public void validateValidLastName() {
        String fakeLastName = "Test";
        validateRegistration.setLastNameVal(fakeLastName);
        ValidateRegistration.ValidationMessage sut = validateRegistration.validateLastName();
        assertTrue(sut.getCheck());
        assertEquals("", sut.getMessage());
    }

    @Test
    public void validateInvalidEmail() {
        String fakeEmail = "";
        validateRegistration.setEmailVal(fakeEmail);
        ValidateRegistration.ValidationMessage sut = validateRegistration.validateEmail();
        assertFalse(sut.getCheck());
        assertEquals("Enter a valid email", sut.getMessage());
    }

    @Test
    public void validateEmptyPassword() {
        String fakePassword = "";
        validateRegistration.setPasswordVal(fakePassword);
        ValidateRegistration.ValidationMessage sut = validateRegistration.validatePassword();
        assertFalse(sut.getCheck());
        assertEquals("Field must not be empty", sut.getMessage());
    }

    @Test
    public void validateInvalidPassword() {
        String fakePassword = "test";
        validateRegistration.setPasswordVal(fakePassword);
        ValidateRegistration.ValidationMessage sut = validateRegistration.validatePassword();
        assertFalse(sut.getCheck());
        assertEquals("Password must have a minimum length of 8 characters",
                sut.getMessage());
    }

    @Test
    public void validateValidPassword() {
        String fakePassword = "Password";
        validateRegistration.setPasswordVal(fakePassword);
        ValidateRegistration.ValidationMessage sut = validateRegistration.validatePassword();
        assertTrue(sut.getCheck());
        assertEquals("", sut.getMessage());
    }

    @Test
    public void validateEmptyDob() {
        String fakeDob = "";
        validateRegistration.setDobVal(fakeDob);
        ValidateRegistration.ValidationMessage sut = validateRegistration.validateDob();
        assertFalse(sut.getCheck());
        assertEquals("Enter a date", sut.getMessage());
    }

    @Test
    public void validateValidDob() {
        String fakeDob = "01/01/01";
        validateRegistration.setDobVal(fakeDob);
        ValidateRegistration.ValidationMessage sut = validateRegistration.validateDob();
        assertTrue(sut.getCheck());
        assertEquals("", sut.getMessage());
    }

    @Test
    public void validateEmptyGender() {
        String fakeGender = "";
        validateRegistration.setGenderVal(fakeGender);
        ValidateRegistration.ValidationMessage sut = validateRegistration.validateGender();
        assertFalse(sut.getCheck());
        assertEquals("Select a gender", sut.getMessage());
    }

    @Test
    public void validateValidGender() {
        String fakeGender = "Male";
        validateRegistration.setGenderVal(fakeGender);
        ValidateRegistration.ValidationMessage sut = validateRegistration.validateGender();
        assertTrue(sut.getCheck());
        assertEquals("", sut.getMessage());
    }

}
