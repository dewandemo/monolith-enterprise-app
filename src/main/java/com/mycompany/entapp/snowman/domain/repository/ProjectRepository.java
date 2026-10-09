/*
 * |-------------------------------------------------
 * | Copyright © 2018 Acme Corp. All rights reserved.
 * |-------------------------------------------------
 */
package com.mycompany.entapp.snowman.domain.repository;

import com.mycompany.entapp.snowman.domain.model.Project;

public interface ProjectRepository {
    Project findProject(int projectId);
    void saveProject(Project project);
    void removeProject(int projectId);
}
