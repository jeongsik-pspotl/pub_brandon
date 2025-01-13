package com.pspotl.sidebranden.manager.util;

import java.util.Vector;

public class ByteBuffer {

    private Vector vec = null;
    public int length;
    public static final String version = "1.00";
    public static final String buildDate = "2006-02-08";

    /* 초기 16 byte 배열 */
    public ByteBuffer() {
        vec = new Vector();
        length = 0;
    }

    public ByteBuffer(byte[] b) {
        vec = new Vector();
        length = 0;
        byte[] tmp = new byte[b.length];
        System.arraycopy(b, 0, tmp, 0, b.length);

        vec.addElement(tmp);
        length += b.length;
    }

    /**
     * byte값에 인자값을 추가한다.
     */
    public void append(byte[] b) {
        byte[] tmp = new byte[b.length];
        System.arraycopy(b, 0, tmp, 0, b.length);

        vec.addElement(tmp);
        length += b.length;
    }

    /**
     * byter값에 인자값을 legth만큼 추가한다.
     */
    public void append(byte[] b, int length) {
        byte[] tmp = new byte[length];
        System.arraycopy(b, 0, tmp, 0, length);

        vec.addElement(tmp);
        this.length += length;
    }

    /**
     * byte값에 인자값을 추가한다.
     */
    public void append(byte b) {
        byte[] tmp = new byte[1];
        tmp[0] = b;
        vec.addElement(tmp);
        length += 1;
    }

    /**
     * byte값을 String으로 변환하여 반환한다.
     */
    public String toString() {
        byte[] ret = getBytes();
        return new String(ret);
    }

    /**
     * byte값을 해당 characterSet String pattern으로 변환하여 반환한다.
     */
    public String toString(String characterSet) throws java.io.UnsupportedEncodingException {
        byte[] ret = getBytes();
        return new String(ret, characterSet);
    }

    public byte[] getBytes() {
        // 길이까지만 반환한다.
        byte[] ret = new byte[length];
        int idx = 0;
        for (int i = 0, vecSize = vec.size(); i < vecSize; i++) {
            byte[] tmp = (byte[]) vec.elementAt(i);
            System.arraycopy(tmp, 0, ret, idx, tmp.length);
            idx += tmp.length;
        }
        return ret;
    }

    /**
     * 현재 길이를 가져온다
     */
    public int length() {
        return length;
    }

}
