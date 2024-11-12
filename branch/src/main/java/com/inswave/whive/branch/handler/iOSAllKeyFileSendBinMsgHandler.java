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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Component
public class iOSAllKeyFileSendBinMsgHandler implements BranchBinaryHandle{

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

        iOSAllKeyCreateMsg iOSAllKeyCreateMsg = new iOSAllKeyCreateMsg();

        sessionTemp = session;

        // msg type 분기처리해서 android, ios 분기처리하기
        if(msgType.equals(ProjectServiceType.BIN_FILE_IOS_ALL_KEY_FILE_SEND_INFO.name())){

            byte[] adminIdLengthBuf = new byte[defaultOffset];
            List<Object> certificateArrayListObj = new ArrayList<>();
            List<Object> profilesArrayJsonObj = new ArrayList<>();
            String deployFilePath = "";

            File deployFile = null;

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

            // profile cnt
            byte[] profileCntLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, profileCntLengthBuf, 0, defaultOffset);
            Integer profileCntIntLength = byteArrayToInt(profileCntLengthBuf);
            offset = offset + defaultOffset;

            byte[] profileCntBuf = new byte[profileCntIntLength];
            System.arraycopy(payload, offset, profileCntBuf, 0, domainIDIntLength);
            Integer profileCnt = new Integer(new String(profileCntBuf, Charset.forName(StandardCharsets.UTF_8.name())));

            byte[] profileCntBytes =  profileCnt.toString().getBytes(StandardCharsets.UTF_8);
            Integer profileCntLength = profileCntBytes.length;
            offset = offset + profileCntLength;

