Digital Ocean Jenkins Library
=============================

[Jenkins pipeline library](https://jenkins.io/doc/book/pipeline/shared-libraries/) for using [Digital Ocean](https://www.digitalocean.com/).

It provides some tasks to interact with Digital Ocean services like Kubernetes clusters, load balancers, etc.

> It's a very early version. While provided steps *do* work, only very few features are covered.

## Steps

### Digital Ocean specific tasks

* [`withDigitalOceanK8SCluster`](vars/withDigitalOceanK8SCluster.md) - creates a [Digital Ocean Kubernetes cluster](https://www.digitalocean.com/products/kubernetes/) and allows some code to run against it.

### K8S steps

* [`withDeployment`](vars/withDeployment.md) - deploying (and undeploying) a K8S file

## Authentication

## Installation

## Prerequisites

## Contributing
