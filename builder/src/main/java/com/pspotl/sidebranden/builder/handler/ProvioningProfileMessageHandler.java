package com.pspotl.sidebranden.builder.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.builder.domain.ProjectGitCloneMessage;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.ProjectServiceType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.ProvisioningProfileService;
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
public class ProvioningProfileMessageHandler implements BranchBinaryHandle{

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    ProvisioningProfileService provisioningProfileService;

    // @Value("${whive.distribution.profilePath}")
    private String dir;

    private final Integer defaultOffset = 4;
    private ObjectMapper Mapper = new ObjectMapper();
    ProjectGitCloneMessage projectGitCloneMessage = new ProjectGitCloneMessage();
    WebSocketSession sessionTemp;

    @Override
    public void handleBinary(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();

        // JSONObject resultObj = (JSONObject) parseResult.get("jsonObj");
        if(messageType.equals(ProjectServiceType.HV_BIN_MSG_PROJECT_PROVIONING_PROFILE_UPLOAD_INFO.name())){

            //ByteBuffer payload = (ByteBuffer) parseResult.get("file");
            byte[] payload = (byte[]) parseResult.get("file");

            // provisioningProfileService.tempToProvisioningProfileUpload(payload);

        }

    }

    @Override
    public void handleBinaryPayLoad(WebSocketSession session, String msgType, byte[] payload) {

        if(msgType.equals(BuildServiceType.BIN_FILE_SIGNINGKEY_SEND_INFO.name())){

            sessionTemp = session;
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

            profile = new File(dir + profileName);

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
                projectCreateMessage(projectGitCloneMessage,"","DONE");

            } catch (IOException e) {
                    
                log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }

        }

    }

    public int byteArrayToInt(byte [] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8) + (b[3] & 0xFF);
    }

    private void projectCreateMessage(ProjectGitCloneMessage projectGitCloneMessage, String logMessage, String gitStatus){

        projectGitCloneMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO.name());
        projectGitCloneMessage.setSessType(PayloadMsgType.HEADQUATER.name());

        if (gitStatus.equals("GITCLONE")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        } else if(gitStatus.equals("SVNCHECKOUT")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        } else if(gitStatus.equals("DONE")){
            projectGitCloneMessage.setGitStatus(gitStatus);
        }

        projectGitCloneMessage.setLogMessage(logMessage);

        Map<String, Object> parseResult = Mapper.convertValue(projectGitCloneMessage, Map.class);
        headQuaterClientHandler.sendMessage(sessionTemp, parseResult);
    }

}
