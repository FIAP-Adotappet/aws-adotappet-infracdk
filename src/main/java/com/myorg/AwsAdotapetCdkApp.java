package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Arrays;

public class AwsAdotapetCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        //Criação da Stack da VPC (virtual private cloud)
        VpcStack vpcStack = new VpcStack(app, "Vpc01AwsCdk");

        //Criação do Cluster ECS (Elastic Container Service)
        ClusterStack clusterStack = new ClusterStack(app, "Cluster01AwsCdk", vpcStack.getVpc());
        clusterStack.addDependency(vpcStack); //?dependencia do clusterStack para o vpcStack

        //Criação da Stack do Banco de dados relacional(RDS)
        RdsStack rdsStack = new RdsStack(app, "Rds01AwsCdk", vpcStack.getVpc());
        rdsStack.addDependency(vpcStack);

        //Criação da stack do load balancer (Elastic Load Balancer) que carregara o cluster ECS com a aplicação java Spring Boot
        Service01Stack service01Stack = new Service01Stack(app, "Service01AwsCdk", clusterStack.getCluster());
        service01Stack.addDependency(clusterStack);
        service01Stack.addDependency(rdsStack);

        app.synth();
    }
}

