package com.pspotl.sidebranden.manager.util.system;

import com.pspotl.sidebranden.manager.util.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class Execute {

    public static final String version = "1.01";
    public static final String buildDate = "2006-02-11";

    String errString = "";
    String outString = "";

    int[] isEnd = new int[1];
    boolean[] isOtherEnd = new boolean[1];

    private static final Logger logger = LoggerFactory.getLogger(Execute.class);

    /**
     * Executes the specified string command in a separate process.
     *
     * @param command
     *            a specified system command.
     * @return execution result
     * @exception Exception
     *                if an exception occurs.
     */

    /**
     * Executes the specified string command in a separate process.
     *
     * @param command
     *            a specified system command.
     * @return execution result
     * @exception Exception
     *                if an exception occurs.
     */
    public String[] execArray(String command) throws Exception {
        // http://developer.java.sun.com/developer/qow/archive/135/index.jsp
        // http://www.javaservice.net/~java/bbs/read.cgi?m=resource&b=javatip&c=r_p&n=955781183
        // http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps_p.html

        String[] result = new String[3];
        this.errString = "";
        this.outString = "";
        this.isOtherEnd[0] = false;

        try {

            Process proc = Runtime.getRuntime().exec(command);

            boolean debug = false;

            // framework�� loading�Ǵ� �� license�� Ȯ�� �ϱ� '�ؼ� ȣ��Ǵ� ����̹Ƿ� Framework�� ��õ� API�� ����ϸ� ��ȯ ��v�� �7� �߻��Ѵ�.
            ErrorStreamHandler1 errorGobbler = new ErrorStreamHandler1(proc.getErrorStream(), debug);
            OutStreamHandler1 outputGobbler = new OutStreamHandler1(proc.getInputStream(), debug);

            errorGobbler.setName("Framework_Execute_STDERR");
            outputGobbler.setName("Framework_Execute_STDOUT");

            errorGobbler.start();
            outputGobbler.start();

            synchronized (isEnd) {
                isEnd.wait();
            }

            int exitVal = proc.waitFor();
            try {
                proc.getErrorStream().close();
                proc.getInputStream().close();
                proc.getOutputStream().close();
            } catch (Exception ee) {}
            result[0] = exitVal + "";
            result[1] = outString;
            result[2] = errString;
        } catch (InterruptedException e) {
            logger.warn(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return result;
    }

    /**
     * Executes the specified string command in a separate process.
     *
     * @return execution result
     * @exception Exception
     *                if an exception occurs.
     */
    public String[] execArray(String[] commandArray) throws Exception {
        // http://developer.java.sun.com/developer/qow/archive/135/index.jsp
        // http://www.javaservice.net/~java/bbs/read.cgi?m=resource&b=javatip&c=r_p&n=955781183
        // http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps_p.html

        String[] result = new String[3];
        this.errString = "";
        this.outString = "";
        this.isOtherEnd[0] = false;

        try {

            Process proc = Runtime.getRuntime().exec(commandArray);

            boolean debug = false;

            // framework�� loading�Ǵ� �� license�� Ȯ�� �ϱ� '�ؼ� ȣ��Ǵ� ����̹Ƿ� Framework�� ��õ� API�� ����ϸ� ��ȯ ��v�� �7� �߻��Ѵ�.
            ErrorStreamHandler1 errorGobbler = new ErrorStreamHandler1(proc.getErrorStream(), debug);
            OutStreamHandler1 outputGobbler = new OutStreamHandler1(proc.getInputStream(), debug);

            errorGobbler.setName("Framework_Execute_STDERR");
            outputGobbler.setName("Framework_Execute_STDOUT");

            errorGobbler.start();
            outputGobbler.start();

            synchronized (isEnd) {
                isEnd.wait();
            }

            int exitVal = proc.waitFor();
            try {
                proc.getErrorStream().close();
                proc.getInputStream().close();
                proc.getOutputStream().close();
            } catch (Exception ee) {}
            result[0] = exitVal + "";
            result[1] = outString;
            result[2] = errString;
        } catch (InterruptedException e) {
            logger.warn(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Executes the specified string command in a separate process.
     *
     * @param command
     *            an array of specified system command strings.
     * @return execution result
     * @exception Exception
     *                if an exception occurs.
     */

    /**
     * Executes the specified string command in a separate process.
     *
     * @param command
     *            Specified system command string.
     * @return execution Document
     * @exception Exception
     *                if an exception occurs.
     */

    /**
     * Executes the specified string command in a separate process.
     *
     * @param command
     *            array containing the command to call and its arguments.
     * @return execution Document
     * @exception Exception
     *                if an exception occurs.
     */

    /**
     * Executes the specified string command in a separate process.
     *
     * @param command
     *            array containing the command to call and its arguments.
     * @param envp
     *            array of strings, each element of which has environment variable settings in format name=value.
     * @return execution Document
     * @exception Exception
     *                if an exception occurs.
     */

    /**
     * Executes the specified string command in a separate process.
     *
     * @return execution Document
     * @exception Exception
     *                if an exception occurs.
     */

    class ErrorStreamHandler extends Thread {}

    class OutStreamHandler extends Thread {}

    class ErrorStreamHandler1 extends Thread {
        InputStream is;
        boolean debug;

        ErrorStreamHandler1(InputStream is, boolean debug) {
            this.is = is;
            this.debug = debug;
        }

        public void run() {
            try {
                is = new BufferedInputStream(is);

                ByteBuffer bb = new ByteBuffer();
                int size = 1024;

                byte[] buf = new byte[size];
                int readSize = 0;
                int totalRead = 0;

                while (true) {
                    readSize = is.read(buf, totalRead, size - totalRead);
                    if (readSize == -1) {
                        if (totalRead > 0) {
                            bb.append(buf, totalRead);
                        }
                        break;
                    }

                    totalRead += readSize;
                    if (totalRead == size) {
                        bb.append(buf, totalRead);
                        totalRead = 0;
                    }
                }
                Execute.this.errString = bb.toString();

                try {
                    Thread.sleep(1);
                } catch (Exception e) {}
                synchronized (Execute.this.isOtherEnd) {
                    if (Execute.this.isOtherEnd[0]) {
                        synchronized (Execute.this.isEnd) {
                            Execute.this.isEnd.notify();
                        }
                    } else {
                        Execute.this.isOtherEnd[0] = true;
                    }
                }
            } catch (Exception ioe) {

                logger.warn(ioe.getMessage(), ioe);
            }
        }
    }

    class OutStreamHandler1 extends Thread {
        InputStream is;
        boolean debug;

        OutStreamHandler1(InputStream is, boolean debug) {
            this.is = is;
            this.debug = debug;
        }

        public void run() {
            try {
                is = new BufferedInputStream(is);

                ByteBuffer bb = new ByteBuffer();
                int size = 1024;

                byte[] buf = new byte[size];
                int readSize = 0;
                int totalRead = 0;

                while (true) {
                    readSize = is.read(buf, totalRead, size - totalRead);
                    if (readSize == -1) {
                        if (totalRead > 0) {
                            bb.append(buf, totalRead);
                        }
                        break;
                    }

                    totalRead += readSize;
                    if (totalRead == size) {
                        bb.append(buf, totalRead);
                        totalRead = 0;
                    }
                }
                Execute.this.outString = bb.toString();

                try {
                    Thread.sleep(1);
                } catch (Exception e) {}
                synchronized (Execute.this.isOtherEnd) {
                    if (Execute.this.isOtherEnd[0]) {
                        synchronized (Execute.this.isEnd) {
                            Execute.this.isEnd.notify();
                        }
                    } else {
                        Execute.this.isOtherEnd[0] = true;
                    }
                }
            } catch (Exception ioe) {

                logger.warn(ioe.getMessage(), ioe);
            }
        }
    }

}
