package com.pspotl.sidebranden.common.workspace;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;


@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class WorkspaceBuildProject {

    private Long id;

    private String workspace_name;

    private boolean favorite;

    private String buildproject;
}
