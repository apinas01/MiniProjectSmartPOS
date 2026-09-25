package com.smartpos.service;

public abstract class BaseDatabase {
    protected String dbName;

    public BaseDatabase(String dbName) {
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }
}