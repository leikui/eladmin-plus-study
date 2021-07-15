package me.zhengjie.modules.flow.service;


import me.zhengjie.modules.flow.domain.Act;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Spur
 * @date 2019-11-27
 */
public interface FlowService {
    String importProcessDefinition(MultipartFile file);
    /**
     * 启动流程
     *
     * @param procDefKey 流程定义Key
     * @param bizKey     业务数据ID
     * @param title      标题
     * @param comment    审批意见
     * @param var        流程变量
     * @return 任务实例ID
     */
     String startProcess(String procDefKey, String bizKey, String title, String comment, String appUser, Map<String, Object> var);

    /**
     * 接收任务
     *
     * @param taskId 任务实例ID
     * @param userId 接收人ID
     */
    void claim(String taskId, String userId);

    /**
     * 审批任务
     *
     * @param procInsId 流程实例ID
     * @param taskId    任务实例ID
     * @param title     标题
     * @param comment   审批意见
     * @param var       流程变量
     */
    void complete(String procInsId, String taskId, String title, String comment, Map<String, Object> var);

    /**
     * 审批任务
     *
     * @param procInsId 流程实例ID
     * @param taskId    任务实例ID
     * @param comment   审批意见
     * @param var       流程变量
     */
     void complete(String procInsId, String taskId, String comment, Map<String, Object> var);

    /**
     * 跳转指定任务
     *
     * @param procInsId    流程实例ID
     * @param taskId       任务实例ID
     * @param comment      审批意见
     * @param targetNodeId 跳转指定节点ID(为空时获取前一节点nodeId)
     * @param var          流程变量
     */
     void jump(String procInsId, String taskId, String comment, String targetNodeId, Map<String, Object> var);

    /**
     * 沟通
     *
     * @param taskId  任务实例ID
     * @param procInsId 流程实例ID
     * @param userId  被沟通人ID
     * @param comment 审批意见
     */
     void delegate(String taskId,String procInsId, String userId, String comment);

    /**
     * 任务还回
     *
     * @param taskId  任务实例ID
     * @param procInsId 流程实例ID
     * @param comment 审批意见
     */
     void resolve(String taskId, String procInsId,String comment);

    /**
     * 作废流程
     *
     * @param procInsId    流程实例ID
     * @param deleteReason 作废原因
     */
     void stop(String procInsId,String taskId, String deleteReason);

    /**
     * 获取流程图
     *
     * @param procDefKey 流程定义ID
     * @param bizKey 业务ID
     */
     String printProcessImage(String procDefKey, String bizKey) throws IOException;

    /**
     * 获取流程待办
     *
     * @return
     */
     List<Act> getTodoList(Map<String,String> map);

    /**
     * 获流程流转记录
     *
     * @param procDefKey 流程定义ID
     * @param bizKey 业务ID
     * @return List<TaskVo>
     */
     List<Act> getTaskList(String procDefKey, String bizKey);

    /**
     * 判断流程是否结束
     * @param procInsId 流程实例ID
     * @return
     */
     boolean isEnd(String procInsId);

    /**
     * 业务流程信息
     *
     * @param procDefKey 流程定义Key
     * @param bizKey     业务数据ID
     * @param userId     用户ID
     * @return 流程信息
     */
     Map processInfo(String procDefKey, String bizKey, String userId);
    /**
     * 获取当前任务节点
     * @param map
     * @return
     */
     Map getCurrentTaskNode(Map<String,Object> map);
}
