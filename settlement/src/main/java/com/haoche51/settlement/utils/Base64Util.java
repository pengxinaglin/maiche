package com.haoche51.settlement.utils;

public class Base64Util {
  static private byte[] encodingTable = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
    'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
    'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
    't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    '+', '/'};

  public static byte[] encode(byte[] data) {
    byte[] bytes;

    if ((data.length % 3) == 0) {
      bytes = new byte[4 * data.length / 3];
    } else {
      bytes = new byte[4 * ((data.length / 3) + 1)];
    }

    for (int i = 0, j = 0; i < ((data.length / 3) * 3); i += 3, j += 4) {
      int b1, b2, b3, b4;
      int d1, d2, d3;

      d1 = data[i] & 0xff;
      d2 = data[i + 1] & 0xff;
      d3 = data[i + 2] & 0xff;

      b1 = (d1 >>> 2) & 0x3f;
      b2 = ((d1 << 4) | (d2 >>> 4)) & 0x3f;
      b3 = ((d2 << 2) | (d3 >>> 6)) & 0x3f;
      b4 = d3 & 0x3f;

      bytes[j] = encodingTable[b1];
      bytes[j + 1] = encodingTable[b2];
      bytes[j + 2] = encodingTable[b3];
      bytes[j + 3] = encodingTable[b4];
    }

		/*
         * process the tail end.
		 */
    int b1, b2, b3;
    int d1, d2;

    switch (data.length % 3) {
      case 0: /* nothing left to do */
        break;
      case 1:
        d1 = data[data.length - 1] & 0xff;
        b1 = (d1 >>> 2) & 0x3f;
        b2 = (d1 << 4) & 0x3f;

        bytes[bytes.length - 4] = encodingTable[b1];
        bytes[bytes.length - 3] = encodingTable[b2];
        bytes[bytes.length - 2] = (byte) '=';
        bytes[bytes.length - 1] = (byte) '=';
        break;
      case 2:
        d1 = data[data.length - 2] & 0xff;
        d2 = data[data.length - 1] & 0xff;

        b1 = (d1 >>> 2) & 0x3f;
        b2 = ((d1 << 4) | (d2 >>> 4)) & 0x3f;
        b3 = (d2 << 2) & 0x3f;

        bytes[bytes.length - 4] = encodingTable[b1];
        bytes[bytes.length - 3] = encodingTable[b2];
        bytes[bytes.length - 2] = encodingTable[b3];
        bytes[bytes.length - 1] = (byte) '=';
        break;
    }

    return bytes;
  }

  public static byte[] decode(byte[] data) {
    int c;
    int c1;
    int len = data.length;
    byte[] b = new byte[(len * 3) / 4];
    int bi = 0;
    for (int i = 0; i < len; ++i) {
      c = indexOf(data[i]);
      ++i;
      c1 = indexOf(data[i]);
      c = ((c << 2) | ((c1 >> 4) & 0x3));
      b[bi++] = (byte) c;
      if (++i < len) {
        c = data[i];
        if ('=' == c) {
          break;
        }

        c = indexOf((byte) c);
        c1 = ((c1 << 4) & 0xf0) | ((c >> 2) & 0xf);
        b[bi++] = (byte) c1;
      }

      if (++i < len) {
        c1 = data[i];
        if ('=' == c1) {
          break;
        }

        c1 = indexOf((byte) c1);
        c = ((c << 6) & 0xc0) | c1;
        b[bi++] = (byte) c;
      }
    }

    if (b.length != bi) {
      byte[] temp = new byte[bi];
      System.arraycopy(b, 0, temp, 0, bi);
      b = temp;
    }

    return b;
  }

  //
  // decode the base 64 input data producing a binary byte array.
  //
  private static int indexOf(byte b) {
    if (b >= 'A' && b <= 'Z') {
      return b - 'A';
    } else if (b >= 'a' && b <= 'z') {
      return b - 'a' + 26;
    } else if (b >= '0' && b <= '9') {
      return b - '0' + 52;
    } else if (b == '+') {
      return 62;
    } else if (b == '/') {
      return 62 + 1;
    } else if (b == '=') {
      return 62 + 2;
    } else {
      return -1;
    }
  }

  public static String decode(String str) {
    return new String(decode(str.getBytes()));
  }

  public static String encode(String str) {
    return new String(encode(str.getBytes()));
  }
}