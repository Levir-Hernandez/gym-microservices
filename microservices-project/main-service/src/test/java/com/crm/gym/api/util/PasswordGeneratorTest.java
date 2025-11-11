package com.crm.gym.api.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("unit")
@ActiveProfiles("test")
class PasswordGeneratorTest
{
    private PasswordGenerator passwordGenerator;

    public PasswordGeneratorTest()
    {
        this.passwordGenerator = new PasswordGeneratorImpl(new Random());
    }

    @Test
    @DisplayName("Should generate a password with a length of 10 characters")
    void generatePassword()
    {
        String password = passwordGenerator.generatePassword();
        assertNotNull(password);

        int expectedPasswordLength= 10;
        int actualPasswordLength = password.length();

        assertEquals(expectedPasswordLength, actualPasswordLength);
    }
}