package me.zhengjie.modules.flow.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.flow.domain.Act;
import me.zhengjie.modules.flow.domain.Variable;
import me.zhengjie.modules.flow.service.FlowService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FormProperty;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.idm.api.User;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Spur
 * @date 2019-11-27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FLowServiceImpl implements FlowService {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private IdentityService identityService;

    @Override
    public String importProcessDefinition(MultipartFile file) {
        ProcessDefinition processDefinition;
        try {
            Deployment deployment = repositoryService.createDeployment()
                    // .key()
                    // .name(name)
                    // .category(category)
                    // .tenantId()
                    // ?????????????????????????????????????????????
                    // .addZipInputStream()
                    // ??????InputStream???????????????
                    .addInputStream(file.getOriginalFilename(), file.getInputStream())
                    // ???????????????classpath??????????????????????????????
                    // .addClasspathResource("p1.bpmn20.xml")
                    // ??????xml??????????????????
                    // .addString()
                    .deploy();
             processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
            if (processDefinition == null) {
                return null;
            }
        } catch (Exception e) {
            throw new BadRequestException("???????????????????????????" + e.getMessage());
        }
        return processDefinition.getKey();
    }

    /**
     * ????????????
     *
     * @param procDefKey ????????????Key
     * @param bizKey     ????????????ID
     * @param title      ??????
     * @param appUser    ???????????????
     * @param comment    ????????????
     * @param var        ????????????
     * @return ????????????ID
     */
    @Override
    public String startProcess(String procDefKey, String bizKey, String title, String comment, String appUser, Map<String, Object> var) {
        // ?????????????????????
        if(var !=null){
            var.put("applyUser",appUser);
        }else{
            var=new HashMap<>();
            var.put("applyUser",appUser);
        }
        //????????????
        ProcessInstance processInstance=runtimeService.startProcessInstanceByKey(procDefKey,bizKey,var);
        String processInstanceId = processInstance.getId();
        //?????????????????????????????????
        Task task=taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
        complete(processInstanceId, task.getId(), title, comment, var);
        return processInstanceId;
    }

    /**
     * ????????????
     * @param taskId ????????????ID
     * @param userId ?????????ID
     */
    @Override
    public void claim(String taskId, String userId) {
        taskService.claim(taskId,userId);
    }

    /**
     * ????????????
     * @param procInsId ????????????ID
     * @param taskId    ????????????ID
     * @param title     ??????
     * @param comment   ????????????
     * @param var       ????????????
     */
    @Override
    public void complete(String procInsId, String taskId, String title, String comment, Map<String, Object> var) {
        var.put("title",title);
        complete(procInsId,taskId,comment,var);
    }

    /**
     * ????????????
     * @param procInsId ????????????ID
     * @param taskId    ????????????ID
     * @param comment   ????????????
     * @param var       ????????????
     */
    @Override
    public void complete(String procInsId, String taskId, String comment, Map<String, Object> var) {
        //??????????????????
        if (StringUtils.isNotEmpty(comment)) {
            taskService.addComment(taskId, procInsId, comment);
        }
        taskService.complete(taskId, var);
    }

    /**
     * ??????
     * @param procInsId    ????????????ID
     * @param taskId       ????????????ID
     * @param comment      ????????????
     * @param targetNodeId ??????????????????ID(???????????????????????????nodeId)
     * @param var          ????????????
     */
    @Override
    public void jump(String procInsId, String taskId, String comment, String targetNodeId, Map<String, Object> var) {

    }

    /**
     * ??????
     * @param taskId  ????????????ID
     * @param procInsId ????????????ID
     * @param userId  ????????????ID
     * @param comment ????????????
     */
    @Override
    public void delegate(String taskId,String procInsId, String userId, String comment) {
        if(StringUtils.isNotBlank(comment)){
            taskService.addComment(taskId,procInsId,comment);
        }
        taskService.delegateTask(taskId,userId);
    }

    /**
     * ????????????
     * @param taskId  ????????????ID
     * @param procInsId ????????????ID
     * @param comment ????????????
     */
    @Override
    public void resolve(String taskId, String procInsId,String comment) {
        if(StringUtils.isNotBlank(comment)){
            taskService.addComment(taskId,procInsId,comment);
        }
        taskService.resolveTask(taskId);
    }

    /**
     * ????????????
     * @param procInsId    ????????????ID
     * @param taskId
     * @param deleteReason ????????????
     */
    @Override
    public void stop(String procInsId, String taskId, String deleteReason) {
        if(StringUtils.isNotBlank(deleteReason)){
            taskService.addComment(taskId,procInsId,deleteReason);
        }
        runtimeService.deleteProcessInstance(procInsId,deleteReason);
    }

    /**
     * ???????????????
     * @param procDefKey ????????????ID
     * @param bizKey ??????ID
     * @return
     * @throws IOException
     */
    @Override
    public String printProcessImage(String procDefKey, String bizKey) throws IOException {
        HistoricProcessInstance historicProcessInstance=historyService.createHistoricProcessInstanceQuery().processDefinitionKey(procDefKey).processInstanceBusinessKey(bizKey).singleResult();
        //??????????????????ID??????????????????????????????????????????????????????????????????
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(historicProcessInstance.getId()).list();

        //?????????????????????Activity???Id
        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }
        //???????????????
        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, "????????????", "????????????", "????????????",
                processEngineConfiguration.getClassLoader(), 1.0,true);
        byte[] buf = inputToByte(in);
        return "data:image/jpeg;base64," + Base64.encodeBase64String(buf);
    }
    private byte[] inputToByte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    /**
     * ??????????????????
     * @param map
     * @return
     */
    @Override
    public List<Act> getTodoList(Map<String, String> map) {
        String userId=String.valueOf(map.get("userId"));
        String title="";
        if(map.containsKey("title")){
            title=map.get("title").toString().toUpperCase();
        }
        List<Act> result = new ArrayList<Act>();
        // =============== ?????????????????????  ===============
        TaskQuery todoTaskQuery=taskService.createTaskQuery().taskAssignee(userId).active().includeProcessVariables();
        if(StringUtils.isNotBlank(title)){
            todoTaskQuery.processVariableValueLike("title","%"+title+"%");
        }
        List<Task> todoList = todoTaskQuery.orderByTaskCreateTime().desc().list();
        for (Task task : todoList) {
            Act e = new Act();
            e.setVars(new Variable(task.getProcessVariables()));
            ProcessInstance ps = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            e.setBusinessId(ps.getBusinessKey());
            e.setTaskId(task.getId());
            e.setTaskName(task.getName());
            e.setAssignee(task.getAssignee());
            e.setProcDefKey(ps.getProcessDefinitionKey());
            e.setTaskDefKey(task.getTaskDefinitionKey());
            e.setProcInsId(ps.getProcessInstanceId());
            e.setStatus("todo");
            e.setFormKey(task.getFormKey());
            e.setTaskCreateTime(task.getCreateTime());
            Map<String, Object> processVariables = taskService.getVariables(task.getId());
            e.setApplyUser((String) processVariables.get("applyUser"));
            e.setTitle((String) processVariables.get("title"));
            // ?????????????????????
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(e.getProcInsId()).orderByTaskCreateTime().asc().list();
            if (list.size() > 0) {
                if (StringUtils.isNotBlank(list.get(0).getAssignee())) {
                    User user = identityService.createUserQuery().userId(list.get(0).getAssignee()).singleResult();
                    e.setApplyUserName(user.getFirstName());
                    e.setApplyUser(user.getId());
                }
            }
            result.add(e);
        }
        // =============== ?????????????????????  ===============
        todoTaskQuery=taskService.createTaskQuery().taskCandidateUser(userId).active().includeProcessVariables();
        if(StringUtils.isNotBlank(title)){
            todoTaskQuery.processVariableValueLike("title","%"+title+"%");
        }
        List<Task> toClaimList = todoTaskQuery.orderByTaskCreateTime().desc().list();
        for (Task task : toClaimList) {
            Act e = new Act();
            e.setVars(new Variable(task.getProcessVariables()));
            ProcessInstance ps = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            e.setBusinessId(ps.getBusinessKey());
            e.setTaskId(task.getId());
            e.setTaskName(task.getName());
            e.setProcInsId(ps.getProcessInstanceId());
            e.setAssignee(userId);
            e.setProcDefKey(ps.getProcessDefinitionKey());
            e.setTaskDefKey(task.getTaskDefinitionKey());
            e.setStatus("claim");
            e.setFormKey(task.getFormKey());
            e.setTaskCreateTime(task.getCreateTime());
            Map<String, Object> processVariables = taskService.getVariables(task.getId());
            e.setTitle((String) processVariables.get("title"));
            // ?????????????????????
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(e.getProcInsId()).orderByTaskCreateTime().asc().list();
            if (list.size() > 0) {
                if (StringUtils.isNotBlank(list.get(0).getAssignee())) {
                    User user = identityService.createUserQuery().userId(list.get(0).getAssignee()).singleResult();
                    if(user != null && !user.getId().equals("")){
                        e.setApplyUserName(user.getFirstName());
                        e.setApplyUser(user.getId());
                    }

                }
            }
            result.add(e);
        }
        // ??????????????????????????????
        Collections.sort(result, new Comparator<Act>() {
            @Override
            public int compare(Act o1, Act o2) {
                if (o1.getTaskCreateTime().before(o2.getTaskCreateTime())) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return result;
    }

    /**
     *??????????????????
     * @param procDefKey ????????????ID
     * @param bizKey ??????ID
     * @return
     */
    @Override
    public List<Act> getTaskList(String procDefKey, String bizKey) {
        List<Act> actList=Lists.newArrayList();
        if(StringUtils.isNotBlank(procDefKey) && StringUtils.isNotBlank(bizKey)){
            HistoricProcessInstance historicProcessInstance=historyService.createHistoricProcessInstanceQuery().processDefinitionKey(procDefKey)
                    .processInstanceBusinessKey(bizKey).singleResult();
            List<HistoricActivityInstance> list=historyService.createHistoricActivityInstanceQuery().processInstanceId(historicProcessInstance.getId())
                    .orderByHistoricActivityInstanceStartTime().asc().orderByHistoricActivityInstanceEndTime().asc().list();
            Map<String, Integer> actMap = Maps.newHashMap();
            for (int i = 0; i < list.size(); i++) {
                HistoricActivityInstance histIns = list.get(i);
                // ????????????????????????????????????????????????????????????????????????
                if ("userTask".equals(histIns.getActivityType()) || "startEvent".equals(histIns.getActivityType())
                        || "endEvent".equals(histIns.getActivityType())) {
                    // ???????????????????????????
                    Integer actNum = actMap.get(histIns.getActivityId());
                    if (actNum == null) {
                        actMap.put(histIns.getActivityId(), actMap.size());
                    }
                    Act e = new Act();
                    e.setBeginDate(histIns.getStartTime());
                    e.setEndDate(histIns.getEndTime());
                    e.setActivityId(histIns.getActivityId());
                    switch (histIns.getActivityType()) {
                        case "startEvent":
                            e.setTaskName("??????");
                            break;
                        case "endEvent":
                            e.setTaskName("??????");
                            break;
                        default:
                            e.setTaskName(histIns.getActivityName());
                    }
                    // ???????????????????????????
                    if ("startEvent".equals(histIns.getActivityType())) {
                        List<HistoricProcessInstance> il = processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(historicProcessInstance.getId()).orderByProcessInstanceStartTime().asc().list();
                        if (il.size() > 0) {
                            if (StringUtils.isNotBlank(il.get(0).getStartUserId())) {
                                User user = identityService.createUserQuery().userId(il.get(0).getStartUserId()).singleResult();
                                e.setAssignee(histIns.getAssignee());
                                e.setAssigneeName(user.getFirstName());
                            }
                        }
                    }
                    // ???????????????????????????
                    if (StringUtils.isNotEmpty(histIns.getAssignee())) {
                        User user = identityService.createUserQuery().userId(histIns.getAssignee()).singleResult();
                        e.setAssignee(histIns.getAssignee());
                        e.setAssigneeName(user.getFirstName());
                    }
                    // ????????????????????????
                    if (StringUtils.isNotBlank(histIns.getTaskId())) {
                        List<Comment> commentList = taskService.getTaskComments(histIns.getTaskId());
                        if (commentList.size() > 0) {
                            e.setComment(commentList.get(0).getFullMessage());
                        }
                    }
                    // ????????????Group?????????????????????????????????
                    if (histIns.getTaskId() != null && histIns.getAssignee() == null) {
                        String groupAssigneeNames = "";
                        String groupAssignees = "";
                        List<HistoricIdentityLink> identityLinkList = historyService.getHistoricIdentityLinksForTask(histIns.getTaskId());
                        for (HistoricIdentityLink identityLink : identityLinkList) {

                            for (User user : identityService.createUserQuery().memberOfGroup(identityLink.getGroupId()).list()) {
                                groupAssignees = groupAssignees + user.getId() + " ";
                                groupAssigneeNames = groupAssigneeNames + user.getFirstName() + " ";
                            }
                        }
                        e.setAssigneeName(groupAssigneeNames);
                        e.setAssignee(groupAssignees);
                    }
                    e.setTaskId(histIns.getTaskId());
                    e.setProcInsId(histIns.getProcessInstanceId());
                    actList.add(e);
                }
            }
        }
        return actList;
    }

    /**
     * ????????????????????????
     * @param procInsId ????????????ID
     * @return
     */
    @Override
    public boolean isEnd(String procInsId) {
        List<Task> list = taskService.createTaskQuery().processInstanceId(procInsId).active().orderByTaskCreateTime().desc().list();
        if (list.size() > 0) {
            return false;
        }
        return true;
    }

    /**
     * ??????????????????
     * @param procDefKey ????????????Key
     * @param bizKey     ????????????ID
     * @param userId     ??????ID
     * @return
     */
    @Override
    public Map processInfo(String procDefKey, String bizKey, String userId) {
        Map map = new HashMap();
        HistoricProcessInstance procIns = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(procDefKey).processInstanceBusinessKey(bizKey).singleResult();
        map.put("procIns", getProcessInstanceState(procIns));
        List buttonList = new ArrayList();
        // =============== ?????????????????????  ===============
        Task todoTask = taskService.createTaskQuery().processDefinitionKey(procDefKey).processInstanceBusinessKey(bizKey).taskAssignee(userId).active().singleResult();
        if (todoTask != null) {
            // 1.????????????????????????????????????form?????? 2.??????????????????
            Map taskProp = getTaskProp(todoTask, userId);
            // Task??????????????????
            Map taskState = getTaskState(todoTask);
            taskState.put("isFormSave", taskProp.get("isFormSave"));
            map.put("task", taskState);
            // Task??????????????????
            buttonList = (List) taskProp.get("buttonList");
        }
        // =============== ?????????????????????  ===============
        Task toClaimTask = taskService.createTaskQuery().processDefinitionKey(procDefKey).processInstanceBusinessKey(bizKey).taskCandidateUser(userId).active().singleResult();
        if (toClaimTask != null) {
            // Task??????????????????
            Map taskState = getTaskState(toClaimTask);
            taskState.put("isFormSave", false);
            map.put("task", taskState);
            // Task??????????????????
            Map button = new HashMap();
            button.put("type", "claim");
            button.put("name", "??????");
            button.put("procInsId", procIns.getId());
            button.put("taskId", toClaimTask.getId());
            button.put("userId", userId);
            buttonList.add(button);
        }
        map.put("buttons", buttonList);
        return map;
    }
    private Map getTaskProp(Task task, String userId) {
        Map map = new HashMap();
        List buttonList = new ArrayList();
        boolean isFormSave = false;
        // ??????BPMN??????
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);
        // ???????????????UserTask
        Collection<UserTask> flowElements = process.findFlowElementsOfType(UserTask.class);
        // ??????UserTask???????????????????????????????????????
        for (UserTask userTask : flowElements) {
            if (userTask.getId().equals(task.getTaskDefinitionKey())) {
                for (FormProperty prop : userTask.getFormProperties()) {
                    if (("save").equals(prop.getId()) || ("agree").equals(prop.getId()) || ("reject").equals(prop.getId()) || ("stop").equals(prop.getId())
                            ||"pend".equals(prop.getId())|| "resolve".equals(prop.getId()) || "feedback".equals(prop.getId()) ) {
                        Map button = new HashMap();
                        button.put("type", prop.getId());
                        button.put("name", prop.getName());
                        button.put("procInsId", task.getProcessInstanceId());
                        button.put("taskId", task.getId());
                        button.put("userId", userId);
                        buttonList.add(button);
                    } else if (("isFormSave").equals(prop.getId())) {
                        if ("true".equals(prop.getName())) {
                            isFormSave = true;
                        }
                    }
                }
                break;
            }
        }
        map.put("isFormSave", isFormSave);
        map.put("buttonList", buttonList);
        return map;
    }

    /**
     * ??????????????????
     * @param processInstance ????????????
     * @return ????????????ID
     */
    public Object getProcessInstanceState(HistoricProcessInstance processInstance) {
        Map<String, Object> persistentState = new HashMap();
        persistentState.put("processDefinitionId", processInstance.getProcessDefinitionId());
        persistentState.put("processDefinitionKey", processInstance.getProcessDefinitionKey());
        persistentState.put("processDefinitionName", processInstance.getProcessDefinitionName());
        persistentState.put("processDefinitionVersion", processInstance.getProcessDefinitionVersion());
        persistentState.put("deploymentId", processInstance.getDeploymentId());
        persistentState.put("processInstanceId", processInstance.getId());
        persistentState.put("variables", processInstance.getProcessVariables());
        persistentState.put("businessKey", processInstance.getBusinessKey());
        persistentState.put("name", processInstance.getName());
        persistentState.put("startTime", processInstance.getStartTime());
        persistentState.put("startUserId", processInstance.getStartUserId());
        persistentState.put("callbackId", processInstance.getCallbackId());
        persistentState.put("callbackType", processInstance.getCallbackType());
        return persistentState;
    }

    /**
     * ????????????
     * @param task ??????
     * @return ????????????ID
     */
    public Map<String, Object> getTaskState(Task task) {
        Map<String, Object> persistentState = new HashMap();
        persistentState.put("assignee", task.getAssignee());
        persistentState.put("id", task.getId());
        persistentState.put("owner", task.getOwner());
        persistentState.put("name", task.getName());
        persistentState.put("priority", task.getPriority());
        persistentState.put("category", task.getCategory());
        persistentState.put("formKey", task.getFormKey());
        persistentState.put("executionId", task.getExecutionId() != null ? task.getExecutionId() : "");
        persistentState.put("processInstanceId", task.getProcessInstanceId() != null ? task.getProcessInstanceId() : "");
        persistentState.put("processDefinitionId", task.getProcessDefinitionId() != null ? task.getProcessDefinitionId() : "");
        persistentState.put("taskDefinitionId", task.getTaskDefinitionId() != null ? task.getTaskDefinitionId() : "");
        persistentState.put("taskDefinitionKey", task.getTaskDefinitionKey() != null ? task.getTaskDefinitionKey() : "");
        persistentState.put("scopeId", task.getScopeId() != null ? task.getScopeId() : "");
        persistentState.put("subScopeId", task.getSubScopeId() != null ? task.getSubScopeId() : "");
        persistentState.put("scopeType", task.getScopeType() != null ? task.getScopeType() : "");
        persistentState.put("scopeDefinitionId", task.getScopeDefinitionId() != null ? task.getScopeDefinitionId() : "");
        persistentState.put("createTime", task.getCreateTime() != null ? task.getCreateTime() : null);
        persistentState.put("description", task.getDescription() != null ? task.getDescription() : "");
        persistentState.put("dueDate", task.getDueDate() != null ? task.getDueDate() : null);
        persistentState.put("parentTaskId", task.getParentTaskId() != null ? task.getParentTaskId() : "");
        persistentState.put("delegationState", task.getDelegationState() != null ? task.getDelegationState() : null);
        persistentState.put("delegationStateString", task.getDelegationState() != null ? task.getDelegationState().toString() : "");
        persistentState.put("claimTime", task.getClaimTime() != null ? task.getClaimTime() : null);
        return persistentState;
    }

    /**
     * ????????????????????????
     * @param map
     * @return
     */
    @Override
    public Map getCurrentTaskNode(Map<String, Object> map) {
        if(map.containsKey("bizKey")){
            String bizKey=String.valueOf(map.get("bizKey"));
            String procDefKey=String.valueOf(map.get("procDefKey"));
            Task task=taskService.createTaskQuery().processDefinitionKey(procDefKey).processInstanceBusinessKey(bizKey).active().singleResult();
            map.put("procInsId",task.getProcessInstanceId());
            map.put("taskId",task.getId());
            map.put("assignee",task.getAssignee());
            return map;
        }else{
           throw new BadRequestException("N:????????????????????????????????????????????????!");
        }
    }
}
