package com.opencloud.task.provider.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import com.opencloud.task.client.model.TaskInfo;
import com.opencloud.task.client.model.entity.SchedulerJobLogs;
import com.opencloud.task.provider.job.HttpExecuteJob;
import com.opencloud.task.provider.service.SchedulerJobLogsService;
import com.opencloud.task.provider.service.SchedulerService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author: liuyadu
 * @date: 2019/3/29 14:12
 * @description:
 */
@RestController
public class SchedulerController {
    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private SchedulerJobLogsService schedulerJobLogsService;

    /**
     * 获取任务执行日志列表
     *
     * @param map
     * @return
     */
    @ApiOperation(value = "获取任务执行日志列表", notes = "获取任务执行日志列表")
    @GetMapping(value = "/job/logs")
    public ResultBody<IPage<SchedulerJobLogs>> getJobLogList(@RequestParam(required = false) Map map) {
        IPage<SchedulerJobLogs> result = schedulerJobLogsService.findListPage(new PageParams(map));
        return ResultBody.ok().data(result);
    }

    /**
     * 获取任务列表
     *
     * @return
     */
    @ApiOperation(value = "获取任务列表", notes = "获取任务列表")
    @GetMapping(value = "/job")
    public ResultBody<IPage<TaskInfo>> getJobList(@RequestParam(required = false) Map map) {
        List<TaskInfo> list = schedulerService.getJobList();
        IPage page = new Page();
        page.setRecords(list);
        page.setTotal(list.size());
        return ResultBody.ok().data(page);
    }

    /**
     * 添加远程调度任务
     *
     * @param jobName        任务名称
     * @param jobDescription 任务描述
     * @param cron           cron表达式
     * @param serviceId      服务名
     * @param path           请求路径
     * @param method         请求类型
     * @param contentType    响应类型
     * @param alarmMail      告警邮箱
     * @return
     */
    @ApiOperation(value = "添加远程调度任务", notes = "添加远程调度任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form"),
            @ApiImplicitParam(name = "jobDescription", value = "任务描述", required = true, paramType = "form"),
            @ApiImplicitParam(name = "cron", value = "cron表达式", required = true, paramType = "form"),
            @ApiImplicitParam(name = "serviceId", value = "服务名", required = true, paramType = "form"),
            @ApiImplicitParam(name = "path", value = "请求路径", required = true, paramType = "form"),
            @ApiImplicitParam(name = "method", value = "请求类型", required = false, paramType = "form"),
            @ApiImplicitParam(name = "contentType", value = "响应类型", required = false, paramType = "form"),
            @ApiImplicitParam(name = "alarmMail", value = "告警邮箱", required = false, paramType = "form"),
    })
    @PostMapping("/job/add/http")
    public ResultBody addHttpJob(@RequestParam(name = "jobName") String jobName,
                                 @RequestParam(name = "jobDescription") String jobDescription,
                                 @RequestParam(name = "cron") String cron,
                                 @RequestParam(name = "serviceId") String serviceId,
                                 @RequestParam(name = "path") String path,
                                 @RequestParam(name = "method", required = false) String method,
                                 @RequestParam(name = "contentType", required = false) String contentType,
                                 @RequestParam(name = "alarmMail", required = false) String alarmMail) {
        TaskInfo taskInfo = new TaskInfo();
        Map data = Maps.newHashMap();
        data.put("serviceId", serviceId);
        data.put("method", method);
        data.put("path", path);
        data.put("contentType", contentType);
        data.put("alarmMail", alarmMail);
        taskInfo.setData(data);
        taskInfo.setJobName(jobName);
        taskInfo.setJobDescription(jobDescription);
        taskInfo.setJobClassName(HttpExecuteJob.class.getName());
        taskInfo.setJobGroupName(Scheduler.DEFAULT_GROUP);
        taskInfo.setCronExpression(cron);
        schedulerService.addCronJob(taskInfo);
        return ResultBody.ok();
    }

    /**
     * 修改远程调度任务
     *
     * @param jobName        任务名称
     * @param jobDescription 任务描述
     * @param cron           cron表达式
     * @param serviceId      服务名
     * @param path           请求路径
     * @param method         请求类型
     * @param contentType    响应类型
     * @param alarmMail      告警邮箱
     * @return
     */
    @ApiOperation(value = "修改远程调度任务", notes = "修改远程调度任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form"),
            @ApiImplicitParam(name = "jobDescription", value = "任务描述", required = true, paramType = "form"),
            @ApiImplicitParam(name = "cron", value = "cron表达式", required = true, paramType = "form"),
            @ApiImplicitParam(name = "serviceId", value = "服务名", required = true, paramType = "form"),
            @ApiImplicitParam(name = "path", value = "请求路径", required = true, paramType = "form"),
            @ApiImplicitParam(name = "method", value = "请求类型", required = false, paramType = "form"),
            @ApiImplicitParam(name = "contentType", value = "响应类型", required = false, paramType = "form"),
            @ApiImplicitParam(name = "alarmMail", value = "告警邮箱", required = false, paramType = "form"),
    })
    @PostMapping("/job/update/http")
    public ResultBody updateHttpJob(@RequestParam(name = "jobName") String jobName,
                                    @RequestParam(name = "jobDescription") String jobDescription,
                                    @RequestParam(name = "cron") String cron,
                                    @RequestParam(name = "serviceId") String serviceId,
                                    @RequestParam(name = "path") String path,
                                    @RequestParam(name = "method", required = false) String method,
                                    @RequestParam(name = "contentType", required = false) String contentType,
                                    @RequestParam(name = "alarmMail", required = false) String alarmMail) {
        TaskInfo taskInfo = new TaskInfo();
        Map data = Maps.newHashMap();
        data.put("serviceId", serviceId);
        data.put("method", method);
        data.put("path", path);
        data.put("contentType", contentType);
        data.put("alarmMail", alarmMail);
        taskInfo.setData(data);
        taskInfo.setJobName(jobName);
        taskInfo.setJobDescription(jobDescription);
        taskInfo.setJobClassName(HttpExecuteJob.class.getName());
        taskInfo.setJobGroupName(Scheduler.DEFAULT_GROUP);
        taskInfo.setCronExpression(cron);
        schedulerService.editCronJob(taskInfo);
        return ResultBody.ok();
    }


    /**
     * 删除任务
     *
     * @param jobName 任务名称
     * @return
     */
    @ApiOperation(value = "删除任务", notes = "删除任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form")
    })
    @PostMapping("/job/delete")
    public ResultBody deleteJob(@RequestParam(name = "jobName") String jobName) {
        schedulerService.deleteJob(jobName, Scheduler.DEFAULT_GROUP);
        return ResultBody.ok();
    }

    /**
     * 暂停任务
     *
     * @param jobName 任务名称
     * @return
     */
    @ApiOperation(value = "暂停任务", notes = "暂停任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form")
    })
    @PostMapping("/job/pause")
    public ResultBody pauseJob(@RequestParam(name = "jobName") String jobName) {
        schedulerService.pauseJob(jobName, Scheduler.DEFAULT_GROUP);
        return ResultBody.ok();
    }


    /**
     * 恢复任务
     *
     * @param jobName 任务名称
     * @return
     */
    @ApiOperation(value = "恢复任务", notes = "恢复任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form")
    })
    @PostMapping("/job/resume")
    public ResultBody resumeJob(@RequestParam(name = "jobName") String jobName) {
        schedulerService.resumeJob(jobName, Scheduler.DEFAULT_GROUP);
        return ResultBody.ok();
    }
}
