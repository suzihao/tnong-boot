package com.tnong.boot.system.job.scheduler;

import com.tnong.boot.system.job.domain.entity.SysJob;
import com.tnong.boot.system.job.domain.entity.SysJobLog;
import com.tnong.boot.system.job.mapper.SysJobLogMapper;
import com.tnong.boot.system.job.mapper.SysJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务调度管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleManager {

    private final TaskScheduler taskScheduler;
    private final SysJobMapper sysJobMapper;
    private final SysJobLogMapper sysJobLogMapper;
    private final JobInvoker jobInvoker;

    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * 启动任务
     */
    public void startJob(SysJob job) {
        if (scheduledTasks.containsKey(job.getId())) {
            log.warn("任务已在运行中: {}", job.getJobName());
            return;
        }

        try {
            ScheduledFuture<?> future = taskScheduler.schedule(
                    () -> executeJob(job),
                    new CronTrigger(job.getCronExpression())
            );
            scheduledTasks.put(job.getId(), future);
            log.info("任务启动成功: {}", job.getJobName());
        } catch (Exception e) {
            log.error("任务启动失败: {}", job.getJobName(), e);
        }
    }

    /**
     * 停止任务
     */
    public void stopJob(Long jobId) {
        ScheduledFuture<?> future = scheduledTasks.remove(jobId);
        if (future != null) {
            future.cancel(false);
            log.info("任务停止成功: jobId={}", jobId);
        }
    }

    /**
     * 重启任务
     */
    public void restartJob(SysJob job) {
        stopJob(job.getId());
        startJob(job);
    }

    /**
     * 执行任务
     */
    private void executeJob(SysJob job) {
        if (job.getConcurrent() == 0) {
            synchronized (this) {
                doExecuteJob(job);
            }
        } else {
            doExecuteJob(job);
        }
    }

    /**
     * 实际执行任务
     */
    private void doExecuteJob(SysJob job) {
        LocalDateTime startTime = LocalDateTime.now();
        SysJobLog jobLog = new SysJobLog();
        jobLog.setTenantId(job.getTenantId());
        jobLog.setJobId(job.getId());
        jobLog.setJobName(job.getJobName());
        jobLog.setJobGroup(job.getJobGroup());
        jobLog.setInvokeTarget(job.getInvokeTarget());
        jobLog.setStartTime(startTime);

        try {
            jobInvoker.invoke(job.getInvokeTarget());
            jobLog.setStatus(1);
            log.info("任务执行成功: {}", job.getJobName());
        } catch (Exception e) {
            jobLog.setStatus(0);
            jobLog.setErrorMsg(e.getMessage());
            log.error("任务执行失败: {}", job.getJobName(), e);
        } finally {
            LocalDateTime endTime = LocalDateTime.now();
            jobLog.setEndTime(endTime);
            jobLog.setCostTime(java.time.Duration.between(startTime, endTime).toMillis());
            sysJobLogMapper.insert(jobLog);

            sysJobMapper.updateLastFireTime(job.getId(), startTime);
        }
    }

    /**
     * 立即执行一次任务
     */
    public void runOnce(SysJob job) {
        new Thread(() -> doExecuteJob(job)).start();
    }
}
