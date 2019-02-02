Digital Ocean Jenkins Library
=============================

[Jenkins pipeline library](https://jenkins.io/doc/book/pipeline/shared-libraries/) for using [Digital Ocean](https://www.digitalocean.com/).

It provides some tasks to interact with Digital Ocean services like Kubernetes clusters, load balancers, etc.

> It's a very early version. While provided steps *do* work, only very few features are covered.

## Table of contents

* [Examples](#examples)
* [Contributing](#contributing)

## Examples

### Creating a cluster for running some tests

Given a `deployment.yaml` in the workspace, one can:

1. create a K8S cluster in Digital Ocean
1. apply the deployment in this cluster
1. wait for the service(s) to be available
1. run some tests
1. tear down the deployment
1. remove the K8S cluster

This is achieved by running the following code in your `Jenkinsfile`:

```groovy
withDigitalOceanK8SCluster(
        logging: true,
        credentials: "MY_DO_CREDENTIALS",
        name: "jenkins-${env.BUILD_NUMBER}",
        region: "ams3",
        version: "1.13.1-do.2",
        pools: [[
          name : "jenkins-${env.BUILD_NUMBER}"
          count: 2,
          size : "s-1vcpu-2gb"
        ]]
) { cluster ->
    withDeployment(file: "deployment.yaml") {
        waitForDigitalOceanLoadBalancer(
                service: "my-service",
                outputVariable: "MY_SERVICE_IP",
                logging: true,
        )
        echo "Service IP = ${env.MY_SERVICE_IP}"
        // Runs the tests against load balancer at MY_SERVICE_IP
    }
    // Here, the deployment has been deleted
}
// Here, the cluster has been destroyed
```

## Steps

### Digital Ocean specific tasks

* [`withDigitalOceanK8SCluster`](vars/withDigitalOceanK8SCluster.md) - creates a [Digital Ocean Kubernetes cluster](https://www.digitalocean.com/products/kubernetes/) and allows some code to run against it.
* [`waitForDigitalOceanLoadBalancer`](vars/waitForDigitalOceanLoadBalancer.md) - waits for a [Digital Ocean load balancer](https://www.digitalocean.com/products/load-balancer/) created by a service to be ready.

### K8S steps

* [`withDeployment`](vars/withDeployment.md) - deploying (and undeploying) a K8S file

## Authentication

## Installation

## Prerequisites

## Contributing
