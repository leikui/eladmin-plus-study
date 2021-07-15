package me.zhengjie.modules.flow.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 工作流Entity
 * @author Spur
 * @date 2019-11-27
 */
public class Act implements Serializable {
    private static final long serialVersionUID = 1L;

    // 任务编号
    private String taskId;

    // 任务名称
    private String taskName;

    // 任务定义Key（任务环节标识）
    private String taskDefKey;

    // 流程实例ID
    private String procInsId;

    // 流程定义ID
    private String procDefId;

    // 流程定义Key（流程定义标识）
    private String procDefKey;

    // 业务绑定Table
    private String businessTable;

    // 业务绑定ID
    private String businessId;

    // 表单KEY
    private String formKey;

    // 发起人
    private String applyUser;

    // 发起人名称
    private String applyUserName;

    // 任务标题
    private String title;

    // 任务状态（todo/claim/finish）
    private String status;

    // 流程执行（办理）RUL
    //	private String procExecUrl;
    // 任务意见
    private String comment;

    // 意见状态
    private String flag;

    // 任务执行人编号
    private String assignee;

    // 任务执行人名称
    private String assigneeName;

    // 流程变量
    private Variable vars;

    // 流程任务变量
//	private Variable taskVars;

    // 开始日期
    private Date beginDate;

    // 结束日期
    private Date endDate;

    // 任务列表
    private List<Act> list;

    // 任务创建日期
    private Date taskCreateTime;

    // 节点ID
    private String activityId;

    protected boolean isNewTask = false;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDefKey() {
        return taskDefKey;
    }

    public void setTaskDefKey(String taskDefKey) {
        this.taskDefKey = taskDefKey;
    }

    public String getProcInsId() {
        return procInsId;
    }

    public void setProcInsId(String procInsId) {
        this.procInsId = procInsId;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public void setProcDefId(String procDefId) {
        this.procDefId = procDefId;
    }

    public String getProcDefKey() {
        return procDefKey;
    }

    public void setProcDefKey(String procDefKey) {
        this.procDefKey = procDefKey;
    }

    public String getBusinessTable() {
        return businessTable;
    }

    public void setBusinessTable(String businessTable) {
        this.businessTable = businessTable;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    public String getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(String applyUser) {
        this.applyUser = applyUser;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public Variable getVars() {
        return vars;
    }

    public void setVars(Variable vars) {
        this.vars = vars;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Act> getList() {
        return list;
    }

    public void setList(List<Act> list) {
        this.list = list;
    }

    public Date getTaskCreateTime() {
        return taskCreateTime;
    }

    public void setTaskCreateTime(Date taskCreateTime) {
        this.taskCreateTime = taskCreateTime;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public boolean isNewTask() {
        return isNewTask;
    }

    public void setNewTask(boolean newTask) {
        isNewTask = newTask;
    }
}
