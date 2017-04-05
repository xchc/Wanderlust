package com.wanderlust.util;

public class DoubleKeyPair
{
    private final Double key;
    private final Double value;

    public DoubleKeyPair(Double aKey, Double aValue)
    {
        key   = aKey;
        value = aValue;
    }

    public Double key()   { return key; }
    public Double value() { return value; }
}