package com.smartpos.service;

/**
 * BaseDatabase: Abstract Class แม่สำหรับโครงสร้าง Database
 * Principles: Abstraction
 */
public abstract class BaseDatabase {

    protected String dbName;

    public BaseDatabase(String dbName) {
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }
}