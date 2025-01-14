package com.pspotl.sidebranden.builder.service;


import lombok.extern.slf4j.Slf4j;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


// zip file temp 전송
@Slf4j
@Component
public class ZipfileImportService {

    // deploy root path
    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    // temp dir to zip file 전송
    // @param byte zipFileObj
    // @param String fileName
    // @param String ?? Json
    public void tempToZipFileUpload(byte[] zipFileObj) throws IOException {

        File fTmp = null;
        ByteArrayInputStream ins = null;
        BufferedOutputStream zoa = null;
        OutputStream output = null;

        byte[] byteBuff = new byte[1024];
        int bytesRead = 0;

        Base64.Decoder decoder = Base64.getDecoder();
        byteBuff = decoder.decode(zipFileObj);

        try {

            zoa = new BufferedOutputStream(
                    new FileOutputStream(
                            new File(userRootPath+"tempZipDir/", "TmpProject.zip")));

            log.info(" tempToZipFileUpload entry in... ");
            //ins = new ByteArrayInputStream(zipFileObj);
            log.info("byteBuff length : {}",byteBuff.length);
            zoa.write(byteBuff);
            zoa.flush();
            zoa.close();

            log.info("tempToZipFileUpload try check.. ");
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        } finally {

        }
        // inputstream
        // path userRootPath + fileName
        // File 객체 생성

    }

    private static void appendZip(ZipOutputStream out, String base, InputStream originInput, File destFile) throws Exception {
        zipOperation(out, base, originInput, destFile);
    }

    private static void zipOperation(ZipOutputStream out, String base, InputStream originInput, File destFile) throws IOException {
        out.putNextEntry(new ZipEntry(base + "tempZipDir/" + destFile.getName()));
        log.info(" zipOperation originInput available {}", originInput.available());
        IOUtils.copy(originInput, out);
        //copy(originInput, out);
        IOUtils.closeQuietly(out);
        IOUtils.closeQuietly(originInput);
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        synchronized (in) {
            synchronized (out) {
                byte[] buffer = new byte[1024];
                while (true) {
                    int bytesRead = in.read(buffer);
                    if (bytesRead == -1) break;
                    out.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    /** * zip 만들기 메소드 *
     * @param file 파일
     * @param zis Zip스트림
     */
    private void createFile(File file, ZipInputStream zis) throws Throwable { // 디렉토리 확인
        File parentDir = new File(file.getParent()); // 디렉토리가 없으면 생성하자
        log.info("createFile start");
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        log.info("createFile doing... ");
        FileOutputStream fos = null; // 파일 스트림 선언

        try {
            log.info("createFile try start ");
            log.info("createFile try ZipInputStream size {} ", zis.available());
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[256];
            int size = 0; // Zip스트림으로부터 byte뽑아내기
            while ((size = zis.read(buffer)) > 0) { // byte로 파일 만들기
                log.info("zip file size : {}", size);
                fos.write(buffer, 0, size);
            }
        } catch (Throwable e) {
            throw e;
        } finally {
            if (fos != null) {
                try {

                    fos.close();
                } catch (IOException e) {

                }
            }
        }
    }

    // temp zip file unzip cli
    private void zipFileCLIAction(){

    }

    // project import 완료 메시지 전송
    private void zipFileStatusMessage(){

    }

}
