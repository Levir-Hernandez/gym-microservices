package com.crm.gym.api.util;

import com.crm.gym.api.util.PasswordGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PasswordGeneratorTest
{
    private PasswordGenerator passwordGenerator;

    @Autowired
    public PasswordGeneratorTest(PasswordGenerator passwordGenerator)
    {
        this.passwordGenerator = passwordGenerator;
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