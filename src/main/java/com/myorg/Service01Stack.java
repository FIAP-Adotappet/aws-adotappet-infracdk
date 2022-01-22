package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Fn;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.CpuUtilizationScalingProps;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.ScalableTaskCount;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.logs.LogGroup;
import software.constructs.Construct;

import java.util.HashMap;
import java.util.Map;

public class Service01Stack extends Stack {
    public Service01Stack(final Construct scope, final String id, Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public Service01Stack(final Construct scope, final String id, final StackProps props, Cluster cluster) {
        super(scope, id, props);

        Map<String, String> envVariables = new HashMap<>();
        envVariables.put("APP_PROFILE","prod");
        envVariables.put("SPRING_DATASOURCE_URL", "jdbc:mariadb://"+ Fn.importValue("rds-endpoint") + ":3306/aws_project01?createDatabaseIfNotExist=true");
        envVariables.put("SPRING_DATA_USERNAME", "admin");
        envVariables.put("SPRING_DATA_PASSWORD", Fn.importValue("rds-password"));

        ApplicationLoadBalancedFargateService service01 = ApplicationLoadBalancedFargateService.Builder.create(this, "ALB01")
                .serviceName("service-01")
                .cluster(cluster)
                .cpu(512)
                .memoryLimitMiB(1024)       // 1GB memory executar
                .desiredCount(2)            //Qtde instances do serviço - assim que o serviço é criado, a quantidade de instâncias é criada
                .listenerPort(8080)         //Porta de escuta do serviço
                .publicLoadBalancer(true)   //Criar um load balancer publico
                .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
                        .containerName("aws_project01")
                        .image(ContainerImage.fromRegistry("pcaique/aws-api-adotapet:1.0.0"))  //!imagem do serviço no ECR ou Docker Hub
                        .containerPort(8080)
                        .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .logGroup(LogGroup.Builder.create(this, "Service01LogGroup")
                                        .logGroupName("Service01")
                                        .removalPolicy(RemovalPolicy.DESTROY)
                                        .build())
                                .streamPrefix("Service01")
                                .build()))
                        .environment(envVariables)
                        .build())
                .build();

        //Configura o health check para o serviço
        service01.getTargetGroup().configureHealthCheck(new HealthCheck.Builder()
                .path("/actuator/health")
                .port("8080")
                .healthyHttpCodes("200")
                .build());

        //Configuração do autoscalling - configuracao para escalar o serviço horizontalmente
        ScalableTaskCount scalableTaskCount = service01.getService().autoScaleTaskCount(EnableScalingProps.builder()
                .minCapacity(1) //!minimo de instancias
                .maxCapacity(2) //!maximo de instancias
                .build());

        //Configuração do autoscalling - configuracao dos gatilhos para "startar" o autoscaling
        scalableTaskCount.scaleOnCpuUtilization("Service01AutoScaling", CpuUtilizationScalingProps.builder()
                .targetUtilizationPercent(50)               //!Porcentagem de uso de CPU
                .scaleInCooldown(Duration.seconds(60))      //!Tempo de espera para escalar para cima (em segundos), se o uso de CPU ficar acima de 50% por 60 segundos (1 minuto), deve aumentar a qtde de instancias
                .scaleOutCooldown(Duration.seconds(60))     //!Tempo de espera para escalar para baixo (em segundos), se o uso de CPU ficar abaixo de 50% por 60 segundos (1 minuto), deve reduzir a qtde de instancias
                .build());

    }
}
