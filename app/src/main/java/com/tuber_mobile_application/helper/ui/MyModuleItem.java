package com.tuber_mobile_application.helper.ui;

public class MyModuleItem
{
    private int moduleID;
    private String moduleName;
    private String moduleCode;
    private int imgActionId;

    public MyModuleItem(int moduleID, String moduleName, String moduleCode, int imgActionId){
        this.moduleID = moduleID;
        this.moduleName = moduleName;
        this.moduleCode = moduleCode;
        this.imgActionId = imgActionId;
    }

    public int getModuleID() {
        return moduleID;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public int getImgActionId() {
        return imgActionId;
    }
}
