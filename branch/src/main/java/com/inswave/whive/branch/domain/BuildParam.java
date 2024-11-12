package com.inswave.whive.branch.domain;


import lombok.Data;

@Data
public class BuildParam {
    private String path;
    private String platform;
    private BuildMode mode;
    private String outputPath;

    public BuildParam() {

    }

    public BuildParam(String path, String platform, BuildMode mode, String outputPath ){
        this.path = path;
        this.platform = platform;
        this.mode = mode;
        this.outputPath = outputPath;

    }

    public String getPath() { return path; }

    public String platform() { return platform; }

    public BuildMode mode() { return mode; }

    public String outputPath() { return outputPath; }

}

