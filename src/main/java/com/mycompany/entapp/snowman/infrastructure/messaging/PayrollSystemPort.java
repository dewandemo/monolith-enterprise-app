/*
 * |-------------------------------------------------
 * | Copyright © 2018 Acme Corp. All rights reserved.
 * |-------------------------------------------------
 */
package com.mycompany.entapp.snowman.infrastructure.messaging;

import com.mycompany.entapp.snowman.infrastructure.messaging.dto.EmployeeDTO;

public interface PayrollSystemPort {
    void sendEmployeeInfo(EmployeeDTO employeeDTO);
}
