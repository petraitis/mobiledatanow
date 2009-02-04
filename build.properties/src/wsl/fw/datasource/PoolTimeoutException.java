//==============================================================================
// PoolTimeoutException.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

/**
 * An exception thrown by Connection pool if there are no free connections and
 * the timeout has expired.
 */
public class PoolTimeoutException extends Exception
{
    public PoolTimeoutException()
    {
    }
}

//==============================================================================
// end of file PoolTimeoutException.java
//==============================================================================
