package com.inswave.whive.headquater.domian;

public class BranchDebugBuild {

    // socket message 데이터 타입
    // 메시지 설계 내용 대로 넣어두기
    private String path;
    private String platform;
    private String mode;
    private String outputPath;

    public BranchDebugBuild() {

    }

    public BranchDebugBuild(String path, String platform, String mode, String outputPath ){
        this.path = path;
        this.platform = platform;
        this.mode = mode;
        this.outputPath = outputPath;

    }

    public String getPath() { return path; }

    public String getPlatform() { return platform; }

    public String getMode() { return mode; }

    public String getOutputPath() { return outputPath; }


}
