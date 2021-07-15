package me.zhengjie.modules.flow.domain;

import java.util.Map;

/**
 * 流程传输Bean
 * @author Spur
 * @date 2019-11-27
 */
public class FlowRequestBean {
    // 流程定义ID
    private String procDefKey;

    // 业务ID
    private String bizKey;

    // 流程实例ID
    private String procInsId;

    // 任务ID
    private String taskId;

    // 标题
    private String title;

    // 意见
    private String comment;

    // 发起人
    private String applyUser;

    // 处理人
    private String userId;

    // 跳转目标节点
    private String targetNodeId;

    // 变量
    private Map<String, Object> map;

    public String getProcDefKey() {
        return procDefKey;
    }

    public void setProcDefKey(String procDefKey) {
        this.procDefKey = procDefKey;
    }

    public String getBizKey() {
        return bizKey;
    }

    public void setBizKey(String bizKey) {
        this.bizKey = bizKey;
    }

    public String getProcInsId() {
        return procInsId;
    }

    public void setProcInsId(String procInsId) {
        this.procInsId = procInsId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(String applyUser) {
        this.applyUser = applyUser;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTargetNodeId() {
        return targetNodeId;
    }

    public void setTargetNodeId(String targetNodeId) {
        this.targetNodeId = targetNodeId;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
