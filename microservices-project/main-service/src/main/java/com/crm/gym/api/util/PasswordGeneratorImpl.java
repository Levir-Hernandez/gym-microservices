package com.crm.gym.api.util;

import java.util.Random;

public class PasswordGeneratorImpl implements PasswordGenerator
{
    private Random random;

    public PasswordGeneratorImpl(Random random)
    {
        this.random = random;
    }

    @Override
    public String generatePassword()
    {
        return random.ints(10, 32, 127)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
