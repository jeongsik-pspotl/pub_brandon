package com.pspotl.sidebranden.builder.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.builder.domain.ProjectSettingImageMessage;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.ProjectServiceType;
import com.pspotl.sidebranden.builder.util.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
public class AppIconZipfileBinMessageHandler implements BranchBinaryHandle{

    private static final Integer defaultOffset = 4;

    private ObjectMapper Mapper = new ObjectMapper();

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    private ZipUtils zipUtils;

    @Value("${whive.distribution.profilePath}")
    private String profileSetDir;

    @Value("${whive.distribution.UserRootPath}")
    private String UserRootPath;

    @Value("${whive.distribution.AndoridLogoxhdpiPath}")
    private String androidIconPath;

    @Value("${whive.distribution.iOSAppIconPath}")
    private String iOSIconPath;

    private String systemUserHomePath = System.getProperty("user.home");

    @Override
    public void handleBinary(WebSocketSession session, Map<String, Object> parseResult) {

    }

    @Override
    public void handleBinaryPayLoad(WebSocketSession session, String msgType, byte[] payload) {

        if(msgType.equals(ProjectServiceType.BIN_FILE_APP_ICON_APPEND_SEND_INFO.name())){



            byte[] adminIdLengthBuf = new byte[defaultOffset];

            File profile = null;

            // branch id length
            Integer offset = 0;
            System.arraycopy(payload, offset, adminIdLengthBuf, 0, defaultOffset);
            Integer adminIdIntLength = byteArrayToInt(adminIdLengthBuf);
            offset = offset + defaultOffset;

            // branch id
            byte[] adminIdBuf = new byte[adminIdIntLength];
            System.arraycopy(payload, offset, adminIdBuf, 0, adminIdIntLength);
            String adminId = new String(adminIdBuf, Charset.forName(StandardCharsets.UTF_8.name()));
            byte[] adminIdBytes = adminId.getBytes(StandardCharsets.UTF_8);
            Integer adminLength = adminIdBytes.length;
            offset = offset + adminLength;

            // bin type length
            byte[] binTypeLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, binTypeLengthBuf, 0, defaultOffset);
            Integer binTypeIntLength = byteArrayToInt(binTypeLengthBuf);
            offset = offset + defaultOffset;

            // bin type
            byte[] binTypeBuf = new byte[binTypeIntLength];
            System.arraycopy(payload, offset, binTypeBuf, 0, binTypeIntLength);
            String binType = new String(binTypeBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] binTypeBytes = binType.getBytes(StandardCharsets.UTF_8);
            Integer binTypeLength = binTypeBytes.length;
            offset = offset + binTypeLength;

            byte[] userIDLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, userIDLengthBuf, 0, defaultOffset);
            Integer userIDIntLength = byteArrayToInt(userIDLengthBuf);
            offset = offset + defaultOffset;

            byte[] userIDBuf = new byte[userIDIntLength];
            System.arraycopy(payload, offset, userIDBuf, 0, userIDIntLength);
            String userID = new String(userIDBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] userIDBytes = userID.getBytes(StandardCharsets.UTF_8);
            Integer userIDLength = userIDBytes.length;
            offset = offset + userIDLength;

