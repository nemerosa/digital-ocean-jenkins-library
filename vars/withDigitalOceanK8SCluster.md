## `withDigitalOceanK8SCluster`

This step creates a [Digital Ocean Kubernetes cluster](https://www.digitalocean.com/products/kubernetes/) and allows some code to run against it. The cluster is optionally destroyed at the end of the step.

### Parameter

| Parameter | Type | Default | Description |
|---|---|---|---|
| logging | boolean | `false` | Logs the operations of creation |
| verbose | boolean | `false` | Logs, with many details, the operations of creation - note that this flag is taken into account only if `logging` is set to `true` |
| destroy | boolean | `true` | By default, the K8S cluster is destroyed after the execution of this step. This can be prevented by setting this parameter to `false`. |
| credentials | String | _Required_ | ID of the Jenkins Credentials which contain the API token to authenticate in Digital Ocean |
| name | String | _Required_ | Name of the cluster to create |
| region (*) | String | _Required_ | Digital Ocean region where to create the cluster (`ams3` for example) |
| version (*) | String | `1.13.1-do.2` | Version of Kubernetes to create |
| tags | Array of String | `[]` | List of tags to associate with the cluster |
| configFile | String | `.kubeconfig` | Path, relative to the workspace or the current directory, where to output the Kubernetes config file needed to connect to the Cluster |
| url | String | `https://api.digitalocean.com/v2/kubernetes/clusters` | Digital Ocean API end point |
| retries | int | `20` | Number of times to retry when waiting for the cluster to be ready |
| interval | int | `30` | Number of seconds to wait when waiting for the cluster to be ready |
| pools | Array of `K8SPool` | _Required_ | Definition of node pools (see below) |

Each `K8SPool` object defines a set of nodes for the K8S cluster.

| Parameter | Type | Default | Description |
|---|---|---|---|
| name | String | _Required_ | Name of the pool |
| count | int | _Required_ | Number of nodes in the pool |
| size | String | _Required_ | Size of each node in this pool |

(*) the `region`, `version` and `size` parameters are listed using the [Digital Ocean API](https://developers.digitalocean.com/documentation/v2/#list-available-regions--node-sizes--and-versions-of-kubernetes).

### Body

The `withDigitalOceanK8SCluster` step body is closure having a parameter with following fields:

* `id` - ID of the Digital Ocean cluster
* `params` - parameters of the step

Entering the body, the `kubectl` environment is configured to use the K8S config file defined by the `configFile` parameter.

### Example

```groovy
withDigitalOceanK8SCluster(
  logging: true,
  verbose: true,
  destroy: true,
  credentials: "DO_K8S_CREDENTIALS_ID",
  name: "jenkins-${env.BUILD_NUMBER}",
  region: "ams3",
  version: "1.13.1-do.2",
  tags: [
    "jenkins",
  ],
  pools: [[
   name : "jenkins-${env.BUILD_NUMBER}",
   count: 2,
   size : "s-1vcpu-2gb"
  ]]
) { cluster ->
   echo "K8S Cluster ID = ${cluster.id}"
}
```
