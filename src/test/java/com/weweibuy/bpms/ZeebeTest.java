package com.weweibuy.bpms;

import com.weweibuy.framework.common.core.utils.IdWorker;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.*;
import io.camunda.zeebe.client.api.worker.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author durenhao
 * @date 2022/5/28 11:28
 **/
@Slf4j
public class ZeebeTest {

    static final String GATEWAY_URL = "localhost:26500";

    static ZeebeClient zeebeClient =
            ZeebeClient.newClientBuilder()
                    .gatewayAddress(GATEWAY_URL)
                    .usePlaintext()
                    .build();

    @Test
    public void deploy() {
        DeploymentEvent deploymentEvent = zeebeClient.newDeployResourceCommand()
                .addResourceFromClasspath("order_test.bpmn")
                .send()
                .join();
    }

    @Test
    public void newInstance() {
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", IdWorker.nextStringId());

        ProcessInstanceEvent instanceEvent = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("order_test")
                .latestVersion()
                .variables(params)
                .send()
                .join();

        long processInstanceKey = instanceEvent.getProcessInstanceKey();
        System.err.println(processInstanceKey);

    }


    @Test
    public void completeTask() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "tom");

        // 完成任务走向下一个任务节点
        CompleteJobResponse response = zeebeClient.newCompleteCommand(2251799813690986L)
                .variables(params)
                .send()
                .join();
    }

    @Test
    public void worker() throws InterruptedException {

        JobWorker worker = zeebeClient.newWorker()
                .jobType("t1")
                .handler((client, job) -> {
                    log.info("job1: {}", job);
                })
                .name("test-worker1")
                .pollInterval(Duration.ofSeconds(10))
                .open();

        JobWorker worker2 = zeebeClient.newWorker()
                .jobType("rule")
                .handler((client, job) -> {
                    log.info("rule: {}", job);
                })
                .name("test-worker2")
                .pollInterval(Duration.ofSeconds(10))
                .open();


        while (worker.isOpen() && worker2.isOpen()) {
            Thread.sleep(2000);
        }

    }

    @Test
    public void queryTask() {


        ActivateJobsResponse response = zeebeClient.newActivateJobsCommand()
                .jobType("t2")
                .maxJobsToActivate(1000)
                .send()
                .join();

        List<ActivatedJob> jobs = response.getJobs();
        jobs.forEach(j -> {
            log.info("query job: {}", j);
        });
    }

    @Test
    public void testBroker() {
        Topology topology = zeebeClient.newTopologyRequest().send().join();
    }


    @Test
    public void userTask() throws Exception {

        JobWorker worker = zeebeClient.newWorker()
                .jobType("io.camunda.zeebe:userTask")
                .handler((client, job) -> {
                    log.info("userTask: {}", job);
                })
                .name("test-worker2")
                .pollInterval(Duration.ofSeconds(10))
                .open();


        while (worker.isOpen()) {
            Thread.sleep(2000);
        }
    }

}
