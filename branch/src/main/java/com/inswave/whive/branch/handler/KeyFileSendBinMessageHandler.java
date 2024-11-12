package com.inswave.whive.branch.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.SigningKeyCreateMessage;
import com.inswave.whive.branch.domain.iOSAllKeyCreateMsg;
import com.inswave.whive.branch.enums.BuilderDirectoryType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.enums.ProjectServiceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

@Slf4j
@Component
public class KeyFileSendBinMessageHandler implements BranchBinaryHandle{

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    WebSocketSession sessionTemp = null;

    private final Integer defaultOffset = 4;

    @Value("${whive.distribution.signingkeyPath}")
    private String dir;
    private String systemUserHomePath = System.getProperty("user.home");

    private ObjectMapper Mapper = new ObjectMapper();

    @Override
    public void handleBinary(WebSocketSession session, Map<String, Object> parseResult) {

    }

    @Override
    public void handleBinaryPayLoad(WebSocketSession session, String msgType, byte[] payload) {

        SigningKeyCreateMessage signingKeyCreateMessage = new SigningKeyCreateMessage();

        sessionTemp = session;

        // msg type 분기처리해서 android, ios 분기처리하기
        // android key store file
        if(msgType.equals(ProjectServiceType.BIN_FILE_PROFILE_TEMPLATE_SEND_INFO.name())){

            byte[] adminIdLengthBuf = new byte[defaultOffset];

            File keyfile = null;
            File keyDir = null;
            File deployfile = null;
            File deployDir = null;
            JSONObject filePathObj = new JSONObject();

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

            // hqkey length
            byte[] hqKeyLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, hqKeyLengthBuf, 0, defaultOffset);
            Integer hqKeyIntLength = byteArrayToInt(hqKeyLengthBuf);
            offset = offset + defaultOffset;

            //hqKey
            byte[] hqKeyeBuf = new byte[hqKeyIntLength];
            System.arraycopy(payload, offset, hqKeyeBuf, 0, hqKeyIntLength);
            String hqKey = new String(hqKeyeBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] hqKeyBytes = hqKey.getBytes(StandardCharsets.UTF_8);
            Integer hqKeytLength = hqKeyBytes.length;
            offset = offset + hqKeytLength;

            // domain_id
            byte[] domainIDLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, domainIDLengthBuf, 0, defaultOffset);
            Integer domainIDIntLength = byteArrayToInt(domainIDLengthBuf);
            offset = offset + defaultOffset;

            byte[] domainIDBuf = new byte[domainIDIntLength];
            System.arraycopy(payload, offset, domainIDBuf, 0, domainIDIntLength);
            String domainID = new String(domainIDBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] domainIDBytes =  domainID.getBytes(StandardCharsets.UTF_8);
            Integer domainIDLength = domainIDBytes.length;
            offset = offset + domainIDLength;

