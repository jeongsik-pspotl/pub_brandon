package com.inswave.whive.branch.controller;

import com.inswave.whive.branch.domain.BuildParam;
import com.inswave.whive.branch.domain.BuildResponse;
import com.inswave.whive.branch.service.ProjectImportService;
import com.inswave.whive.branch.util.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class BuilderProjectImportController {

    @Autowired
    ProjectImportService projectImportService;

    @Value("${whive.distribution.UserRootPath}")
    private String UserRootPath;

    private String systemUserHomePath = System.getProperty("user.home");

    JSONParser parser = new JSONParser();

    @RequestMapping(value = "/builder/project/import/Upload", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public @ResponseBody DeferredResult<BuildResponse> addRequest(HttpServletRequest request, @RequestParam("zipFile") MultipartFile zipfile
            ,@RequestParam("projectImportJson") String projectImportJson) {

//        Map<String, Object> parseResult = new HashMap<>();
        String sessionId = ServletUtil.getSession().getId();
        log.info(">> Build task add. session id : {}", sessionId);

        try {
            JSONObject jsonProjectImportObj = (JSONObject) parser.parse(projectImportJson);
            JSONObject jsonRepositoryObj = (JSONObject) parser.parse(jsonProjectImportObj.get("jsonRepository").toString());
            String vcsType = jsonRepositoryObj.get("vcsType").toString();

            if(vcsType.equals("localgit") || vcsType.equals("localsvn")){
                /**
                 *  local Git 방식으로 project import 기능 수행
                 */

                projectImportService.startProjectLocalGitImport(zipfile, jsonProjectImportObj);
            } else if(vcsType.equals("git")){
                /**
                 *  Git 방식으로 project import 기능 수행
                 */

                projectImportService.startProjectGitImport(zipfile, jsonProjectImportObj);
            }


            BuildResponse buildResponse = new BuildResponse();
            buildResponse.setResponseResult(BuildResponse.ResponseResult.SUCCESS);
            buildResponse.setbuildTaskId(sessionId);
            buildResponse.setSessionId(sessionId);

            final DeferredResult<BuildResponse> deferredResult = new DeferredResult<>(null);
            deferredResult.setResult(buildResponse);

            return deferredResult;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
