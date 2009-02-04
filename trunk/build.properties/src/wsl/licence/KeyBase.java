package wsl.licence;

/**
 * Title:        Mobile Data Now
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      WAP Solutions Ltd
 * @author Paul Lupton
 * @version 1.0
 */

public abstract class KeyBase
{

    public KeyBase()
    {
    }

    protected void intToBytes(byte[] dest, int offset, int srcVal) throws Exception
    {
        if (dest == null || dest.length < offset + 4)
           throw new RuntimeException("Invalid destination array");

        for (int i = 0; i < 4; i++)
            dest[offset+i] = (byte)((srcVal>>(8*(3-i)))&0xff);
    }

    protected void shortToBytes(byte[] dest, int offset, short srcVal) throws Exception
    {
        if (dest == null || dest.length < offset + 2)
           throw new RuntimeException("Invalid destination array");

        dest[offset] = (byte)((srcVal>>8)&0xff);
        dest[offset+1] = (byte)(srcVal&0xff);
    }

    protected int bytesToInt(byte[] src, int offset) throws Exception
    {
        if (src == null || src.length < offset + 4)
           throw new RuntimeException("Invalid source array");

        int ret = 0;
        for (int i = 0; i < 4; i++)
            ret |= (((int)src[offset+i])&0xff)<< (8*(3-i));

        return ret;
    }

    protected short bytesToShort(byte[] src, int offset) throws Exception
    {
        if (src == null || src.length < offset + 2)
           throw new RuntimeException("Invalid source array");

        short ret = 0;
        ret |= ((short)src[offset])<<8;
        ret |= (((short)src[offset+1])&0xff);

        return ret;
    }

    /**
     * Overload to force byte array structuring
     */
    protected abstract byte[] getBytes() throws Exception;


}