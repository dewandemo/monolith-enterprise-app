/*
 * |-------------------------------------------------
 * | Copyright © 2018 Acme Corp. All rights reserved.
 * |-------------------------------------------------
 */
package com.mycompany.entapp.snowman.domain.service;

import com.mycompany.entapp.snowman.domain.model.Project;

public interface ProjectService {
    Project getProject(int projectId);
    void createProject(Project project);
    void updateProject(Project project);
    void deleteProject(int projectId);
}
