package cn.edu.xmu.aiagent.common;

public enum ErrorCode {
    SUCCESS(20000, "ok", ""),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    SYSTEM_ERROR(50000, "系统内部异常", "");

    /**
     * 状态码
     */
    private int code;
    /**
     * 状态码信息
     */
    private String message;
    /**
     * 状态码描述（详情）
     */
    private String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    public String getDescription() {
        return description;
    }
}
