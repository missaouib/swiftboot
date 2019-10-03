package org.swiftboot.web.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * 默认的修改状态返回对象
 *
 * @author swiftech
 **/
public class DefaultStatusChangeResult {

    @ApiModelProperty(value = "新状态码")
    @JsonProperty("new_status_code")
    private int newStatusCode;

    @ApiModelProperty(value = "新状态名称")
    @JsonProperty("new_status_name")
    private String newStatusName;

    public DefaultStatusChangeResult(int newStatusCode) {
        this.newStatusCode = newStatusCode;
    }

    public DefaultStatusChangeResult(int newStatusCode, String newStatusName) {
        this.newStatusCode = newStatusCode;
        this.newStatusName = newStatusName;
    }

    public int getNewStatusCode() {
        return newStatusCode;
    }

    public void setNewStatusCode(int newStatusCode) {
        this.newStatusCode = newStatusCode;
    }

    public String getNewStatusName() {
        return newStatusName;
    }

    public void setNewStatusName(String newStatusName) {
        this.newStatusName = newStatusName;
    }
}
