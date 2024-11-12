package com.inswave.whive.branch.util;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.util.UnzipUtil;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
public class ZipUtils {

    /**
     * <pre>파일압축 해제</pre>
     *
     * @param zipFileName 압축파일명
     * @param directory 압축이 풀릴 파일 경로
     * @throws Throwable
     */
    public void decompress(String zipFileName, String directory) throws Throwable {

        File zipFile = new File(zipFileName);
        FileInputStream fis = null;
        ZipInputStream zis = null;
        ZipEntry zipentry = null;

        try {
            //파일 스트림
            fis = new FileInputStream(zipFile);
            //Zip 파일 스트림
            zis = new ZipInputStream(fis);

            //entry가 없을때까지 뽑기
            while ((zipentry = zis.getNextEntry()) != null) {
                String filename = zipentry.getName();
                File file = new File(directory, filename);

                //entiry가 폴더면 폴더 생성
                if (zipentry.isDirectory()) {
                    file.mkdirs();
                } else {
                    //파일이면 파일 만들기
                    createFile(file, zis);
                }
            }
        } catch (Throwable e) {
            throw e;
        } finally {

            if (zis != null)
                zis.close();

            if (fis != null)
                fis.close();

            if(zipFile != null)
                zipFile.delete();

        }

    }

    /**
     * <pre>파일압축 해제 Android Template</pre>
     *
     * @param zipFileName 압축파일명
     * @param directoryhdpi 압축이 풀릴 파일 경로
     * @param directorymdpi 압축이 풀릴 파일 경로
     * @param directoryxhdpi 압축이 풀릴 파일 경로
     * @throws Throwable
     */
    public void decompressFormAndroid(String zipFileName, String directoryhdpi, String directorymdpi, String directoryxhdpi) throws Throwable {

        File zipFile = new File(zipFileName);
        FileInputStream fis = null;
        ZipInputStream zis = null;
        ZipEntry zipentry = null;
        String directory = "";
        File file = null;

        try {
            //파일 스트림
            fis = new FileInputStream(zipFile);
            //Zip 파일 스트림
            zis = new ZipInputStream(fis);

            //entry가 없을때까지 뽑기
            while ((zipentry = zis.getNextEntry()) != null) {
                String filename = zipentry.getName();

                if(filename.matches(".*-hdpi.*")){
                    directory = directoryhdpi;
                    // project_name substr 처리
                    filename = filename.replace("-hdpi","");
                }else if(filename.matches(".*-mdpi.*")){
                    directory = directorymdpi;
                    // project_name substr 처리
                    filename = filename.replace("-mdpi","");
                }else if(filename.matches(".*-xhdpi.*")){
                    directory = directoryxhdpi;
                    // project_name substr 처리
                    filename = filename.replace("-xhdpi","");
                }

                file = new File(directory, filename);

                //entry가 폴더면 폴더 생성
                if (zipentry.isDirectory()) {
                    file.mkdirs();
                } else {
                    //파일이면 파일 만들기
                    createFile(file, zis);
                }

            }
        } catch (Throwable e) {
            throw e;
        } finally {

            if (zis != null)
                zis.close();

            if (fis != null)
                fis.close();

        }

    }


    /**
     * <pre>파일압축 해제 Android Template</pre>
     *
     * @param zipFileName 압축파일명
     * @param directoryxhdpi 압축이 풀릴 파일 경로
     * @throws Throwable
     */
    public void decompressFormAndroidIcon(String zipFileName, String directoryxhdpi) throws Throwable {

        File zipFile = new File(zipFileName);
        FileInputStream fis = null;
        ZipInputStream zis = null;
        ZipEntry zipentry = null;
        String directory = "";
        File file = null;

        try {
            //파일 스트림
            fis = new FileInputStream(zipFile);
            //Zip 파일 스트림
            zis = new ZipInputStream(fis);

            //entry가 없을때까지 뽑기
            while ((zipentry = zis.getNextEntry()) != null) {
                String filename = zipentry.getName();

               if(filename.matches(".*-xhdpi.*")){
                    directory = directoryxhdpi;
                    // project_name substr 처리
                    filename = filename.replace("-xhdpi","");
                }else {
                   directory =  directoryxhdpi;
                   filename = filename.replace(filename,"logo.png");
               }

                file = new File(directory, filename);

                //entry가 폴더면 폴더 생성
                if (zipentry.isDirectory()) {
                    file.mkdirs();
                } else {
                    //파일이면 파일 만들기
                    createFile(file, zis);
                }

            }
        } catch (Throwable e) {
            throw e;
        } finally {

            if (zis != null)
                zis.close();

            if (fis != null)
                fis.close();
                // zipFile.delete();

        }

    }

