package com.ublavins.emotion;

import android.util.Patterns;


public class ValidateRegistration {

    public class ValidationMessage {

        private boolean check;
        private String message;

        public ValidationMessage(boolean bool, String msg) {
            check = bool;
            message = msg;
        }

        public void setCheck(boolean bool) {
            check = bool;
        }

        public void setMessage(String msg) {
            message = msg;
        }

        public boolean getCheck() {
            return check;
        }

        public String getMessage() {
            return message;
        }

    }

    private String firstNameVal;
    private String lastNameVal;
    private String emailVal;
    private String passwordVal;
    private String dobVal;
    private String genderVal;

    public ValidateRegistration(
            String fName,
            String lName,
            String email,
            String pass,
            String dob,
            String gender
    ) {
        firstNameVal = fName;
        lastNameVal = lName;
        emailVal = email;
        passwordVal = pass;
        dobVal = dob;
        genderVal = gender;
    }

    public void setFirstNameVal(String fName) {
        firstNameVal = fName;
    }

    public void setLastNameVal(String lName) {
        lastNameVal = lName;
    }

    public void setEmailVal(String email) {
        emailVal = email;
    }

    public void setPasswordVal(String pass) {
        passwordVal = pass;
    }

    public void setDobVal(String dob) {
        dobVal = dob;
    }

    public void setGenderVal(String gender) {
        genderVal = gender;
    }

    public String getFirstNameVal() {
        return firstNameVal;
    }

    public String getLastNameVal() {
        return lastNameVal;
    }

    public String getEmailVal() {
        return emailVal;
    }

    public String getPasswordVal() {
        return passwordVal;
    }

    public String getDobVal() {
        return dobVal;
    }

    public String getGenderVal() {
        return genderVal;
    }

    public ValidationMessage validateFirstName() {
        ValidationMessage validationMessage = new ValidationMessage(true, "");
        if (firstNameVal.isEmpty()) {
            validationMessage.setCheck(false);
            validationMessage.setMessage("Field must not be empty");
        } else if (!firstNameVal.matches("^[a-zA-Z]+$")) {
            validationMessage.setCheck(false);
            validationMessage.setMessage("First name must contain letters");
        }
        return validationMessage;
    }

    public ValidationMessage validateLastName() {
        ValidationMessage validationMessage = new ValidationMessage(true, "");
        if (lastNameVal.isEmpty()) {
            validationMessage.setCheck(false);
            validationMessage.setMessage("Field must not be empty");
        } else if (!lastNameVal.matches("^[a-zA-Z]+$")) {
            validationMessage.setCheck(false);
            validationMessage.setMessage("Last name must contain letters");
        }
        return validationMessage;
    }

    public ValidationMessage validateEmail() {
        ValidationMessage validationMessage = new ValidationMessage(true, "");
        if (emailVal.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
            validationMessage.setCheck(false);
            validationMessage.setMessage("Enter a valid email");
        }
        return validationMessage;
    }

    public ValidationMessage validatePassword() {
        ValidationMessage validationMessage = new ValidationMessage(true, "");
        if (passwordVal.isEmpty()) {
            validationMessage.setCheck(false);
            validationMessage.setMessage("Field must not be empty");
        } else if (passwordVal.length() < 8) {
            validationMessage.setCheck(false);
            validationMessage.setMessage("Password must have a minimum length of 8 characters");
        }
        return validationMessage;
    }

    public ValidationMessage validateDob() {
        ValidationMessage validationMessage = new ValidationMessage(true, "");
        if (dobVal.isEmpty()) {
            validationMessage.setCheck(false);
            validationMessage.setMessage("Enter a date");
        }
        return validationMessage;
    }

    public ValidationMessage validateGender() {
        ValidationMessage validationMessage = new ValidationMessage(true, "");
        if (genderVal.isEmpty()) {
            validationMessage.setCheck(false);
            validationMessage.setMessage("Select a gender");
        }
        return validationMessage;
    }

}
