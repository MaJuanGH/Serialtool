package com.mdeveloper.serialtool;

public class Serial {
    public native int Close();

    public native int Open(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);

    public int OpenPort(String[] paramArrayOfString) {
        return Open(1, Integer.parseInt(paramArrayOfString[1]), Integer.parseInt(paramArrayOfString[2]), Integer.parseInt(paramArrayOfString[3]), 2);
//    StringList localStringList = new StringList();
//    int j = 1;
//    int k = 0;
//    int i = 0;
//    if (i >= localStringList.ttyList.length) {
//      label26:
//      i = 0;
//    }
//    for (;;)
//    {
//      if (i >= localStringList.CheckBit.length) {
//        i = k;
//      }
//      while (paramArrayOfString[4].equalsIgnoreCase((String)localStringList.CheckBit[i]))
//      {
//        return Open(j, Integer.parseInt(paramArrayOfString[1]), Integer.parseInt(paramArrayOfString[2]), Integer.parseInt(paramArrayOfString[3]), i);
//        if (paramArrayOfString[0].equalsIgnoreCase((String)localStringList.ttyList[i]))
//        {
//          j = i;
//          break label26;
//        }
//        i += 1;
//        break;
//      }
//      i += 1;
//    }
    }

    public native byte[] Read();

    public native int Write(byte[] paramArrayOfByte, int paramInt);
}


/* Location:              D:\Jianruilin\3.Developtools\fanbianyi\dex2jar-2.0\dex2jar-2.0\classes-dex2jar.jar!\com\mdeveloper\serialtool\Serial.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */