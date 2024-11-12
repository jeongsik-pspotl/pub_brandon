package com.inswave.whive.common.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CommonUtils {

    public static boolean isWindows = File.separatorChar == '\\' ? true : false;

    public static <T> Collection<List<T>> partition(List<T> list, int size) {

        final AtomicInteger counter = new AtomicInteger(0);

        return list.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size))
                .values();
    }

    public static void copyFolder(File sourceFolder, File destinationFolder) {
        try {
            if (sourceFolder.isDirectory()) {

                if (!destinationFolder.exists()) {
                    destinationFolder.mkdir();
//                    System.out.println("Directory created :: " + destinationFolder);
                }

                String files[] = sourceFolder.list();

                for (String file : files) {
                    File srcFile = new File(sourceFolder, file);
                    File destFile = new File(destinationFolder, file);

                    copyFolder(srcFile, destFile);
                }
            } else {
                Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                System.out.println("File copied :: " + destinationFolder);
            }
        } catch (IOException e) {
            
        }
    }

//    public static List<UpdateFiles> copyFolder(File sourceFolder, File destinationFolder, int baseLength,
//                                               String ostype, String appId, String version, String scope, List<UpdateFiles> filesList) {
//        try {
//            if (sourceFolder.isDirectory()) {
//
//                if (!destinationFolder.exists()) {
//                    destinationFolder.mkdir();
//                }
//
//                String files[] = sourceFolder.list();
//
//                for (String file : files) {
//                    File srcFile = new File(sourceFolder, file);
//                    File destFile = new File(destinationFolder, file);
//
//                    copyFolder(srcFile, destFile, baseLength, ostype, appId, version, scope, filesList);
//                }
//            } else {
//                Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                filesList.add(getUpdateFile(ostype, appId, version, scope, destinationFolder, baseLength));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return filesList;
//    }

//    public static List<WgearDistFiles> copyFolder(File sourceFolder, File destinationFolder, int baseLength,
//                                                  String appId, String version, String scope, List<WgearDistFiles> filesList) {
//        try {
//            if (sourceFolder.isDirectory()) {
//
//                if (!destinationFolder.exists()) {
//                    destinationFolder.mkdir();
//                }
//
//                String files[] = sourceFolder.list();
//
//                for (String file : files) {
//                    File srcFile = new File(sourceFolder, file);
//                    File destFile = new File(destinationFolder, file);
//
//                    copyFolder(srcFile, destFile, baseLength, appId, version, scope, filesList);
//                }
//            } else {
//                Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                filesList.add(getWgerDistFile(appId, version, scope, destinationFolder, baseLength));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return filesList;
//    }

//    public static WgearDistFiles getWgerDistFile(String appId, String version, String scope, File file, int baseLength) {
//        WgearDistFiles wgearDistFile = new WgearDistFiles();
//        wgearDistFile.setAppId(appId);
//        wgearDistFile.setVersion(version);
//        wgearDistFile.setScope(scope);
//        wgearDistFile.setPath(file.getPath().substring(baseLength));
//
//        if (isWindows) {
//            wgearDistFile.setPath(file.getPath().substring(baseLength).replaceAll("\\\\", "/"));
//        } else {
//            wgearDistFile.setPath(file.getPath().substring(baseLength));
//        }
//
//        wgearDistFile.setCompareKey(makeHash(file).toUpperCase());
//        return wgearDistFile;
//    }

//    public static UpdateFiles getUpdateFile(String ostype, String appId, String version, String scope, File file, int baseLength) {
//        UpdateFiles updateFiles = new UpdateFiles();
//        updateFiles.setOsType(ostype);
//        updateFiles.setAppId(appId);
//        updateFiles.setVersion(version);
//        updateFiles.setScope(scope);
//        updateFiles.setPath(file.getPath().substring(baseLength));
//
//        if (isWindows) {
//            updateFiles.setPath(file.getPath().substring(baseLength).replaceAll("\\\\", "/"));
//        } else {
//            updateFiles.setPath(file.getPath().substring(baseLength));
//        }
//
//        updateFiles.setCompareKey(makeHash(file).toUpperCase());
//        return updateFiles;
//    }

    public static String makeHash(File file) {

        BufferedInputStream bis = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            return makeHash(bis);
        } catch (FileNotFoundException e) {
                
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
        }

        return "";
    }

    private static String makeHash(InputStream in) {

        StringBuffer sb = new StringBuffer();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] buf = new byte[1024];
            int read = 0;
            while ((read = in.read(buf)) != -1) {
                md.update(buf, 0, read);
            }
            byte[] mdbytes = md.digest();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (Exception e) {
                
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
            }
        }

        return sb.toString();
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] descriptors = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor descriptor : descriptors) {
            Object srcValue = src.getPropertyValue(descriptor.getName());
            if (srcValue == null) {
                emptyNames.add(descriptor.getName());
            }
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

}
