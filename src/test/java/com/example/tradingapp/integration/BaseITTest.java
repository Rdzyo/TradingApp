package com.example.tradingapp.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class BaseITTest {

    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.14.0"))
            .withCopyFileToContainer(MountableFile.forClasspathResource("tables.sh", 0744), "/etc/localstack/init/ready.d/tables.sh")
            .withServices(LocalStackContainer.Service.DYNAMODB)
            .waitingFor(Wait.forLogMessage(".*Executed tables.sh script.*", 1));

    @BeforeAll
    static void beforeAll() {
        localStackContainer.start();
    }

    @AfterAll
    static void afterAll() {
        localStackContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("AWS_ACCESS_KEY_ID", localStackContainer::getAccessKey);
        registry.add("AWS_SECRET_ACCESS_KEY", localStackContainer::getSecretKey);
        registry.add("AWS_REGION", localStackContainer::getRegion);
        registry.add("dynamodb.local.url", localStackContainer::getEndpoint);
    }
}