            byte[] hqKeyLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, hqKeyLengthBuf, 0, defaultOffset);
            Integer hqKeyIntLength = byteArrayToInt(hqKeyLengthBuf);
            offset = offset + defaultOffset;

            byte[] hqKeyBuf = new byte[hqKeyIntLength];
            System.arraycopy(payload, offset, hqKeyBuf, 0, hqKeyIntLength);
            String hqkey = new String(hqKeyBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] hqKeyBytes = hqkey.getBytes(StandardCharsets.UTF_8);
            Integer hqKeyLength = hqKeyBytes.length;
            offset = offset + hqKeyLength;

            // profile name length
            byte[] profileNameLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, profileNameLengthBuf, 0, defaultOffset);
            Integer profileNameIntLength = byteArrayToInt(profileNameLengthBuf);
            offset = offset + defaultOffset;

            // profile name
            byte[] profileNameBuf = new byte[profileNameIntLength];
            System.arraycopy(payload, offset, profileNameBuf, 0, profileNameIntLength);
            String profileName = new String(profileNameBuf, Charset.forName(StandardCharsets.UTF_8.name()));
            byte[] profileNameBytes = profileName.getBytes(StandardCharsets.UTF_8);
            Integer profileNameLength = profileNameBytes.length;
            offset = offset + profileNameLength;

            // app icon upload status send message

            ProjectSettingImageMessage projectSettingImageStartMessage = new ProjectSettingImageMessage();
            projectSettingImageStartMessage.setStatus("APPICONUPLOAD");
            projectSettingImageStartMessage.setHqKey(hqkey);
            projectSettingMessage(session, projectSettingImageStartMessage);

            profile = new File(systemUserHomePath + profileSetDir + userID + "/" + profileName);

            // profile length
            byte[] profileIntBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, profileIntBuf, 0, defaultOffset);
            Integer profileIntLength = byteArrayToInt(profileIntBuf);
            offset = offset + defaultOffset;

            // profile
            byte[] profileBuf = new byte[profileIntLength];
            System.arraycopy(payload, offset, profileBuf, 0, profileIntLength);

            try {
                FileUtils.writeByteArrayToFile(profile, profileBuf);
                ProjectSettingImageMessage projectSettingImageMessage = new ProjectSettingImageMessage();
                projectSettingImageMessage.setStatus("APPICONUPLOADDONE");
                projectSettingImageMessage.setHqKey(hqkey);
                projectSettingMessage(session, projectSettingImageMessage);


            } catch (IOException e) {

                log.error("builder app icon binary error", e);
            }


        }else if(msgType.equals(ProjectServiceType.BIN_FILE_APP_ICON_UPDATE_SEND_INFO.name())){

            byte[] adminIdLengthBuf = new byte[defaultOffset];

            File profile = null;

            // branch id length
            Integer offset = 0;
            System.arraycopy(payload, offset, adminIdLengthBuf, 0, defaultOffset);
            Integer adminIdIntLength = byteArrayToInt(adminIdLengthBuf);
            offset = offset + defaultOffset;

            // branch id
            byte[] adminIdBuf = new byte[adminIdIntLength];
            System.arraycopy(payload, offset, adminIdBuf, 0, adminIdIntLength);
            String adminId = new String(adminIdBuf, Charset.forName(StandardCharsets.UTF_8.name()));
            byte[] adminIdBytes = adminId.getBytes(StandardCharsets.UTF_8);
            Integer adminLength = adminIdBytes.length;
            offset = offset + adminLength;

            // bin type length
            byte[] binTypeLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, binTypeLengthBuf, 0, defaultOffset);
            Integer binTypeIntLength = byteArrayToInt(binTypeLengthBuf);
            offset = offset + defaultOffset;

            // bin type
            byte[] binTypeBuf = new byte[binTypeIntLength];
            System.arraycopy(payload, offset, binTypeBuf, 0, binTypeIntLength);
            String binType = new String(binTypeBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] binTypeBytes = binType.getBytes(StandardCharsets.UTF_8);
            Integer binTypeLength = binTypeBytes.length;
            offset = offset + binTypeLength;

            // user id length
            byte[] userIDLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, userIDLengthBuf, 0, defaultOffset);
            Integer userIDIntLength = byteArrayToInt(userIDLengthBuf);
            offset = offset + defaultOffset;

            byte[] userIDBuf = new byte[userIDIntLength];
            System.arraycopy(payload, offset, userIDBuf, 0, userIDIntLength);
            String userID = new String(userIDBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] userIDBytes = userID.getBytes(StandardCharsets.UTF_8);
            Integer userIDLength = byteArrayToInt(userIDBytes);
            offset = offset + userIDLength;


            // platform length
            byte[] platformLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, platformLengthBuf, 0, defaultOffset);
            Integer platformIntLength = byteArrayToInt(platformLengthBuf);
            offset = offset + defaultOffset;

            // platform
            byte[] platformBuf = new byte[platformIntLength];
            System.arraycopy(payload, offset, platformBuf, 0, platformIntLength);
            String platform = new String(platformBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            // platform string length
            byte[] platformBytes = platform.getBytes(StandardCharsets.UTF_8);
            Integer platformLength = platformBytes.length;
            offset = offset + platformLength;

            // project path length
            byte[] projectPathLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, projectPathLengthBuf, 0, defaultOffset);
            Integer projectPathIntLength = byteArrayToInt(projectPathLengthBuf);
            offset = offset + defaultOffset;

            // project path
            byte[] projectPathBuf = new byte[projectPathIntLength];
            System.arraycopy(payload, offset, projectPathBuf, 0, projectPathIntLength);
            String projectPath = new String(projectPathBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            // project path string length
            byte[] projectPathBytes = projectPath.getBytes(StandardCharsets.UTF_8);
            Integer projectPathLength = projectPathBytes.length;
            offset = offset + projectPathLength;

            // profile name length
            byte[] profileNameLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, profileNameLengthBuf, 0, defaultOffset);
            Integer profileNameIntLength = byteArrayToInt(profileNameLengthBuf);
            offset = offset + defaultOffset;

            // profile name
            byte[] profileNameBuf = new byte[profileNameIntLength];
            System.arraycopy(payload, offset, profileNameBuf, 0, profileNameIntLength);
            String profileName = new String(profileNameBuf, Charset.forName(StandardCharsets.UTF_8.name()));
            byte[] profileNameBytes = profileName.getBytes(StandardCharsets.UTF_8);
            Integer profileNameLength = profileNameBytes.length;
            offset = offset + profileNameLength;

            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                // UserRootPath + projectPath + androidIconPath
                profile = new File(systemUserHomePath + profileSetDir + userID +"/"+ profileName);

            }else {
                // UserRootPath + projectPath + iOSIconPath
                profile = new File(systemUserHomePath + profileSetDir + userID +"/"+ profileName);

            }


            // profile length
            byte[] profileIntBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, profileIntBuf, 0, defaultOffset);
            Integer profileIntLength = byteArrayToInt(profileIntBuf);
            offset = offset + defaultOffset;

            // profile
            byte[] profileBuf = new byte[profileIntLength];
            System.arraycopy(payload, offset, profileBuf, 0, profileIntLength);

            try {
                FileUtils.writeByteArrayToFile(profile, profileBuf);

                // app icon zip file unzip ..
                if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                    zipUtils.decompressFormAndroidIcon(systemUserHomePath + profileSetDir+userID +"/"+profileName, systemUserHomePath + UserRootPath +"builder_main/"+ projectPath + androidIconPath);
                }else {
                    zipUtils.decompress(systemUserHomePath + profileSetDir+userID +"/"+profileName, systemUserHomePath + UserRootPath + "builder_main/"+  projectPath + iOSIconPath);
                }


            } catch (IOException e) {
                log.error("builder app icon binary error", e);
            } catch (Throwable throwable) {
                log.error("builder app icon binary error", throwable.getMessage());
            }

        }

    }

    public int byteArrayToInt(byte [] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8) + (b[3] & 0xFF);
    }

    private void projectSettingMessage(WebSocketSession session, ProjectSettingImageMessage projectSettingImageMessage){

        projectSettingImageMessage.setMsgType(ProjectServiceType.BIN_FILE_APP_ICON_APPEND_SEND_INFO.name());
        projectSettingImageMessage.setSessType(PayloadMsgType.HEADQUATER.name());

        // projectSettingImageMessage.setStatus("");

        Map<String, Object> parseResult = Mapper.convertValue(projectSettingImageMessage, Map.class);
        // session 객체 추가
        headQuaterClientHandler.sendMessage(session, parseResult);

    }


}
