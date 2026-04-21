package com.aml.srv.core.efrmsrv.ruleengine.serive;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.aml.srv.core.efrm.parquet.entity.TransactionParquetMppaing;
import com.aml.srv.core.efrmsrv.entity.NormalizedTblEntity;
import com.aml.srv.core.efrmsrv.ruleengine.RulewhizConfig;
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;
import com.google.gson.Gson;

import jakarta.annotation.PreDestroy;

@EnableAsync(proxyTargetClass=true)
@EnableCaching
@Configuration
@EnableKafka
public class RuleExecutorService {

	private Logger LOGGER = LoggerFactory.getLogger(RuleExecutorService.class);
	
	private static final Gson GSON = new Gson();
	
	@Autowired
	private RulewhizConfig appConfig;
	
	@Value("${aml.rule.thread.executor.core.pool.size:32}")
	private Integer corePoolSize;
	
	@Value("${aml.rule.thread.executor.max.pool.size:128}")
	private Integer maxPoolSize;
	
	@Value("${aml.rule.thread.executor.queue.capacity:2000}")
	private Integer queueCapacity;
	

	@Value("${aml.rule.thread.executor.keep.alive.seconds:120}")
	private Integer keepAliveSeconds;
	
	@Value("${aml.rule.thread.executor.wait.terminate.seconds:60}")
	private Integer waitTerminatSeconds;
	
	@Value("${aml.rule.thread.executor.complete.shutdown:true}")
	private boolean complete2Shutdown;
	
	
	@Autowired
	@Qualifier("kafkaTaskExecutor")
	private TaskExecutor taskExecutor;
	
	List<ConcurrentMessageListenerContainer<String, String>> containers = null;
	String clazzName = RuleExecutorService.class.getSimpleName();

    @Bean(name = "RuleEngineExecutor", destroyMethod = "shutdown")
    public Executor ruleEngineThrdExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);          // baseline threads
        executor.setMaxPoolSize(maxPoolSize);           // burst capacity
        executor.setQueueCapacity(queueCapacity);        // queue before spawning new threads
        executor.setKeepAliveSeconds(keepAliveSeconds);      // idle timeout
        executor.setThreadNamePrefix("RuleEngineExec-");
        executor.setWaitForTasksToCompleteOnShutdown(complete2Shutdown);
        executor.setAwaitTerminationSeconds(waitTerminatSeconds);
        executor.setAllowCoreThreadTimeOut(false);
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "kafkaTaskExecutor", destroyMethod = "shutdown")
    public TaskExecutor kafkaTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
       // executor.setThreadNamePrefix("kafka-worker-");    // ← this controls thread name
        executor.setThreadGroupName("kafka-group");        // optional
        ThreadFactory factory = r -> new Thread(r, "kafka-worker-" + new Date().getTime());
        executor.setThreadFactory(factory);
        executor.initialize();
        return executor;
    }

    @Bean
    public List<ConcurrentMessageListenerContainer<String, String>> listenerContainers(
            ConcurrentKafkaListenerContainerFactory<String, String> factory, ProcessEventsService myService) {
    	LOGGER.info("Dynamic Kafak Lister Creation Method Called............");
    	String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		//stopListeners();
		try {
			containers = new ArrayList<>();
			Integer ruleCount = appConfig.ruleEntity.size();
			LOGGER.info("Dynamic Kafak Lister Creation rouleCount [{}] : ", ruleCount);
			for (int i = 0; i < ruleCount; i++) {
				final int groupId = i; // make a final copy
				NormalizedTblEntity ruleEntity = appConfig.ruleEntity.get(i);
				ConcurrentMessageListenerContainer<String, String> container = factory.createContainer(RuleWhizConstants.KAFKA_PUB_TOPIC_DYNAMIC);
				container.getContainerProperties().setGroupId(RuleWhizConstants.KAFKA_PUB_TOPIC_GRP + appConfig.ruleEntity.get(groupId).getId());
				container.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
				container.getContainerProperties().setAsyncAcks(false);
				container.setConcurrency(3);
				/*container.setupMessageListener((AcknowledgingMessageListener<String, String>) (record, acknowledgment) -> {
					myService.processEvent(new Gson().fromJson(record.value(), TransactionParquetMppaing.class),
							RuleWhizConstants.KAFKA_PUB_TOPIC_GRP + appConfig.ruleEntity.get(groupId).getId(), ruleEntity);
					acknowledgment.acknowledge();
						LOGGER.info("Group: [{}]  acknowledgment completed...........\n", appConfig.ruleEntity.get(groupId).getId());
				});*/
				container.setupMessageListener((AcknowledgingMessageListener<String, String>) (record, acknowledgment) -> {
				    taskExecutor.execute(() -> {
				        try {
				            TransactionParquetMppaing payload =  GSON.fromJson(record.value(), TransactionParquetMppaing.class);
				            myService.processEvent(
				                payload,
				                RuleWhizConstants.KAFKA_PUB_TOPIC_GRP + appConfig.ruleEntity.get(groupId).getId(),
				                ruleEntity
				            );
							if (acknowledgment != null) {
								acknowledgment.acknowledge();
							}
				            LOGGER.info("Group: [{}]  acknowledgment completed...........\n", appConfig.ruleEntity.get(groupId).getId());
				        } catch (Exception e) {
				            LOGGER.error("Failed to process Kafka message for groupId {} - {}", groupId, e);
				        }
				    });
				});
				container.getContainerProperties().setShutdownTimeout(30000L);
				container.start();
				containers.add(container);
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in {}@{} : {}", clazzName, methodName, e);
		} finally {
			LOGGER.info("Dynamic Kafak Lister Creation Method End............\n\n");
		}
		return containers;
	}
  
    @PreDestroy
    public void destroy() {
        stopListeners();
    }

	public void stopListeners() {
//		if (containers != null) {
//			LOGGER.info("stopListeners Called...........");
//			containers.forEach(ConcurrentMessageListenerContainer::stop);
//		}
		LOGGER.info("stopListeners Called...........");
		
	    if (containers != null && !containers.isEmpty()) {
	        containers.forEach(container -> {
	            if (container.isRunning()) {
	                LOGGER.info("Stopping container with groupId [{}]...", container.getContainerProperties().getGroupId());
	                container.stop();
	            } else {
	                LOGGER.debug("Container for groupId [{}] is already stopped.",  container.getContainerProperties().getGroupId());
	            }
	        });
	        containers.clear();
	    } else {
	        LOGGER.debug("No containers to stop (containers is null or empty).");
	    }
	}
}