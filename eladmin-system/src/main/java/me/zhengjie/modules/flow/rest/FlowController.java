package me.zhengjie.modules.flow.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.zhengjie.modules.flow.domain.Act;
import me.zhengjie.modules.flow.domain.FlowRequestBean;
import me.zhengjie.modules.flow.service.FlowService;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Spur
 * @date 2019-11-27
 */
@RestController
@RequestMapping(value = "api/flow")
@Api(value = "flow",description = "流程引擎Controller")
public class FlowController {
    @Autowired
    private FlowService flowService;

    /**
     * 启动流程
     * @param flowRequestBean
     * @return
     */
    @PostMapping(value = "/start")
    @ApiOperation(value = "启动工作流")
    public ResponseEntity start(@RequestBody FlowRequestBean flowRequestBean){
        // 流程定义key
        String procDefKey= flowRequestBean.getProcDefKey();
        // 业务ID
        String bizKey= flowRequestBean.getBizKey();
        // 意见
        String comment= flowRequestBean.getComment();
        // 标题
        String title= flowRequestBean.getTitle();
        // 参数
        Map map= flowRequestBean.getMap();
        //流程启动人
        String applyUser= flowRequestBean.getApplyUser();
        return new ResponseEntity(flowService.startProcess(procDefKey,bizKey,title,comment,applyUser,map),HttpStatus.OK);
    }
    @GetMapping(value = "/claim/{taskId}/{userId}")
    @ApiOperation(value = "签收任务")
    public ResponseEntity claim(@PathVariable String taskId, @PathVariable String userId) {
        flowService.claim(taskId, userId);
        return new ResponseEntity(HttpStatus.OK);
    }
    /**
     * 同意
     * @param flowRequestBean
     * @return
     */
    @PostMapping(value = "/agree")
    @ApiOperation(value = "审批任务")
    public ResponseEntity agree(@RequestBody FlowRequestBean flowRequestBean){
        String comment="同意:"+(StringUtils.isNoneBlank(flowRequestBean.getComment())?flowRequestBean.getComment():"");
        String procInsId = flowRequestBean.getProcInsId();
        String taskId = flowRequestBean.getTaskId();
        Map<String, Object> map = flowRequestBean.getMap();
        if (map != null) {
            map.put("pass", true);
        } else {
            map = new HashMap<>();
            map.put("pass", true);
        }
        flowService.complete(procInsId,taskId,comment,map);
        return new ResponseEntity(HttpStatus.OK);
    }
    /**
     * 驳回
     */
    @PostMapping(value = "/reject")
    @ApiOperation(value = "驳回任务")
    public ResponseEntity reject(@RequestBody FlowRequestBean flowRequestBean){
        String comment="驳回:"+(StringUtils.isNoneBlank(flowRequestBean.getComment())?flowRequestBean.getComment():"");
        String procInsId = flowRequestBean.getProcInsId();
        String taskId = flowRequestBean.getTaskId();
        Map<String, Object> map = flowRequestBean.getMap();
        if (map != null) {
            map.put("pass", false);
        } else {
            map = new HashMap<>();
            map.put("pass", false);
        }
        flowService.complete(procInsId,taskId,comment,map);
        return new ResponseEntity(HttpStatus.OK);
    }
    /**
     * 终止流程
     */
    @PostMapping(value = "/stop")
    @ApiOperation(value = "终止流程")
    public ResponseEntity stop(@RequestBody FlowRequestBean flowRequestBean){
        String comment = "终止"+(org.apache.commons.lang3.StringUtils.isNotBlank(flowRequestBean.getComment())?flowRequestBean.getComment():"");
        String procInsId = flowRequestBean.getProcInsId();
        String taskId = flowRequestBean.getTaskId();
        flowService.stop(procInsId,taskId,comment);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 获取待办列表
     */
    @GetMapping(value = "/todo")
    @ApiOperation(value = "获取流程待办")
    public List<Act> todo(@RequestParam Map<String,String> map) {
        return flowService.getTodoList(map);
    }
    /**
     * 获取流程图
     */
    @GetMapping("/processImage/{procDefKey}/{bizKey}")
    @ApiOperation(value = "获取流程图")
    public String showProcess(@PathVariable String procDefKey, @PathVariable String bizKey) {
        try {
            return flowService.printProcessImage(procDefKey, bizKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取流转信息
     */
    @GetMapping("/historyFlow/{procDefKey}/{bizKey}")
    @ApiOperation(value = "获取流程流转信息")
    public List<Act> historyFlow(@PathVariable String procDefKey, @PathVariable String bizKey) {
        List<Act> historyFlowList = flowService.getTaskList(procDefKey, bizKey);
        return historyFlowList;
    }
    /**
     * 获取流程信息
     */
    @PostMapping(value = "/processInfo")
    @ApiOperation(value = "获取流程信息")
    public ResponseEntity process(@RequestBody FlowRequestBean flowRequestBean) {

        String procDefKey = flowRequestBean.getProcDefKey();
        String bizKey = flowRequestBean.getBizKey();
        String userId = flowRequestBean.getUserId();
        return new ResponseEntity(flowService.processInfo(procDefKey, bizKey, userId),HttpStatus.OK);
    }

    /**
     * 获取当前节点
     * @param params
     * @return
     */
    @GetMapping(value = "/getCurrentTaskNode")
    @ApiOperation(value = "获取当前流程节点")
    public Map getCurrentTaskNode(@RequestParam Map<String,Object> params){
        return this.flowService.getCurrentTaskNode(params);
    }
}
