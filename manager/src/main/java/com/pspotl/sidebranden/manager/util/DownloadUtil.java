package com.pspotl.sidebranden.manager.util;

import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class DownloadUtil {
    public void filDown(HttpServletRequest request,
                        HttpServletResponse response, String filePath, String realFilNm,
                        String viewFileNm) throws IOException {
        File file = new File(filePath + realFilNm);

        if (file.exists() && file.isFile()) {
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setContentLength((int) file.length());
            String browser = getBrowser(request);
            String disposition = getDisposition(viewFileNm, browser);
            response.setHeader("Content-Disposition", disposition);
            response.setHeader("Content-Transfer-Encoding", "binary");
            OutputStream out = response.getOutputStream();
            FileInputStream fis = null;
            fis = new FileInputStream(file);

            FileCopyUtils.copy(fis, out);

            if (fis != null)
                fis.close();

            out.flush();
            out.close();
        }
    }

    private String getBrowser(HttpServletRequest request) {
        String header = request.getHeader("User-Agent");
        if (header.indexOf("MSIE") > -1 || header.indexOf("Trident") > -1)
            return "MSIE";
        else if (header.indexOf("Chrome") > -1)
            return "Chrome";
        else if (header.indexOf("Opera") > -1)
            return "Opera";
        return "Firefox";
    }

    private String getDisposition(String filename, String browser)
            throws UnsupportedEncodingException {
        String dispositionPrefix = "attachment;filename=";
        String encodedFilename = null;
        if (browser.equals("MSIE")) {
            encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.name()).replaceAll(
                    "\\+", "%20");
        } else if (browser.equals("Firefox")) {
            encodedFilename = "\""
                    + new String(filename.getBytes(StandardCharsets.UTF_8.name()), StandardCharsets.ISO_8859_1) + "\"";
        } else if (browser.equals("Opera")) {
            encodedFilename = "\""
                    + new String(filename.getBytes(StandardCharsets.UTF_8.name()), StandardCharsets.ISO_8859_1) + "\"";
        } else if (browser.equals("Chrome")) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < filename.length(); i++) {
                char c = filename.charAt(i);
                if (c > '~') {
                    sb.append(URLEncoder.encode("" + c, StandardCharsets.UTF_8.name()));
                } else {
                    sb.append(c);
                }
            }
            encodedFilename = sb.toString();
        }
        return dispositionPrefix + encodedFilename;
    }

}