            // certificate cnt
            byte[] certificateCntLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, certificateCntLengthBuf, 0, defaultOffset);
            Integer certificateCntIntLength = byteArrayToInt(certificateCntLengthBuf);
            offset = offset + defaultOffset;

            byte[] certificateCntBuf = new byte[certificateCntIntLength];
            System.arraycopy(payload, offset, certificateCntBuf, 0, domainIDIntLength);
            Integer certificateCnt = new Integer(new String(certificateCntBuf, Charset.forName(StandardCharsets.UTF_8.name())));

            byte[] certificateCntBytes =  certificateCnt.toString().getBytes(StandardCharsets.UTF_8);
            Integer certificateCntLength = certificateCntBytes.length;
            offset = offset + certificateCntLength;

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
                 try {
                    // deploy name
                    byte[] deployNameLengthBuf = new byte[defaultOffset];
                    System.arraycopy(payload, offset, deployNameLengthBuf, 0, defaultOffset);
                    Integer deployNameIntLength = byteArrayToInt(deployNameLengthBuf);
                    offset = offset + defaultOffset;

                    // deploy name
                    byte[] deployNameBuf = new byte[deployNameIntLength];
                    System.arraycopy(payload, offset, deployNameBuf, 0, deployNameIntLength);
                    String deployName = new String(deployNameBuf, Charset.forName(StandardCharsets.UTF_8.name()));
                    byte[] deployNameBytes = deployName.getBytes(StandardCharsets.UTF_8);
                    Integer deployNameLength = deployNameBytes.length;
                    offset = offset + deployNameLength;

                    if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                        deployFile = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID + "/"+ deployName);
                    }


                    if ( deployFile != null && !deployFile.exists()) {
                        try{
                            deployFile.mkdir(); //폴더 생성합니다.
                            //System.out.println("폴더가 생성되었습니다.");
                        }
                        catch(Exception e){
                            e.getStackTrace();
                        }
                    }else {
                        // System.out.println("이미 폴더가 생성되어 있습니다.");
                    }

                    // deployKeyfile
                    byte[] deployKeyfileIntBuf = new byte[defaultOffset];
                    System.arraycopy(payload, offset, deployKeyfileIntBuf, 0, defaultOffset);
                    Integer deployKeyfileIntLength = byteArrayToInt(deployKeyfileIntBuf);
                    offset = offset + defaultOffset;

                    // keyfile
                    byte[] deployKeyfileBuf = new byte[deployKeyfileIntLength];
                    System.arraycopy(payload, offset, deployKeyfileBuf, 0, deployKeyfileIntLength);

                    FileUtils.writeByteArrayToFile(deployFile, deployKeyfileBuf);
                    deployFilePath = systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID + "/"+ deployName;
                    Integer debufProfileLength = Files.readAllBytes(deployFile.toPath()).length;
                    offset = offset + debufProfileLength;

                } catch (IOException e) {

                    log.error("builder key file send binary message error",e);
                }

            }else {

            }

                // certificate
                byte[][] certificateKeyNameLengthBuf = new byte[certificateCnt][];
                byte[][] certificateKeyNameBuf = new byte[certificateCnt][];
                byte[][] certificateKeyNameBytes = new byte[certificateCnt][];
                byte[][] certificatePasswordLengthBuf = new byte[certificateCnt][];
                byte[][] certificatePasswordBuf = new byte[certificateCnt][];
                byte[][] certificatePasswordBytes = new byte[certificateCnt][];
                byte[][] certificateNameLengthBuf = new byte[certificateCnt][];
                byte[][] certificateNameBuf = new byte[certificateCnt][];
                byte[][] certificateNameBytes = new byte[certificateCnt][];
                byte[][] certificateFileIntBuf = new byte[certificateCnt][];
                byte[][] certificateFileBuf = new byte[certificateCnt][];


                for(int i = 0 ;i < certificateCnt; i++){
                    try {
                    File certificateFile = null;
                    JSONObject certificateJsonObj = new JSONObject();

                    certificateKeyNameLengthBuf[i] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, certificateKeyNameLengthBuf[i], 0, defaultOffset);
                    Integer certificateKeyNameIntLength = byteArrayToInt(certificateKeyNameLengthBuf[i]);
                    offset = offset + defaultOffset;

                    certificateKeyNameBuf[i] = new byte[certificateKeyNameIntLength];
                    System.arraycopy(payload, offset, certificateKeyNameBuf[i], 0, certificateKeyNameIntLength);
                    String certificateKeyName = new String(certificateKeyNameBuf[i], Charset.forName(StandardCharsets.UTF_8.name()));
                    certificateKeyNameBytes[i] = certificateKeyName.getBytes(StandardCharsets.UTF_8);
                    Integer certificateKeyNameLength = certificateKeyNameBytes[i].length;
                    offset = offset + certificateKeyNameLength;

                    certificatePasswordLengthBuf[i] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, certificatePasswordLengthBuf[i], 0, defaultOffset);
                    Integer certificatePasswordIntLength = byteArrayToInt(certificatePasswordLengthBuf[i]);
                    offset = offset + defaultOffset;

                    certificatePasswordBuf[i] = new byte[certificatePasswordIntLength];
                    System.arraycopy(payload, offset, certificatePasswordBuf[i], 0, certificatePasswordIntLength);
                    String certificatePassword = new String(certificatePasswordBuf[i], Charset.forName(StandardCharsets.UTF_8.name()));
                    certificatePasswordBytes[i] = certificatePassword.getBytes(StandardCharsets.UTF_8);
                    Integer certificatePasswordLength = certificatePasswordBytes[i].length;
                    offset = offset + certificatePasswordLength;

                    certificateNameLengthBuf[i] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, certificateNameLengthBuf[i], 0, defaultOffset);
                    Integer certificateNameIntLength = byteArrayToInt(certificateNameLengthBuf[i]);
                    offset = offset + defaultOffset;

                    certificateNameBuf[i] = new byte[certificateNameIntLength];
                    System.arraycopy(payload, offset, certificateNameBuf[i], 0, certificateNameIntLength);
                    String certificateName = new String(certificateNameBuf[i], Charset.forName(StandardCharsets.UTF_8.name()));
                    certificateNameBytes[i] = certificateName.getBytes(StandardCharsets.UTF_8);
                    Integer certificateNameLength = certificateNameBytes[i].length;
                    offset = offset + certificateNameLength;

                    if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                        certificateFile = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID +"/"+ BuilderDirectoryType.SIGNINGKEYFILE + i +"/"+ certificateName);
                    }
                    log.info(String.valueOf(certificateFile.exists()));
                    log.info(String.valueOf(certificateFile.isDirectory()));
                    if ( certificateFile != null && !certificateFile.exists()) {
                        try{
                            certificateFile.mkdir(); //폴더 생성합니다.
                            //System.out.println("폴더가 생성되었습니다.");
                        }
                        catch(Exception e){
                            e.getStackTrace();
                        }
                    }else {
                        // System.out.println("이미 폴더가 생성되어 있습니다.");
                    }

                    certificateFileIntBuf[i] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, certificateFileIntBuf[i], 0, defaultOffset);
                    Integer certificatefileIntLength = byteArrayToInt(certificateFileIntBuf[i]);
                    offset = offset + defaultOffset;

                    // keyfile
                    certificateFileBuf[i] = new byte[certificatefileIntLength];
                    System.arraycopy(payload, offset, certificateFileBuf[i], 0, certificatefileIntLength);


                        FileUtils.writeByteArrayToFile(certificateFile, certificateFileBuf[i]);
                        Integer certificateFileLength = Files.readAllBytes(certificateFile.toPath()).length;
                        offset = offset + certificateFileLength;
                        certificateJsonObj.put("certificate_key_name",certificateKeyName);
                        certificateJsonObj.put("certificate_password",certificatePassword);
                        certificateJsonObj.put("certificate_path",systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID +"/"+ BuilderDirectoryType.SIGNINGKEYFILE + i +"/"+ certificateName);
                        certificateArrayListObj.add(certificateJsonObj);


                    } catch (IOException e) {

                        log.error("builder key file send binary message error",e);
                    }

                }

                // profiles

                byte[][] profilesKeyNameLengthBuf = new byte[profileCnt][];
                byte[][] profilesKeyNameBuf = new byte[profileCnt][];
                byte[][] profilesKeyNameBytes = new byte[profileCnt][];
                byte[][] profilesBuildTypeLengthBuf = new byte[profileCnt][];
                byte[][] profilesBuildTypeBuf = new byte[profileCnt][];
                byte[][] profilesBuildTypeBytes = new byte[profileCnt][];
                byte[][] profilesNameLengthBuf = new byte[profileCnt][];
                byte[][] profilesNameBuf = new byte[profileCnt][];
                byte[][] profilesNameBytes = new byte[profileCnt][];
                byte[][] profilesFileIntBuf = new byte[profileCnt][];
                byte[][] profilesFileBuf = new byte[profileCnt][];

                for(int j = 0 ;j < profileCnt; j++){
                    try {
                    File prpfilesFile = null;
                    JSONObject profileJsonObj = new JSONObject();

                    profilesKeyNameLengthBuf[j] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, profilesKeyNameLengthBuf[j], 0, defaultOffset);
                    Integer profilesKeyNameIntLength = byteArrayToInt(profilesKeyNameLengthBuf[j]);
                    offset = offset + defaultOffset;

                    profilesKeyNameBuf[j] = new byte[profilesKeyNameIntLength];
                    System.arraycopy(payload, offset, profilesKeyNameBuf[j], 0, profilesKeyNameIntLength);
                    String profilesKeyName = new String(profilesKeyNameBuf[j], Charset.forName(StandardCharsets.UTF_8.name()));
                        log.info(profilesKeyName);
                    profilesKeyNameBytes[j] = profilesKeyName.getBytes(StandardCharsets.UTF_8);
                    Integer profilesKeyNameLength = profilesKeyNameBytes[j].length;
                    offset = offset + profilesKeyNameLength;

                    profilesBuildTypeLengthBuf[j] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, profilesBuildTypeLengthBuf[j], 0, defaultOffset);
                    Integer profilesBuildTypeIntLength = byteArrayToInt(profilesBuildTypeLengthBuf[j]);
                    offset = offset + defaultOffset;

                    profilesBuildTypeBuf[j] = new byte[profilesBuildTypeIntLength];
                    System.arraycopy(payload, offset, profilesBuildTypeBuf[j], 0, profilesBuildTypeIntLength);
                    String profilesBuildType = new String(profilesBuildTypeBuf[j], Charset.forName(StandardCharsets.UTF_8.name()));
                        log.info(profilesBuildType);
                    profilesBuildTypeBytes[j] = profilesBuildType.getBytes(StandardCharsets.UTF_8);
                    Integer profilesBuildTypeLength = profilesBuildTypeBytes[j].length;
                    offset = offset + profilesBuildTypeLength;

                    profilesNameLengthBuf[j] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, profilesNameLengthBuf[j], 0, defaultOffset);
                    Integer profilesNameIntLength = byteArrayToInt(profilesNameLengthBuf[j]);
                    offset = offset + defaultOffset;

                    profilesNameBuf[j] = new byte[profilesNameIntLength];
                    System.arraycopy(payload, offset, profilesNameBuf[j], 0, profilesNameIntLength);
                    String profilesName = new String(profilesNameBuf[j], Charset.forName(StandardCharsets.UTF_8.name()));
                    log.info(profilesName);
                    profilesNameBytes[j] = profilesName.getBytes(StandardCharsets.UTF_8);
                    Integer profilesNameLength = profilesNameBytes[j].length;
                    offset = offset + profilesNameLength;

                    if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                        prpfilesFile = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID+"/"+ BuilderDirectoryType.RELEASE_PROFILE + j +"/"+ profilesName);
                    }

                    if ( prpfilesFile != null && !prpfilesFile.exists()) {
                        try{
                            prpfilesFile.mkdir(); //폴더 생성합니다.
                            //System.out.println("폴더가 생성되었습니다.");
                        }
                        catch(Exception e){
                            e.getStackTrace();
                        }
                    }else {
                        // System.out.println("이미 폴더가 생성되어 있습니다.");
                    }

                    profilesFileIntBuf[j] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, profilesFileIntBuf[j], 0, defaultOffset);
                    Integer profilesIntLength = byteArrayToInt(profilesFileIntBuf[j]);
                    offset = offset + defaultOffset;

                    // keyfile
                    profilesFileBuf[j] = new byte[profilesIntLength];
                    System.arraycopy(payload, offset, profilesFileBuf[j], 0, profilesIntLength);


                        FileUtils.writeByteArrayToFile(prpfilesFile, profilesFileBuf[j]);
                        Integer profilesFileLength = Files.readAllBytes(prpfilesFile.toPath()).length;
                        offset = offset + profilesFileLength;

                        profileJsonObj.put("profiles_key_name",profilesKeyName);
                        profileJsonObj.put("profiles_build_type",profilesBuildType);
                        profileJsonObj.put("profiles_path", systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID+"/"+ BuilderDirectoryType.RELEASE_PROFILE + j +"/"+ profilesName);
                        profilesArrayJsonObj.add(profileJsonObj);
                    } catch (IOException e) {

                        log.error("builder key file send binary message error",e);
                    }

                }
                // builder 에서 manager 데이터 통신 하는 구간..
                iOSAllKeyfilesCreateMsg(session, iOSAllKeyCreateMsg, profilesArrayJsonObj, certificateArrayListObj, hqKey, signingkeyID, deployFilePath);

        } else if(msgType.equals(ProjectServiceType.BIN_FILE_IOS_ALL_KEY_FILE_UPDATE_SEND_INFO.name())){

            byte[] adminIdLengthBuf = new byte[defaultOffset];
            List<Object> certificateArrayListObj = new ArrayList<>();
            List<Object> profilesArrayJsonObj = new ArrayList<>();
            String deployFilePath = "";

            File deployFile = null;
            File deployDir = null;

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

            // profile cnt
            byte[] profileCntLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, profileCntLengthBuf, 0, defaultOffset);
            Integer profileCntIntLength = byteArrayToInt(profileCntLengthBuf);
            offset = offset + defaultOffset;

            byte[] profileCntBuf = new byte[profileCntIntLength];
            System.arraycopy(payload, offset, profileCntBuf, 0, domainIDIntLength);
            Integer profileCnt = new Integer(new String(profileCntBuf, Charset.forName(StandardCharsets.UTF_8.name())));

            byte[] profileCntBytes =  profileCnt.toString().getBytes(StandardCharsets.UTF_8);
            Integer profileCntLength = profileCntBytes.length;
            offset = offset + profileCntLength;

            // certificate cnt
            byte[] certificateCntLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, certificateCntLengthBuf, 0, defaultOffset);
            Integer certificateCntIntLength = byteArrayToInt(certificateCntLengthBuf);
            offset = offset + defaultOffset;

            byte[] certificateCntBuf = new byte[certificateCntIntLength];
            System.arraycopy(payload, offset, certificateCntBuf, 0, domainIDIntLength);
            Integer certificateCnt = new Integer(new String(certificateCntBuf, Charset.forName(StandardCharsets.UTF_8.name())));

            byte[] certificateCntBytes =  certificateCnt.toString().getBytes(StandardCharsets.UTF_8);
            Integer certificateCntLength = certificateCntBytes.length;
            offset = offset + certificateCntLength;

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
                try {
                    // deploy name
                    byte[] deployNameLengthBuf = new byte[defaultOffset];
                    System.arraycopy(payload, offset, deployNameLengthBuf, 0, defaultOffset);
                    Integer deployNameIntLength = byteArrayToInt(deployNameLengthBuf);
                    offset = offset + defaultOffset;

                    // deploy name
                    byte[] deployNameBuf = new byte[deployNameIntLength];
                    System.arraycopy(payload, offset, deployNameBuf, 0, deployNameIntLength);
                    String deployName = new String(deployNameBuf, Charset.forName(StandardCharsets.UTF_8.name()));
                    byte[] deployNameBytes = deployName.getBytes(StandardCharsets.UTF_8);
                    Integer deployNameLength = deployNameBytes.length;
                    offset = offset + deployNameLength;

                    Path deployPath = Paths.get(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID );

                    try (Stream<Path> filePathStream= Files.walk(deployPath)) {

                        filePathStream.filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {

                            String fileNameToString = String.valueOf(filePath.getFileName());

                            if (fileNameToString.matches(".*.p8.*")) {

                                try {
                                    Files.delete(Paths.get(deployPath +"/" +fileNameToString ));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                            }

                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                        deployDir = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID);
                        deployFile = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID + "/"+ deployName);
                    }


                    if ( deployDir != null && !deployDir.exists()) {
                        try{
                            deployDir.mkdir(); //폴더 생성합니다.
                            //System.out.println("폴더가 생성되었습니다.");
                        }
                        catch(Exception e){
                            e.getStackTrace();
                        }
                    }else {
                        // System.out.println("이미 폴더가 생성되어 있습니다.");
                    }

                    // deployKeyfile
                    byte[] deployKeyfileIntBuf = new byte[defaultOffset];
                    System.arraycopy(payload, offset, deployKeyfileIntBuf, 0, defaultOffset);
                    Integer deployKeyfileIntLength = byteArrayToInt(deployKeyfileIntBuf);
                    offset = offset + defaultOffset;

                    // keyfile
                    byte[] deployKeyfileBuf = new byte[deployKeyfileIntLength];
                    System.arraycopy(payload, offset, deployKeyfileBuf, 0, deployKeyfileIntLength);

                    FileUtils.writeByteArrayToFile(deployFile, deployKeyfileBuf);
                    deployFilePath = systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID + "/"+ deployName;
                    Integer debufProfileLength = Files.readAllBytes(deployFile.toPath()).length;
                    offset = offset + debufProfileLength;

                } catch (IOException e) {

                    log.error("builder key file send binary message error",e);
                }

            }else {

            }

            // certificate
            byte[][] certificateKeyNameLengthBuf = new byte[certificateCnt][];
            byte[][] certificateKeyNameBuf = new byte[certificateCnt][];
            byte[][] certificateKeyNameBytes = new byte[certificateCnt][];
            byte[][] certificatePasswordLengthBuf = new byte[certificateCnt][];
            byte[][] certificatePasswordBuf = new byte[certificateCnt][];
            byte[][] certificatePasswordBytes = new byte[certificateCnt][];
            byte[][] certificateNameLengthBuf = new byte[certificateCnt][];
            byte[][] certificateNameBuf = new byte[certificateCnt][];
            byte[][] certificateNameBytes = new byte[certificateCnt][];
            byte[][] certificateFileIntBuf = new byte[certificateCnt][];
            byte[][] certificateFileBuf = new byte[certificateCnt][];


            for(int i = 0 ;i < certificateCnt; i++){
                try {
                    File certificateFile = null;
                    File certificateDir = null;
                    JSONObject certificateJsonObj = new JSONObject();

                    certificateKeyNameLengthBuf[i] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, certificateKeyNameLengthBuf[i], 0, defaultOffset);
                    Integer certificateKeyNameIntLength = byteArrayToInt(certificateKeyNameLengthBuf[i]);
                    offset = offset + defaultOffset;

                    certificateKeyNameBuf[i] = new byte[certificateKeyNameIntLength];
                    System.arraycopy(payload, offset, certificateKeyNameBuf[i], 0, certificateKeyNameIntLength);
                    String certificateKeyName = new String(certificateKeyNameBuf[i], Charset.forName(StandardCharsets.UTF_8.name()));
                    certificateKeyNameBytes[i] = certificateKeyName.getBytes(StandardCharsets.UTF_8);
                    Integer certificateKeyNameLength = certificateKeyNameBytes[i].length;
                    offset = offset + certificateKeyNameLength;

                    certificatePasswordLengthBuf[i] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, certificatePasswordLengthBuf[i], 0, defaultOffset);
                    Integer certificatePasswordIntLength = byteArrayToInt(certificatePasswordLengthBuf[i]);
                    offset = offset + defaultOffset;

                    certificatePasswordBuf[i] = new byte[certificatePasswordIntLength];
                    System.arraycopy(payload, offset, certificatePasswordBuf[i], 0, certificatePasswordIntLength);
                    String certificatePassword = new String(certificatePasswordBuf[i], Charset.forName(StandardCharsets.UTF_8.name()));
                    certificatePasswordBytes[i] = certificatePassword.getBytes(StandardCharsets.UTF_8);
                    Integer certificatePasswordLength = certificatePasswordBytes[i].length;
                    offset = offset + certificatePasswordLength;

                    certificateNameLengthBuf[i] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, certificateNameLengthBuf[i], 0, defaultOffset);
                    Integer certificateNameIntLength = byteArrayToInt(certificateNameLengthBuf[i]);
                    offset = offset + defaultOffset;

                    certificateNameBuf[i] = new byte[certificateNameIntLength];
                    System.arraycopy(payload, offset, certificateNameBuf[i], 0, certificateNameIntLength);
                    String certificateName = new String(certificateNameBuf[i], Charset.forName(StandardCharsets.UTF_8.name()));
                    certificateNameBytes[i] = certificateName.getBytes(StandardCharsets.UTF_8);
                    Integer certificateNameLength = certificateNameBytes[i].length;
                    offset = offset + certificateNameLength;

                    if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                        certificateDir = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID +"/"+ BuilderDirectoryType.SIGNINGKEYFILE + i );
                        certificateFile = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID +"/"+ BuilderDirectoryType.SIGNINGKEYFILE + i +"/"+ certificateName);
                    }

                    // certificateDir delete
                    Path certificatePath = Paths.get(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID +"/"+ BuilderDirectoryType.SIGNINGKEYFILE + i );

                    try (Stream<Path> filePathStream= Files.walk(certificatePath)) {

                        filePathStream.filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {

                            String fileNameToString = String.valueOf(filePath.getFileName());

                            if (fileNameToString.matches(".*.p12.*")) {

                                try {
                                    Files.delete(Paths.get(certificatePath +"/" +fileNameToString ));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                            }

                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    log.info(String.valueOf(certificateDir.exists()));
                    log.info(String.valueOf(certificateDir.isDirectory()));
                    if ( certificateDir != null && !certificateDir.exists()) {
                        try{
                            certificateDir.mkdir(); //폴더 생성합니다.
                            //System.out.println("폴더가 생성되었습니다.");
                        }
                        catch(Exception e){
                            e.getStackTrace();
                        }
                    }else {
                        // System.out.println("이미 폴더가 생성되어 있습니다.");
                    }

                    certificateFileIntBuf[i] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, certificateFileIntBuf[i], 0, defaultOffset);
                    Integer certificatefileIntLength = byteArrayToInt(certificateFileIntBuf[i]);
                    offset = offset + defaultOffset;

                    // keyfile
                    certificateFileBuf[i] = new byte[certificatefileIntLength];
                    System.arraycopy(payload, offset, certificateFileBuf[i], 0, certificatefileIntLength);


                    FileUtils.writeByteArrayToFile(certificateFile, certificateFileBuf[i]);
                    Integer certificateFileLength = Files.readAllBytes(certificateFile.toPath()).length;
                    offset = offset + certificateFileLength;
                    certificateJsonObj.put("certificate_key_name",certificateKeyName);
                    certificateJsonObj.put("certificate_password",certificatePassword);
                    certificateJsonObj.put("certificate_path",systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID +"/"+ BuilderDirectoryType.SIGNINGKEYFILE + i +"/"+ certificateName);
                    certificateArrayListObj.add(certificateJsonObj);


                } catch (IOException e) {

                    log.error("builder key file send binary message error",e);
                }

            }

            // profiles

            byte[][] profilesKeyNameLengthBuf = new byte[profileCnt][];
            byte[][] profilesKeyNameBuf = new byte[profileCnt][];
            byte[][] profilesKeyNameBytes = new byte[profileCnt][];
            byte[][] profilesBuildTypeLengthBuf = new byte[profileCnt][];
            byte[][] profilesBuildTypeBuf = new byte[profileCnt][];
            byte[][] profilesBuildTypeBytes = new byte[profileCnt][];
            byte[][] profilesNameLengthBuf = new byte[profileCnt][];
            byte[][] profilesNameBuf = new byte[profileCnt][];
            byte[][] profilesNameBytes = new byte[profileCnt][];
            byte[][] profilesFileIntBuf = new byte[profileCnt][];
            byte[][] profilesFileBuf = new byte[profileCnt][];

            for(int j = 0 ;j < profileCnt; j++){
                try {
                    File profilesFile = null;
                    File profilesDir = null;
                    JSONObject profileJsonObj = new JSONObject();

                    profilesKeyNameLengthBuf[j] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, profilesKeyNameLengthBuf[j], 0, defaultOffset);
                    Integer profilesKeyNameIntLength = byteArrayToInt(profilesKeyNameLengthBuf[j]);
                    offset = offset + defaultOffset;

                    profilesKeyNameBuf[j] = new byte[profilesKeyNameIntLength];
                    System.arraycopy(payload, offset, profilesKeyNameBuf[j], 0, profilesKeyNameIntLength);
                    String profilesKeyName = new String(profilesKeyNameBuf[j], Charset.forName(StandardCharsets.UTF_8.name()));
                    log.info(profilesKeyName);
                    profilesKeyNameBytes[j] = profilesKeyName.getBytes(StandardCharsets.UTF_8);
                    Integer profilesKeyNameLength = profilesKeyNameBytes[j].length;
                    offset = offset + profilesKeyNameLength;

                    profilesBuildTypeLengthBuf[j] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, profilesBuildTypeLengthBuf[j], 0, defaultOffset);
                    Integer profilesBuildTypeIntLength = byteArrayToInt(profilesBuildTypeLengthBuf[j]);
                    offset = offset + defaultOffset;

                    profilesBuildTypeBuf[j] = new byte[profilesBuildTypeIntLength];
                    System.arraycopy(payload, offset, profilesBuildTypeBuf[j], 0, profilesBuildTypeIntLength);
                    String profilesBuildType = new String(profilesBuildTypeBuf[j], Charset.forName(StandardCharsets.UTF_8.name()));
                    log.info(profilesBuildType);
                    profilesBuildTypeBytes[j] = profilesBuildType.getBytes(StandardCharsets.UTF_8);
                    Integer profilesBuildTypeLength = profilesBuildTypeBytes[j].length;
                    offset = offset + profilesBuildTypeLength;

                    profilesNameLengthBuf[j] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, profilesNameLengthBuf[j], 0, defaultOffset);
                    Integer profilesNameIntLength = byteArrayToInt(profilesNameLengthBuf[j]);
                    offset = offset + defaultOffset;

                    profilesNameBuf[j] = new byte[profilesNameIntLength];
                    System.arraycopy(payload, offset, profilesNameBuf[j], 0, profilesNameIntLength);
                    String profilesName = new String(profilesNameBuf[j], Charset.forName(StandardCharsets.UTF_8.name()));
                    log.info(profilesName);
                    profilesNameBytes[j] = profilesName.getBytes(StandardCharsets.UTF_8);
                    Integer profilesNameLength = profilesNameBytes[j].length;
                    offset = offset + profilesNameLength;

                    // certificateDir delete
                    Path profilePath = Paths.get(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID +"/"+ BuilderDirectoryType.RELEASE_PROFILE + j );

                    try (Stream<Path> filePathStream= Files.walk(profilePath)) {

                        filePathStream.filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {

                            String fileNameToString = String.valueOf(filePath.getFileName());

                            if (fileNameToString.matches(".*.mobileprovision.*")) {

                                try {
                                    Files.delete(Paths.get(profilePath +"/" +fileNameToString ));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                            }

                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                        profilesDir = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID+"/"+ BuilderDirectoryType.RELEASE_PROFILE + j );
                        profilesFile = new File(systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID+"/"+ BuilderDirectoryType.RELEASE_PROFILE + j +"/"+ profilesName);
                    }

                    if ( profilesDir != null && !profilesDir.exists()) {
                        try{
                            profilesDir.mkdir(); //폴더 생성합니다.
                            //System.out.println("폴더가 생성되었습니다.");
                        }
                        catch(Exception e){
                            e.getStackTrace();
                        }
                    }else {
                        // System.out.println("이미 폴더가 생성되어 있습니다.");
                    }

                    profilesFileIntBuf[j] = new byte[defaultOffset];
                    System.arraycopy(payload, offset, profilesFileIntBuf[j], 0, defaultOffset);
                    Integer profilesIntLength = byteArrayToInt(profilesFileIntBuf[j]);
                    offset = offset + defaultOffset;

                    // keyfile
                    profilesFileBuf[j] = new byte[profilesIntLength];
                    System.arraycopy(payload, offset, profilesFileBuf[j], 0, profilesIntLength);


                    FileUtils.writeByteArrayToFile(profilesFile, profilesFileBuf[j]);
                    Integer profilesFileLength = Files.readAllBytes(profilesFile.toPath()).length;
                    offset = offset + profilesFileLength;

                    profileJsonObj.put("profiles_key_name",profilesKeyName);
                    profileJsonObj.put("profiles_build_type",profilesBuildType);
                    profileJsonObj.put("profiles_path", systemUserHomePath + dir + BuilderDirectoryType.DOMAIN_ + domainID + "/" + BuilderDirectoryType.ADMIN_+adminID +"/"+ platform +"/"+ BuilderDirectoryType.SIGNINGKEY_.toString() + signingkeyID+"/"+ BuilderDirectoryType.RELEASE_PROFILE + j +"/"+ profilesName);
                    profilesArrayJsonObj.add(profileJsonObj);
                } catch (IOException e) {

                    log.error("builder key file send binary message error",e);
                }

            }
            // builder 에서 manager 데이터 통신 하는 구간..
            iOSAllKeyfilesUpdateMsg(session, iOSAllKeyCreateMsg, profilesArrayJsonObj, certificateArrayListObj, hqKey, signingkeyID, deployFilePath);
        }

    }

    public int byteArrayToInt(byte [] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8) + (b[3] & 0xFF);
    }

    private void keyFileCreateMessage(SigningKeyCreateMessage signingKeyCreateMessage, String filePath, String hqKey, String signingkeyID){

        signingKeyCreateMessage.setMsgType(ProjectServiceType.BIN_FILE_PROFILE_TEMPLATE_SEND_INFO.name());
        signingKeyCreateMessage.setSessType(PayloadMsgType.HEADQUATER.name());

        signingKeyCreateMessage.setKeyfilePath(filePath);
        signingKeyCreateMessage.setHqKey(hqKey);
        signingKeyCreateMessage.setSigningkeyID(signingkeyID);

        Map<String, Object> parseResult = Mapper.convertValue(signingKeyCreateMessage, Map.class);
        headQuaterClientHandler.sendMessage(sessionTemp, parseResult);
    }

    // ios 전용으로 send message 메소드 추가해야함.
    private void iOSkeyFileCreateMessage(iOSAllKeyCreateMsg iOSAllKeyCreateMsg, JSONObject fileObj, String hqKey, String signingkeyID){

        iOSAllKeyCreateMsg.setMsgType(ProjectServiceType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_SEND_INFO.name());
        iOSAllKeyCreateMsg.setSessType(PayloadMsgType.HEADQUATER.name());

        iOSAllKeyCreateMsg.setKeyfilePath(fileObj.get("keyFilePath").toString());
        iOSAllKeyCreateMsg.setDebugProfilePath(fileObj.get("debugProFilePath").toString());
        iOSAllKeyCreateMsg.setReleaseProfilePath(fileObj.get("releaseProFilePath").toString());
        iOSAllKeyCreateMsg.setHqKey(hqKey);
        iOSAllKeyCreateMsg.setSigningkeyID(signingkeyID);

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

    // ios multi profile 전송 msg type
    private void iOSAllKeyfilesCreateMsg(WebSocketSession session, iOSAllKeyCreateMsg iOSAllKeyCreateMsg, List<Object> profilesList, List<Object> certificatesList, String hqKey, String signingkeyID, String deployFilePath){

        iOSAllKeyCreateMsg.setMsgType(ProjectServiceType.BIN_FILE_IOS_ALL_KEY_FILE_SEND_INFO.name());
        iOSAllKeyCreateMsg.setSessType(PayloadMsgType.HEADQUATER.name());

        iOSAllKeyCreateMsg.setProfilesList(profilesList);
        iOSAllKeyCreateMsg.setCertificatesList(certificatesList);
        iOSAllKeyCreateMsg.setHqKey(hqKey);
        iOSAllKeyCreateMsg.setSigningkeyID(signingkeyID);
        iOSAllKeyCreateMsg.setKeyfilePath(deployFilePath);

        Map<String, Object> parseResult = Mapper.convertValue(iOSAllKeyCreateMsg, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

    // BIN_FILE_IOS_ALL_KEY_FILE_UPDATE_SEND_INFO
    private void iOSAllKeyfilesUpdateMsg(WebSocketSession session, iOSAllKeyCreateMsg iOSAllKeyCreateMsg, List<Object> profilesList, List<Object> certificatesList, String hqKey, String signingkeyID, String deployFilePath){

        iOSAllKeyCreateMsg.setMsgType(ProjectServiceType.BIN_FILE_IOS_ALL_KEY_FILE_UPDATE_SEND_INFO.name());
        iOSAllKeyCreateMsg.setSessType(PayloadMsgType.HEADQUATER.name());

        iOSAllKeyCreateMsg.setProfilesList(profilesList);
        iOSAllKeyCreateMsg.setCertificatesList(certificatesList);
        iOSAllKeyCreateMsg.setHqKey(hqKey);
        iOSAllKeyCreateMsg.setSigningkeyID(signingkeyID);
        iOSAllKeyCreateMsg.setKeyfilePath(deployFilePath);

        Map<String, Object> parseResult = Mapper.convertValue(iOSAllKeyCreateMsg, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
    }
}
