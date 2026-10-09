/*
 * |-------------------------------------------------
 * | Copyright © 2017 Acme Corp. All rights reserved.
 * |-------------------------------------------------
 */
package com.mycompany.entapp.snowman.domain.service;

import com.mycompany.entapp.snowman.domain.exception.BusinessException;
import com.mycompany.entapp.snowman.domain.model.AppInfo;

public interface ApplicationInfoService {
    AppInfo getAppInfo() throws BusinessException;
}
