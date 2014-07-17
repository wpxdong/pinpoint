package com.nhn.pinpoint.common.buffer;

import com.nhn.pinpoint.common.util.BytesUtils;
import junit.framework.Assert;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * @author emeroad
 */
public class FixedBufferTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Random random = new Random();

    @Test
    public void testPutPrefixedBytes() throws Exception {
        String test = "test";
        int endExpected = 3333;
        testPutPrefixedBytes(test, endExpected);
        testPutPrefixedBytes(null, endExpected);
        testPutPrefixedBytes("", endExpected);
    }

    private void testPutPrefixedBytes(String test, int expected) throws UnsupportedEncodingException {
        Buffer buffer = new FixedBuffer(1024);
        if (test != null) {
            buffer.putPrefixedBytes(test.getBytes("UTF-8"));
        } else {
            buffer.putPrefixedString(null);
        }

        buffer.put(expected);
        byte[] buffer1 = buffer.getBuffer();

        Buffer actual = new FixedBuffer(buffer1);
        String s = actual.readPrefixedString();
        Assert.assertEquals(test, s);

        int i = actual.readInt();
        Assert.assertEquals(expected, i);
    }

    @Test
    public void testPadBytes() throws Exception {
        int TOTAL_LENGTH = 20;
        int TEST_SIZE = 10;
        int PAD_SIZE = TOTAL_LENGTH - TEST_SIZE;
        Buffer buffer = new FixedBuffer(32);
        byte[] test = new byte[10];

        random.nextBytes(test);

        buffer.putPadBytes(test, TOTAL_LENGTH);

        byte[] result = buffer.getBuffer();
        Assert.assertEquals(result.length, TOTAL_LENGTH);
        Assert.assertTrue("check data", Bytes.equals(test, 0, TEST_SIZE, result, 0, TEST_SIZE));
        byte[] padBytes = new byte[TOTAL_LENGTH - TEST_SIZE];
        Assert.assertTrue("check pad", Bytes.equals(padBytes, 0, TEST_SIZE, result, TEST_SIZE, PAD_SIZE));

    }

    @Test
    public void testPadBytes_Error() throws Exception {

        Buffer buffer1_1 = new FixedBuffer(32);
        try {
            buffer1_1.putPadBytes(new byte[11], 10);
        } catch (Exception e) {
        }

        Buffer buffer1_2 = new FixedBuffer(32);
        try {
            buffer1_2.putPadBytes(new byte[20], 10);
            Assert.fail("error");
        } catch (Exception e) {
        }

        Buffer buffer2 = new FixedBuffer(32);
        buffer2.putPadBytes(new byte[10], 10);

    }

    @Test
    public void testPadString() throws Exception {
        int TOTAL_LENGTH = 20;
        int TEST_SIZE = 10;
        int PAD_SIZE = TOTAL_LENGTH - TEST_SIZE;
        Buffer buffer= new FixedBuffer(32);
        String test = StringUtils.repeat('a', TEST_SIZE);

        buffer.putPadString(test, TOTAL_LENGTH);

        byte[] result = buffer.getBuffer();
        String decodedString = new String(result);
        String trimString = decodedString.trim();
        Assert.assertEquals(result.length, TOTAL_LENGTH);

        Assert.assertEquals("check data", test, trimString);

        String padString = new String(result, TOTAL_LENGTH - TEST_SIZE, PAD_SIZE, "UTF-8");
        byte[] padBytes = new byte[TOTAL_LENGTH - TEST_SIZE];
        Assert.assertEquals("check pad", padString, new String(padBytes, Charset.forName("UTF-8")));

    }

    @Test
    public void testPadString_Error() throws Exception {

        Buffer buffer1_1 = new FixedBuffer(32);
        try {
            buffer1_1.putPadString(StringUtils.repeat('a', 11), 10);
        } catch (Exception e) {
        }

        Buffer buffer1_2 = new FixedBuffer(32);
        try {
            buffer1_2.putPadString(StringUtils.repeat('a', 20), 10);
            Assert.fail("error");
        } catch (Exception e) {
        }

        Buffer buffer2 = new FixedBuffer(32);
        buffer2.putPadString(StringUtils.repeat('a', 10), 10);
    }

    @Test
    public void testPut2PrefixedBytes() throws Exception {
        String test = "test";
        int endExpected = 3333;

        checkPut2PrefixedBytes(test, endExpected);
        checkPut2PrefixedBytes(null, endExpected);
        checkPut2PrefixedBytes("", endExpected);

        byte[] bytes = new byte[Short.MAX_VALUE];
        checkPut2PrefixedBytes(BytesUtils.toString(bytes), endExpected, Short.MAX_VALUE * 2);

        try {
            byte[] bytes2 = new byte[Short.MAX_VALUE + 1];
            checkPut2PrefixedBytes(BytesUtils.toString(bytes2), endExpected, Short.MAX_VALUE * 2);
            Assert.fail("too large bytes");
        } catch (Exception e) {
        }

    }

    private void checkPut2PrefixedBytes(String test, int expected) throws UnsupportedEncodingException {
        checkPut2PrefixedBytes(test, expected, 1024);
    }

    private void checkPut2PrefixedBytes(String test, int expected, int bufferSize) throws UnsupportedEncodingException {
        Buffer buffer = new FixedBuffer(bufferSize);
        if (test != null) {
            buffer.put2PrefixedBytes(test.getBytes("UTF-8"));
        } else {
            buffer.put2PrefixedBytes(null);
        }

        buffer.put(expected);
        byte[] buffer1 = buffer.getBuffer();

        Buffer actual = new FixedBuffer(buffer1);
        String s = actual.read2PrefixedString();
        Assert.assertEquals(test, s);

        int i = actual.readInt();
        Assert.assertEquals(expected, i);
    }

    @Test
    public void testPut4PrefixedBytes() throws Exception {
        String test = "test";
        int endExpected = 3333;

        checkPut4PrefixedBytes(test, endExpected);
        checkPut4PrefixedBytes(null, endExpected);
        checkPut4PrefixedBytes("", endExpected);

    }

    private void checkPut4PrefixedBytes(String test, int expected) throws UnsupportedEncodingException {
        Buffer buffer = new FixedBuffer(1024);
        if (test != null) {
            buffer.put4PrefixedBytes(test.getBytes("UTF-8"));
        } else {
            buffer.put4PrefixedBytes(null);
        }

        buffer.put(expected);
        byte[] buffer1 = buffer.getBuffer();

        Buffer actual = new FixedBuffer(buffer1);
        String s = actual.read4PrefixedString();
        Assert.assertEquals(test, s);

        int i = actual.readInt();
        Assert.assertEquals(expected, i);
    }

    @Test
    public void testReadByte() throws Exception {

    }

    @Test
    public void testReadBoolean() throws Exception {

    }

    @Test
    public void testReadInt() throws Exception {

    }

    @Test
    public void testReadLong() throws Exception {

    }




    @Test
    public void testReadPrefixedString() throws Exception {

    }

    @Test
    public void testRead4PrefixedString() throws Exception {
        String value = "test";
        byte[] length = Bytes.toBytes(value.length());
        byte[] string = Bytes.toBytes(value);
        byte[] result = Bytes.add(length, string);


        Buffer buffer = new FixedBuffer(result);
        String prefixedString = buffer.read4PrefixedString();
        Assert.assertEquals(prefixedString, value);

    }

    @Test
    public void testRead4PrefixedString_Null() throws Exception {
        byte[] length = Bytes.toBytes(-1);


        Buffer buffer = new FixedBuffer(length);
        String prefixedString = buffer.read4PrefixedString();
        Assert.assertEquals(prefixedString, null);

    }

    @Test
    public void testPut() throws Exception {
        checkUnsignedByte(255);

        checkUnsignedByte(0);
    }

    @Test
    public void testPutVar32() throws Exception {
        checkVarInt(Integer.MAX_VALUE, 5);
        checkVarInt(25, 1);
        checkVarInt(100, 1);

        checkVarInt(Integer.MIN_VALUE, 10);

        checkVarInt(0, -1);
        checkVarInt(Integer.MAX_VALUE / 2, -1);
        checkVarInt(Integer.MAX_VALUE / 10, -1);
        checkVarInt(Integer.MAX_VALUE / 10000, -1);

        checkVarInt(Integer.MIN_VALUE / 2, -1);
        checkVarInt(Integer.MIN_VALUE / 10, -1);
        checkVarInt(Integer.MIN_VALUE / 10000, -1);

    }

    private void checkVarInt(int v, int offset) {
        Buffer buffer = new FixedBuffer(32);
        buffer.putVar(v);
        if (offset != -1) {
            Assert.assertEquals(buffer.getOffset(), offset);
        } else {
            logger.info("{} offsetSize:{}", v, buffer.getOffset());
        }
        buffer.setOffset(0);
        int readV = buffer.readVarInt();
        Assert.assertEquals(readV, v);
    }

    @Test
    public void testPutSVar32() throws Exception {
        // 63이 1바이트 경계.
        checkSVarInt(63, -1);
        // 8191이 2바이트 경계
        checkSVarInt((1024*8)-1, -1);

        checkSVarInt(3, -1);

        checkSVarInt(Integer.MAX_VALUE, 5);

        checkSVarInt(Integer.MIN_VALUE, 5);

        checkSVarInt(0, -1);
        checkSVarInt(Integer.MAX_VALUE / 2, -1);
        checkSVarInt(Integer.MAX_VALUE / 10, -1);
        checkSVarInt(Integer.MAX_VALUE / 10000, -1);

        checkSVarInt(Integer.MIN_VALUE / 2, -1);
        checkSVarInt(Integer.MIN_VALUE / 10, -1);
        checkSVarInt(Integer.MIN_VALUE / 10000, -1);


    }

    private void checkSVarInt(int v, int offset) {
        Buffer buffer = new FixedBuffer(32);
        buffer.putSVar(v);
        if (offset != -1) {
            Assert.assertEquals(buffer.getOffset(), offset);
        } else {
            logger.info("{} offsetSize:{}", v, buffer.getOffset());
        }
        buffer.setOffset(0);
        int readV = buffer.readSVarInt();
        Assert.assertEquals(readV, v);
    }

    @Test
    public void testPutVar64() throws Exception {

    }

    private void checkUnsignedByte(int value) {
        Buffer buffer = new FixedBuffer(1024);
        buffer.put((byte) value);
        byte[] buffer1 = buffer.getBuffer();

        Buffer reader = new FixedBuffer(buffer1);
        int i = reader.readUnsignedByte();
        Assert.assertEquals(value, i);
    }


    @Test
    public void testGetBuffer() throws Exception {
        Buffer buffer = new FixedBuffer(4);
        buffer.put(1);
        Assert.assertEquals(buffer.getOffset(), 4);
        Assert.assertEquals(buffer.getBuffer().length, 4);
    }

    @Test
    public void testSliceGetBuffer() throws Exception {
        Buffer buffer = new FixedBuffer(5);
        buffer.put(1);
        Assert.assertEquals(buffer.getOffset(), 4);
        Assert.assertEquals(buffer.getBuffer().length, 4);

        byte[] buffer1 = buffer.getBuffer();
        byte[] buffer2 = buffer.getBuffer();
        Assert.assertTrue(buffer1 != buffer2);

    }

    @Test
    public void testBoolean() {
        Buffer buffer = new FixedBuffer(16);
        buffer.put(true);
        buffer.put(false);

        Buffer read = new FixedBuffer(buffer.getBuffer());
        boolean b = read.readBoolean();
        Assert.assertEquals(true, b);

        boolean c = read.readBoolean();
        Assert.assertEquals(false, c);
    }

    @Test
    public void testGetOffset() throws Exception {
        Buffer buffer = new FixedBuffer();
        Assert.assertEquals(buffer.getOffset(), 0);

        buffer.put(4);
        Assert.assertEquals(buffer.getOffset(), 4);

    }
}
