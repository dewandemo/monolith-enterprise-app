/*
 * |-------------------------------------------------
 * | Copyright © 2018 Acme Corp. All rights reserved.
 * |-------------------------------------------------
 */
package com.mycompany.entapp.snowman.domain.exception;

public class BusinessException extends Exception {

    public BusinessException(String message){
        super(message);
    }
}