            // admin_id
            byte[] adminIDLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, adminIDLengthBuf, 0, defaultOffset);
            Integer adminIDIntLength = byteArrayToInt(adminIDLengthBuf);
            offset = offset + defaultOffset;

            byte[] adminIDBuf = new byte[adminIDIntLength];
            System.arraycopy(payload, offset, adminIDBuf, 0, adminIDIntLength);
            String adminID = new String(adminIDBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] adminIDBytes = adminID.getBytes(StandardCharsets.UTF_8);
            Integer adminIDLength = adminIDBytes.length;
            offset = offset + adminIDLength;

            //platform
            byte[] platformLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, platformLengthBuf, 0, defaultOffset);
            Integer platformIntLength = byteArrayToInt(platformLengthBuf);
            offset = offset + defaultOffset;

            byte[] platformBuf = new byte[platformIntLength];
            System.arraycopy(payload, offset, platformBuf, 0, platformIntLength);
            String platform = new String(platformBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] platformLBytes = platform.getBytes(StandardCharsets.UTF_8);
            Integer platformLength = platformLBytes.length;
            offset = offset + platformLength;

            // signingkey_type
            byte[] signingkeyTypeLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, signingkeyTypeLengthBuf, 0, defaultOffset);
            Integer signingkeyTypeIntLength = byteArrayToInt(signingkeyTypeLengthBuf);
            offset = offset + defaultOffset;

            byte[] signingkeyBuf = new byte[signingkeyTypeIntLength];
            System.arraycopy(payload, offset, signingkeyBuf, 0, signingkeyTypeIntLength);
            String signingkeyType = new String(signingkeyBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] signingkeyTypeBytes = signingkeyType.getBytes(StandardCharsets.UTF_8);
            Integer signingkeyTypeLength = signingkeyTypeBytes.length;
            offset = offset + signingkeyTypeLength;

            //signingkey_id
            byte[] signingkeyIDLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, signingkeyIDLengthBuf, 0, defaultOffset);
            Integer signingkeyIDIntLength = byteArrayToInt(signingkeyIDLengthBuf);
            offset = offset + defaultOffset;

            byte[] signingkeyIDBuf = new byte[signingkeyIDIntLength];
            System.arraycopy(payload, offset, signingkeyIDBuf, 0, signingkeyIDIntLength);
            String signingkeyID = new String(signingkeyIDBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] signingkeyIDBytes = signingkeyID.getBytes(StandardCharsets.UTF_8);
            Integer signingkeyIDLength = signingkeyIDBytes.length;
            offset = offset + signingkeyIDLength;

            // keystore file name length
            byte[] keystoreNameLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, keystoreNameLengthBuf, 0, defaultOffset);
            Integer keystoreNameIntLength = byteArrayToInt(keystoreNameLengthBuf);
            offset = offset + defaultOffset;

            // keystore file name
            byte[] keystoreNameBuf = new byte[keystoreNameIntLength];
            System.arraycopy(payload, offset, keystoreNameBuf, 0, keystoreNameIntLength);
            String keystoreName = new String(keystoreNameBuf, Charset.forName(StandardCharsets.UTF_8.name()));
            byte[] keystoreNameBytes = keystoreName.getBytes(StandardCharsets.UTF_8);
            Integer keystoreNameLength = keystoreNameBytes.length;
            offset = offset + keystoreNameLength;

            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                keyfile = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID +"/"+ keystoreName);
                keyDir = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID);
                filePathObj.put("keyfilePath",systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID +"/"+ keystoreName);
            }


            if ( keyDir != null && !keyDir.exists()) {
                try{
                    keyDir.mkdir(); //폴더 생성합니다.
                    //System.out.println("폴더가 생성되었습니다.");
                }
                catch(Exception e){
                    e.getStackTrace();
                }
            }else {
                // System.out.println("이미 폴더가 생성되어 있습니다.");
            }

            // keystore file length
            byte[] keystoreIntBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, keystoreIntBuf, 0, defaultOffset);
            Integer keystoreIntLength = byteArrayToInt(keystoreIntBuf);
            offset = offset + defaultOffset;

            // keystore file
            byte[] keystoreBuf = new byte[keystoreIntLength];
            System.arraycopy(payload, offset, keystoreBuf, 0, keystoreIntLength);

            try {
                FileUtils.writeByteArrayToFile(keyfile, keystoreBuf);
                // 추가 작업 진행해야함..
                Integer keyfileLength = Files.readAllBytes(keyfile.toPath()).length;
                offset = offset + keyfileLength;

            } catch (IOException e) {

                log.error("builder key file send binary message error",e);
            }

            // deploy yn 변수값 바이너리  값으로 세팅하기 3
            // 해당 조건에 따라서 deploy name, deploy file 값을 처리하지 않는 방법으로 구현하기
            byte[] deployYnLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, deployYnLengthBuf, 0, defaultOffset);
            Integer deployYnIntLength = byteArrayToInt(deployYnLengthBuf);
            offset = offset + defaultOffset;

            byte[] deployYnBuf = new byte[deployYnIntLength];
            System.arraycopy(payload, offset, deployYnBuf, 0, deployYnIntLength);
            String deployYn = new String(deployYnBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] deployYnBytes = deployYn.getBytes(StandardCharsets.UTF_8);
            Integer  deployYnLength = deployYnBytes.length;
            offset = offset + deployYnLength;

            // deploy 바이너리 데이터 확인 여부
            if(deployYn.equals("Y")){
                // debug profile name length
                byte[] deployNameLengthBuf = new byte[defaultOffset];
                System.arraycopy(payload, offset, deployNameLengthBuf, 0, defaultOffset);
                Integer deployNameIntLength = byteArrayToInt(deployNameLengthBuf);
                offset = offset + defaultOffset;

                // keyfile name
                byte[] deployNameBuf = new byte[deployNameIntLength];
                System.arraycopy(payload, offset, deployNameBuf, 0, deployNameIntLength);
                String deployName = new String(deployNameBuf, Charset.forName(StandardCharsets.UTF_8.name()));
                byte[] deployNameBytes = deployName.getBytes(StandardCharsets.UTF_8);
                Integer deployNameLength = deployNameBytes.length;
                offset = offset + deployNameLength;

                deployfile = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ +domainID +"/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_ + signingkeyID +"/" + deployName);
                deployDir = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ +domainID +"/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_ + signingkeyID);
                filePathObj.put("deployfilePath",systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID +"/"+ deployName);

                if ( deployDir != null && !deployDir.exists()) {
                    try{
                        deployDir.mkdir(); //폴더 생성합니다.
                        //System.out.println("deployfile 폴더가 생성되었습니다.");
                    }
                    catch(Exception e){
                        e.getStackTrace();
                    }
                }else {
                    // System.out.println("이미 폴더가 생성되어 있습니다.");
                }

                // keyfile length
                byte[] deployfileIntBuf = new byte[defaultOffset];
                System.arraycopy(payload, offset, deployfileIntBuf, 0, defaultOffset);
                Integer deployfileIntLength = byteArrayToInt(deployfileIntBuf);
                offset = offset + defaultOffset;

                // keyfile
                byte[] debufProfileBuf = new byte[deployfileIntLength];
                System.arraycopy(payload, offset, debufProfileBuf, 0, deployfileIntLength);

                try {
                    FileUtils.writeByteArrayToFile(deployfile, debufProfileBuf);

                } catch (IOException e) {

                    log.error("builder key file send binary message error",e);
                }

                if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                    signingKeyCreateMessage.setBuilderID(adminId);
                    keyFileCreateMessage(session, signingKeyCreateMessage, filePathObj, hqKey, signingkeyID, adminID);
                }

            }else {
                filePathObj.put("deployfilePath","");
                if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                    signingKeyCreateMessage.setBuilderID(adminId);
                    keyFileCreateMessage(session, signingKeyCreateMessage, filePathObj, hqKey, signingkeyID, adminID);
                }
            }



        }else if(msgType.equals(ProjectServiceType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_SEND_INFO.name())){

            iOSAllKeyCreateMsg iOSAllKeyCreateMsg = new iOSAllKeyCreateMsg();
            JSONObject allKeyFileObj = new JSONObject();

            byte[] adminIdLengthBuf = new byte[defaultOffset];

            File keyfile = null;
            File keyDir = null;
            File debugProfile = null;
            File debugProfileDir = null;
            File releaseProfile = null;
            File releaseProfileDir = null;

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

            // hqkey length
            byte[] hqKeyLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, hqKeyLengthBuf, 0, defaultOffset);
            Integer hqKeyIntLength = byteArrayToInt(hqKeyLengthBuf);
            offset = offset + defaultOffset;

            //hqKey
            byte[] hqKeyeBuf = new byte[hqKeyIntLength];
            System.arraycopy(payload, offset, hqKeyeBuf, 0, hqKeyIntLength);
            String hqKey = new String(hqKeyeBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] hqKeyBytes = hqKey.getBytes(StandardCharsets.UTF_8);
            Integer hqKeytLength = hqKeyBytes.length;
            offset = offset + hqKeytLength;

            // domain_id
            byte[] domainIDLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, domainIDLengthBuf, 0, defaultOffset);
            Integer domainIDIntLength = byteArrayToInt(domainIDLengthBuf);
            offset = offset + defaultOffset;

            byte[] domainIDBuf = new byte[domainIDIntLength];
            System.arraycopy(payload, offset, domainIDBuf, 0, domainIDIntLength);
            String domainID = new String(domainIDBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] domainIDBytes =  domainID.getBytes(StandardCharsets.UTF_8);
            Integer domainIDLength = domainIDBytes.length;
            offset = offset + domainIDLength;

            // admin_id
            byte[] adminIDLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, adminIDLengthBuf, 0, defaultOffset);
            Integer adminIDIntLength = byteArrayToInt(adminIDLengthBuf);
            offset = offset + defaultOffset;

            byte[] adminIDBuf = new byte[adminIDIntLength];
            System.arraycopy(payload, offset, adminIDBuf, 0, adminIDIntLength);
            String adminID = new String(adminIDBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] adminIDBytes = adminID.getBytes(StandardCharsets.UTF_8);
            Integer adminIDLength = adminIDBytes.length;
            offset = offset + adminIDLength;

            //platform
            byte[] platformLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, platformLengthBuf, 0, defaultOffset);
            Integer platformIntLength = byteArrayToInt(platformLengthBuf);
            offset = offset + defaultOffset;

            byte[] platformBuf = new byte[platformIntLength];
            System.arraycopy(payload, offset, platformBuf, 0, platformIntLength);
            String platform = new String(platformBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] platformLBytes = platform.getBytes(StandardCharsets.UTF_8);
            Integer platformLength = platformLBytes.length;
            offset = offset + platformLength;

            // signingkey_type
            byte[] signingkeyTypeLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, signingkeyTypeLengthBuf, 0, defaultOffset);
            Integer signingkeyTypeIntLength = byteArrayToInt(signingkeyTypeLengthBuf);
            offset = offset + defaultOffset;

            byte[] signingkeyBuf = new byte[signingkeyTypeIntLength];
            System.arraycopy(payload, offset, signingkeyBuf, 0, signingkeyTypeIntLength);
            String signingkeyType = new String(signingkeyBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] signingkeyTypeBytes = signingkeyType.getBytes(StandardCharsets.UTF_8);
            Integer signingkeyTypeLength = signingkeyTypeBytes.length;
            offset = offset + signingkeyTypeLength;

            //signingkey_id
            byte[] signingkeyIDLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, signingkeyIDLengthBuf, 0, defaultOffset);
            Integer signingkeyIDIntLength = byteArrayToInt(signingkeyIDLengthBuf);
            offset = offset + defaultOffset;

            byte[] signingkeyIDBuf = new byte[signingkeyIDIntLength];
            System.arraycopy(payload, offset, signingkeyIDBuf, 0, signingkeyIDIntLength);
            String signingkeyID = new String(signingkeyIDBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] signingkeyIDBytes = signingkeyID.getBytes(StandardCharsets.UTF_8);
            Integer signingkeyIDLength = signingkeyIDBytes.length;
            offset = offset + signingkeyIDLength;

            // keyfile name length
            byte[] keyfileNameLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, keyfileNameLengthBuf, 0, defaultOffset);
            Integer keyfileNameIntLength = byteArrayToInt(keyfileNameLengthBuf);
            offset = offset + defaultOffset;

            // keyfile name
            byte[] keyfileNameBuf = new byte[keyfileNameIntLength];
            System.arraycopy(payload, offset, keyfileNameBuf, 0, keyfileNameIntLength);
            String keyfileName = new String(keyfileNameBuf, Charset.forName(StandardCharsets.UTF_8.name()));
            byte[] keyfileNameBytes = keyfileName.getBytes(StandardCharsets.UTF_8);
            Integer keyfileNameLength = keyfileNameBytes.length;
            offset = offset + keyfileNameLength;

            keyfile = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ +domainID +"/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_ + signingkeyID +"/"+ BuilderDirectoryType.SIGNINGKEYFILE + "/" + keyfileName);
            keyDir = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ +domainID +"/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_ + signingkeyID +"/"+ BuilderDirectoryType.SIGNINGKEYFILE);

            if (!keyDir.exists()) {
                try{
                    keyDir.mkdir(); //폴더 생성합니다.
                    System.out.println(" keyfile 폴더가 생성되었습니다.");
                }
                catch(Exception e){
                    e.getStackTrace();
                }
            }else {
                // System.out.println("이미 폴더가 생성되어 있습니다.");
            }

            // keyfile length
            byte[] keyfileIntBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, keyfileIntBuf, 0, defaultOffset);
            Integer keyfileIntLength = byteArrayToInt(keyfileIntBuf);
            offset = offset + defaultOffset;

            // keyfile
            byte[] keyfileBuf = new byte[keyfileIntLength];
            System.arraycopy(payload, offset, keyfileBuf, 0, keyfileIntLength);


            try {
                FileUtils.writeByteArrayToFile(keyfile, keyfileBuf);
                Integer keyfileLength = Files.readAllBytes(keyfile.toPath()).length;
                offset = offset + keyfileLength;

            } catch (IOException e) {

                log.error("builder key file send binary message error",e);
            }

            // debug profile

            // debug profile name length
            byte[] debugProfileNameLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, debugProfileNameLengthBuf, 0, defaultOffset);
            Integer debugProfileNameIntLength = byteArrayToInt(debugProfileNameLengthBuf);
            offset = offset + defaultOffset;

            // keyfile name
            byte[] debugProfileNameBuf = new byte[debugProfileNameIntLength];
            System.arraycopy(payload, offset, debugProfileNameBuf, 0, debugProfileNameIntLength);
            String debugProfileName = new String(debugProfileNameBuf, Charset.forName(StandardCharsets.UTF_8.name()));
            byte[] debugProfileNameBytes = debugProfileName.getBytes(StandardCharsets.UTF_8);
            Integer debugProfileNameLength = debugProfileNameBytes.length;
            offset = offset + debugProfileNameLength;

            debugProfile = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ +domainID +"/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_ + signingkeyID +"/"+ BuilderDirectoryType.DEBUG_PROFILE + "/" + debugProfileName);
            debugProfileDir= new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ +domainID +"/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_ + signingkeyID +"/"+ BuilderDirectoryType.DEBUG_PROFILE);

            if (!debugProfileDir.exists()) {
                try{
                    debugProfileDir.mkdir(); //폴더 생성합니다.
                    //System.out.println("debugProfile 폴더가 생성되었습니다.");
                }
                catch(Exception e){
                    e.getStackTrace();
                }
            }else {
                // System.out.println("이미 폴더가 생성되어 있습니다.");
            }

            // keyfile length
            byte[] debugProfileIntBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, debugProfileIntBuf, 0, defaultOffset);
            Integer debugProfileIntLength = byteArrayToInt(debugProfileIntBuf);
            offset = offset + defaultOffset;

            // keyfile
            byte[] debufProfileBuf = new byte[debugProfileIntLength];
            System.arraycopy(payload, offset, debufProfileBuf, 0, debugProfileIntLength);

            try {
                FileUtils.writeByteArrayToFile(debugProfile, debufProfileBuf);
                Integer debufProfileLength = Files.readAllBytes(debugProfile.toPath()).length;
                offset = offset + debufProfileLength;

            } catch (IOException e) {

                log.error("builder key file send binary message error",e);
            }

            // release profile

            // debug profile name length
            byte[] releaseProfileNameLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, releaseProfileNameLengthBuf, 0, defaultOffset);
            Integer releaseProfileNameIntLength = byteArrayToInt(releaseProfileNameLengthBuf);
            offset = offset + defaultOffset;

            // keyfile name
            byte[] releaseProfileNameBuf = new byte[releaseProfileNameIntLength];
            System.arraycopy(payload, offset, releaseProfileNameBuf, 0, releaseProfileNameIntLength);
            String releaseProfileName = new String(releaseProfileNameBuf, Charset.forName(StandardCharsets.UTF_8.name()));
            byte[] releaseProfileNameBytes = releaseProfileName.getBytes(StandardCharsets.UTF_8);
            Integer releaseProfileNameLength = releaseProfileNameBytes.length;
            offset = offset + releaseProfileNameLength;

            releaseProfile = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ +domainID +"/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_ + signingkeyID +"/"+ BuilderDirectoryType.RELEASE_PROFILE + "/" + releaseProfileName);
            releaseProfileDir = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ +domainID +"/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_ + signingkeyID +"/"+ BuilderDirectoryType.RELEASE_PROFILE);

            if (!releaseProfileDir.exists()) {
                try{
                    releaseProfileDir.mkdir(); //폴더 생성합니다.
                    //System.out.println("releaseProfile 폴더가 생성되었습니다.");
                }
                catch(Exception e){
                    e.getStackTrace();
                }
            }else {
                // System.out.println("이미 폴더가 생성되어 있습니다.");
            }

            // keyfile length
            byte[] releaseProfileIntBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, releaseProfileIntBuf, 0, defaultOffset);
            Integer releaseProfileIntLength = byteArrayToInt(releaseProfileIntBuf);
            offset = offset + defaultOffset;

            // release profile
            byte[] releaseProfileBuf = new byte[releaseProfileIntLength];
            System.arraycopy(payload, offset, releaseProfileBuf, 0, releaseProfileIntLength);



            try {
                FileUtils.writeByteArrayToFile(releaseProfile, releaseProfileBuf);

                if (platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                    allKeyFileObj.put("keyFilePath",systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ +domainID +"/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_ + signingkeyID +"/"+ BuilderDirectoryType.SIGNINGKEYFILE + "/" + keyfileName);
                    allKeyFileObj.put("debugProFilePath",systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ +domainID +"/"  + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_ + signingkeyID +"/"+ BuilderDirectoryType.DEBUG_PROFILE + "/" + debugProfileName);
                    allKeyFileObj.put("releaseProFilePath",systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ +domainID +"/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_ + signingkeyID +"/"+ BuilderDirectoryType.RELEASE_PROFILE + "/" + releaseProfileName);

                    iOSkeyFileCreateMessage(iOSAllKeyCreateMsg, allKeyFileObj, hqKey, signingkeyID, adminID);
                }

            } catch (IOException e) {

                log.error("builder key file send binary message error",e);
            }

        } else if (msgType.equals(ProjectServiceType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_DEPLOY_SEND_INFO.name())){
            iOSAllKeyCreateMsg iOSAllKeyCreateMsg = new iOSAllKeyCreateMsg();
            JSONObject allKeyFileObj = new JSONObject();

            byte[] adminIdLengthBuf = new byte[defaultOffset];

            File keyfile = null;

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

            // hqkey length
            byte[] hqKeyLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, hqKeyLengthBuf, 0, defaultOffset);
            Integer hqKeyIntLength = byteArrayToInt(hqKeyLengthBuf);
            offset = offset + defaultOffset;

            //hqKey
            byte[] hqKeyeBuf = new byte[hqKeyIntLength];
            System.arraycopy(payload, offset, hqKeyeBuf, 0, hqKeyIntLength);
            String hqKey = new String(hqKeyeBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] hqKeyBytes = hqKey.getBytes(StandardCharsets.UTF_8);
            Integer hqKeytLength = hqKeyBytes.length;
            offset = offset + hqKeytLength;

            // domain_id
            byte[] domainIDLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, domainIDLengthBuf, 0, defaultOffset);
            Integer domainIDIntLength = byteArrayToInt(domainIDLengthBuf);
            offset = offset + defaultOffset;

            byte[] domainIDBuf = new byte[domainIDIntLength];
            System.arraycopy(payload, offset, domainIDBuf, 0, domainIDIntLength);
            String domainID = new String(domainIDBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] domainIDBytes =  domainID.getBytes(StandardCharsets.UTF_8);
            Integer domainIDLength = domainIDBytes.length;
            offset = offset + domainIDLength;

            // admin_id
            byte[] adminIDLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, adminIDLengthBuf, 0, defaultOffset);
            Integer adminIDIntLength = byteArrayToInt(adminIDLengthBuf);
            offset = offset + defaultOffset;

            byte[] adminIDBuf = new byte[adminIDIntLength];
            System.arraycopy(payload, offset, adminIDBuf, 0, adminIDIntLength);
            String adminID = new String(adminIDBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] adminIDBytes = adminID.getBytes(StandardCharsets.UTF_8);
            Integer adminIDLength = adminIDBytes.length;
            offset = offset + adminIDLength;

            //platform
            byte[] platformLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, platformLengthBuf, 0, defaultOffset);
            Integer platformIntLength = byteArrayToInt(platformLengthBuf);
            offset = offset + defaultOffset;

            byte[] platformBuf = new byte[platformIntLength];
            System.arraycopy(payload, offset, platformBuf, 0, platformIntLength);
            String platform = new String(platformBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] platformLBytes = platform.getBytes(StandardCharsets.UTF_8);
            Integer platformLength = platformLBytes.length;
            offset = offset + platformLength;

            // signingkey_type
            byte[] signingkeyTypeLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, signingkeyTypeLengthBuf, 0, defaultOffset);
            Integer signingkeyTypeIntLength = byteArrayToInt(signingkeyTypeLengthBuf);
            offset = offset + defaultOffset;

            byte[] signingkeyBuf = new byte[signingkeyTypeIntLength];
            System.arraycopy(payload, offset, signingkeyBuf, 0, signingkeyTypeIntLength);
            String signingkeyType = new String(signingkeyBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] signingkeyTypeBytes = signingkeyType.getBytes(StandardCharsets.UTF_8);
            Integer signingkeyTypeLength = signingkeyTypeBytes.length;
            offset = offset + signingkeyTypeLength;

            //signingkey_id
            byte[] signingkeyIDLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, signingkeyIDLengthBuf, 0, defaultOffset);
            Integer signingkeyIDIntLength = byteArrayToInt(signingkeyIDLengthBuf);
            offset = offset + defaultOffset;

            byte[] signingkeyIDBuf = new byte[signingkeyIDIntLength];
            System.arraycopy(payload, offset, signingkeyIDBuf, 0, signingkeyIDIntLength);
            String signingkeyID = new String(signingkeyIDBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            byte[] signingkeyIDBytes = signingkeyID.getBytes(StandardCharsets.UTF_8);
            Integer signingkeyIDLength = signingkeyIDBytes.length;
            offset = offset + signingkeyIDLength;

            // keyfile name length
            byte[] keyfileNameLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, keyfileNameLengthBuf, 0, defaultOffset);
            Integer keyfileNameIntLength = byteArrayToInt(keyfileNameLengthBuf);
            offset = offset + defaultOffset;

            // keyfile name
            byte[] keyfileNameBuf = new byte[keyfileNameIntLength];
            System.arraycopy(payload, offset, keyfileNameBuf, 0, keyfileNameIntLength);
            String keyfileName = new String(keyfileNameBuf, Charset.forName(StandardCharsets.UTF_8.name()));
            byte[] keyfileNameBytes = keyfileName.getBytes(StandardCharsets.UTF_8);
            Integer keyfileNameLength = keyfileNameBytes.length;
            offset = offset + keyfileNameLength;

            keyfile = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ +domainID +"/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_ + signingkeyID +"/"+ BuilderDirectoryType.SIGNINGKEYFILE + "/" + keyfileName);

            if (!keyfile.exists()) {
                try{
                    keyfile.mkdir(); //폴더 생성합니다.
                    System.out.println(" keyfile 폴더가 생성되었습니다.");
                }
                catch(Exception e){
                    e.getStackTrace();
                }
            }else {
                // System.out.println("이미 폴더가 생성되어 있습니다.");
            }

            // keyfile length
            byte[] keyfileIntBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, keyfileIntBuf, 0, defaultOffset);
            Integer keyfileIntLength = byteArrayToInt(keyfileIntBuf);
            offset = offset + defaultOffset;

            // keyfile
            byte[] keyfileBuf = new byte[keyfileIntLength];
            System.arraycopy(payload, offset, keyfileBuf, 0, keyfileIntLength);


            try {
                FileUtils.writeByteArrayToFile(keyfile, keyfileBuf);
                Integer keyfileLength = Files.readAllBytes(keyfile.toPath()).length;
                offset = offset + keyfileLength;

            } catch (IOException e) {

                log.error("builder key file send binary message error",e);
            }


            if (platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                allKeyFileObj.put("keyFilePath",systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ +domainID +"/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_ + signingkeyID +"/"+ BuilderDirectoryType.SIGNINGKEYFILE + "/" + keyfileName);

                iOSDeploykeyFileCreateMessage(iOSAllKeyCreateMsg, allKeyFileObj, hqKey, signingkeyID);
            }

        }

    }

    public int byteArrayToInt(byte [] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8) + (b[3] & 0xFF);
    }

    private void keyFileCreateMessage(WebSocketSession session, SigningKeyCreateMessage signingKeyCreateMessage, JSONObject filePath, String hqKey, String signingkeyID, String adminID){

        signingKeyCreateMessage.setMsgType(ProjectServiceType.BIN_FILE_PROFILE_TEMPLATE_SEND_INFO.name());
        signingKeyCreateMessage.setSessType(PayloadMsgType.HEADQUATER.name());

        signingKeyCreateMessage.setKeyfilePath(filePath.get("keyfilePath").toString());
        signingKeyCreateMessage.setDeployfilePath(filePath.get("deployfilePath").toString());
        signingKeyCreateMessage.setHqKey(hqKey);
        signingKeyCreateMessage.setSigningkeyID(signingkeyID);
        signingKeyCreateMessage.setBuilderUserID(adminID);

        Map<String, Object> parseResult = Mapper.convertValue(signingKeyCreateMessage, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

    // ios 전용으로 send message 메소드 추가해야함.
    private void iOSkeyFileCreateMessage(iOSAllKeyCreateMsg iOSAllKeyCreateMsg, JSONObject fileObj, String hqKey, String signingkeyID, String adminID){

        iOSAllKeyCreateMsg.setMsgType(ProjectServiceType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_SEND_INFO.name());
        iOSAllKeyCreateMsg.setSessType(PayloadMsgType.HEADQUATER.name());

        iOSAllKeyCreateMsg.setKeyfilePath(fileObj.get("keyFilePath").toString());
        iOSAllKeyCreateMsg.setDebugProfilePath(fileObj.get("debugProFilePath").toString());
        iOSAllKeyCreateMsg.setReleaseProfilePath(fileObj.get("releaseProFilePath").toString());
        iOSAllKeyCreateMsg.setHqKey(hqKey);
        iOSAllKeyCreateMsg.setSigningkeyID(signingkeyID);
        iOSAllKeyCreateMsg.setBuilderUserID(adminID);

        Map<String, Object> parseResult = Mapper.convertValue(iOSAllKeyCreateMsg, Map.class);
        headQuaterClientHandler.sendMessage(sessionTemp, parseResult);
    }

    // ios 전용으로 send message 메소드 추가해야함.
    private void iOSDeploykeyFileCreateMessage(iOSAllKeyCreateMsg iOSAllKeyCreateMsg, JSONObject fileObj, String hqKey, String signingkeyID){

        iOSAllKeyCreateMsg.setMsgType(ProjectServiceType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_DEPLOY_SEND_INFO.name());
        iOSAllKeyCreateMsg.setSessType(PayloadMsgType.HEADQUATER.name());

        iOSAllKeyCreateMsg.setKeyfilePath(fileObj.get("keyFilePath").toString());
        iOSAllKeyCreateMsg.setHqKey(hqKey);
        iOSAllKeyCreateMsg.setSigningkeyID(signingkeyID);

        Map<String, Object> parseResult = Mapper.convertValue(iOSAllKeyCreateMsg, Map.class);
        headQuaterClientHandler.sendMessage(sessionTemp, parseResult);
    }
}
