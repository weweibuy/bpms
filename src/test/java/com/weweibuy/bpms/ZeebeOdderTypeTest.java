package com.weweibuy.bpms;

import com.weweibuy.framework.common.core.utils.IdWorker;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.*;
import io.camunda.zeebe.client.api.worker.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author durenhao
 * @date 2022/5/28 11:28
 **/
@Slf4j
public class ZeebeOdderTypeTest {

    static final String GATEWAY_URL = "localhost:26500";

    static ZeebeClient zeebeClient =
            ZeebeClient.newClientBuilder()
                    .gatewayAddress(GATEWAY_URL)
                    .usePlaintext()
                    .build();

    @Test
    public void deploy() {
        DeploymentEvent deploymentEvent = zeebeClient.newDeployResourceCommand()
                .addResourceFromClasspath("order_type.bpmn")
                .send()
                .join();
    }

    @Test
    public void newInstance() {
        Map<String, Object> params = new HashMap<>();
        params.put("productNo", IdWorker.nextStringId());
        params.put("qty", 10);


        ProcessInstanceEvent instanceEvent = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("order_type")
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
        params.put("orderId", IdWorker.nextStringId());
        params.put("type", "B2C");

        // 完成任务走向下一个任务节点
        CompleteJobResponse response = zeebeClient.newCompleteCommand(2251799813690287L)
                .variables(params)
                .send()
                .join();
    }

    @Test
    public void worker() throws InterruptedException {

        JobWorker worker = zeebeClient.newWorker()
                .jobType("order_find_inventory")
                .handler((client, job) -> {
                    log.info("job1: {}", job);
                })
                .name("test-worker1")
                .pollInterval(Duration.ofSeconds(10))
                .open();

        JobWorker worker2 = zeebeClient.newWorker()
                .jobType("order_take_inventory")
                .handler((client, job) -> {
                    log.info("job2: {}", job);
                })
                .name("test-worker2")
                .pollInterval(Duration.ofSeconds(10))
                .open();

        JobWorker worker3 = zeebeClient.newWorker()
                .jobType("order_account_in")
                .handler((client, job) -> {
                    log.info("job3: {}", job);
                })
                .name("test-worker3")
                .pollInterval(Duration.ofSeconds(10))
                .open();
        JobWorker worker4 = zeebeClient.newWorker()
                .jobType("create_order")
                .handler((client, job) -> {
                    log.info("job3: {}", job);
                })
                .name("test-worker3")
                .pollInterval(Duration.ofSeconds(10))
                .open();

        JobWorker worker5 = zeebeClient.newWorker()
                .jobType("order_find_inventory_rule")
                .handler((client, job) -> {
                    log.info("job5: {}", job);
                })
                .name("test-worker3")
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

}
