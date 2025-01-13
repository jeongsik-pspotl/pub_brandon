package com.pspotl.sidebranden.manager.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Base64;
import java.util.regex.Pattern;

@Slf4j
public class BaseUtility {

    public static final String alg = "AES/CBC/PKCS5Padding";
    private static final String key = "01234567890123456789012345678901";
    private static final String iv = key.substring(0, 16); // 16byte

    public String aes256Encrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String aes256Decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, "UTF-8");
    }

    // 2.1 common cli mkdir
    public int executueCommonsCLIToTemplateCopy(CommandLine commandLineParse, String commandType) throws IOException {

        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();

        BufferedReader is = null;
        String tmp = "";

        try {
            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(commandLineParse, resultHandler);

            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));

            while ((tmp = is.readLine()) != null) {
                log.info(" #### Gradle git clone CLI CommandLine log data ### : " + tmp);

            }

            resultHandler.waitFor();
            handler.stop();

            int exitCode = resultHandler.getExitValue();
            if(exitCode == 0){
                return exitCode;
                // send message type

            }else if(exitCode == 1){

                return exitCode;
            }else {
                return exitCode;
            }

        } catch (IOException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            return -1;

        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            return -1;

        } finally {
            if(is != null) is.close();
        }
    }

    /**
     * 빌드된 파일명에 yyyyMMddHHmmss 형태의 시간정보를 붙여서 리턴한다.
     *
     * @param originalFileName 원래 파일명
     * @param timeString 기준이 되는 시간 정보 (빌드 시작때 서버에서 만든 시간)
     * @return 빌드된 파일명에 yyyyMMddHHmmss 형태의 시간정보를 붙여서 리턴
     */
    public String makeBuildedFileName(String originalFileName, String timeString) {
        // yyyyMMddHHmm, yyyyMMddHHmmss
        String pattern = "\\d{4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])([0-1][0-9]|2[0-3])([0-5][0-9])+";

        // 기존 파일명의 다른 yyyyMMddHHmmss 형태의 문자열이 있으면 인자로 들어온 값으로 바꿔서 리턴
        if (Pattern.compile(pattern).matcher(originalFileName).find()) {
            return originalFileName.replaceAll(pattern, timeString);
        }
//        // 기존 파일명의 다른 yyyyMMddHHmm 형태의 문자열이 있으면 인자로 들어온 값으로 바꿔서 리턴
//        else if (Pattern.compile(pattern2).matcher(originalFileName).find()) {
//            return originalFileName.replaceAll(pattern2, timeString);
//        }
        // yyyyMMddHHmmss 형태의 문자열이 없으면 뒤에 붙여서 리턴
        else {
            return originalFileName;// + "-" + timeString;
        }
    }
}