    /**
     * 폴더내 파일 압축
     * @param sourceDirPath
     * @param zipName
     * @throws IOException
     */
    public static void makeZipFromDir( String sourceDirPath, String zipName) throws IOException {

        log.info("makeZipFromDir() + zipName : " + zipName);
        log.info("makeZipFromDir() + sourceDirPath : " + sourceDirPath);
        log.info("test zip file create ... " + sourceDirPath);

        ZipFile zipFile = new ZipFile(zipName);

        zipFile.addFolder(new File(sourceDirPath));
        zipFile.close();


        // ZipUtil.pack(sourceDir, targetZipFile);
//        Path p = Files.createFile(Paths.get(zipName));
//        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
//            Path pp = Paths.get(sourceDirPath);
//            Files.walk(pp)
//                    .filter(path -> !Files.isDirectory(path))
//                    .forEach(path -> {
//                        try {
//                        log.info(String.valueOf(path));
//                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
//                            zs.putNextEntry(zipEntry);
//                            Files.copy(path, zs);
//                            zs.closeEntry();
//                        } catch (Exception e) {
//                            log.warn(e.getMessage(), e);
//                        }
//                    });
//        } catch (Exception e){
//            log.warn(e.getMessage(), e);
//        }

    }

    public static void unzipAction(Path source, Path target){

        try {
            new ZipFile(source.toFile()).extractAll(target.toString());
            /**
             *   프로젝트 Import 한 zip 파일을 삭제 하는 기능
             */
            File delFile = source.toFile();
            delFile.delete();

        } catch (ZipException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * 파일 압축
     * @param dir
     * @param fileName
     * @param zipName
     * @throws IOException
     */

    public static void makeZip( String dir, String fileName, String zipName) throws IOException {

        ZipOutputStream zos = new ZipOutputStream( new FileOutputStream( dir + zipName));

        byte[] buf = new byte[1024];
        FileInputStream in = new FileInputStream(dir+File.separator + fileName);
        zos.putNextEntry( new ZipEntry( fileName));

        int len;
        while( (len = in.read(buf)) > 0) {
            zos.write( buf, 0, len);
        }

        zos.closeEntry();
        in.close();
        zos.close();

    }

    /**
     * <pre>파일 경로에 압축 푼 파일 생성</pre>
     *
     * @param file
     * @param zis
     * @throws Throwable
     */
    private static void createFile(File file, ZipInputStream zis) throws Throwable {

        //디렉토리 확인
        File parentDir = new File(file.getParent());
        //디렉토리가 없으면 생성하자
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        //파일 스트림 선언
        try (FileOutputStream fos = new FileOutputStream(file)) {

            byte[] buffer = new byte[256];
            int size = 0;
            //Zip스트림으로부터 byte뽑아내기
            while ((size = zis.read(buffer)) > 0) {
                //byte로 파일 만들기
                fos.write(buffer, 0, size);
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            throw e;
        }
    }

    private static void handleDir(String dirPath, File[] files, ZipOutputStream zipOut) throws Exception {
        final int BUFFER = 1024 * 4;

        for (File file : files) {
            if(!file.exists()){
                continue;
            }

            String fileName = file.toString().substring(dirPath.length()+1);

            if (file.isDirectory()) {
                File[] lf = file.listFiles();

                if (lf == null) {
                    throw new FileNotFoundException();
                }

                if (lf.length == 0) {
                    try {
                        zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                    } catch (IOException ie) {
                       // ie.printStackTrace();
                    } finally {
                        zipOut.closeEntry();
                    }
                } else {
                    handleDir(dirPath,lf, zipOut);
                }
            } else {
                FileInputStream fis = null;

                try {
                    fis = new FileInputStream(file);
                    zipOut.putNextEntry(new ZipEntry(getFileName(fileName)));
                    byte[] bytes =new byte[BUFFER];
                    int c = 0;
                    while ((c = fis.read(bytes))!=-1) {
                        zipOut.write(bytes, 0, c);
                    }

                } catch (IOException ie) {
                    //ie.printStackTrace();
                } finally {
                    zipOut.close();
                    if (fis != null) fis.close();
                }
            }
        }
    }

    private static String getFileName(String fileName){
        String[] str=fileName.split("/");
        return str[str.length-1];
    }

    private static void handleFile(File[] files, ZipOutputStream zipOut) throws Exception {
        for (File file : files) {
            if(!file.exists()){
                continue;
            }

            String dirPath = file.toString().substring(0, file.toString().lastIndexOf(File.separator));
            handleDir(dirPath,new File[]{file},zipOut);
        }
    }

}

