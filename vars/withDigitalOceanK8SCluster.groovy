import net.nemerosa.jenkins.pipeline.digitalocean.JsonUtils
import net.nemerosa.jenkins.pipeline.digitalocean.ParamUtils
import net.nemerosa.jenkins.pipeline.digitalocean.k8s.K8SCluster
import net.nemerosa.jenkins.pipeline.digitalocean.k8s.K8SPool

def call(Map<String, ?> params, Closure body) {

    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    boolean verbose = ParamUtils.getBooleanParam(params, "verbose", false)
    boolean destroy = ParamUtils.getBooleanParam(params, "destroy", true)
    String credentials = ParamUtils.getParam(params, "credentials")
    String name = ParamUtils.getParam(params, "name")
    String region = ParamUtils.getParam(params, "region")
    String version = ParamUtils.getParam(params, "version", "1.13.1-do.2")

    List<String> tags = params.tags.collect { it as String } ?: []

    String configFile = ParamUtils.getParam(params, "configFile", ".kubeconfig")

    String url = "https://api.digitalocean.com/v2/kubernetes/clusters"

    int retries = ParamUtils.getIntParam(params, "retries", 20)
    int interval = ParamUtils.getIntParam(params, "interval", 30)

    List<K8SPool> pools = []
    def poolDefs = params.pools
    if (poolDefs && poolDefs instanceof Collection) {
        poolDefs.each { poolDef ->
            def pool = new K8SPool(
                    ParamUtils.getParam(poolDef as Map<String, ?>, "name"),
                    ParamUtils.getIntParam(poolDef as Map<String, ?>, "count"),
                    ParamUtils.getParam(poolDef as Map<String, ?>, "size"),
            )
            pools.add(pool)
        }
    } else {
        throw new IllegalArgumentException("The `pools` parameter must be present and be a collection.")
    }

    if (logging) {
        echo "DO K8S Cluster - name: $name"
        echo "DO K8S Cluster - region: $region"
        echo "DO K8S Cluster - version: $version"
        echo "DO K8S Cluster - destroy: $destroy"
        echo "DO K8S Cluster - tags: $tags"
        if (verbose) {
            pools.each { pool ->
                echo "DO K8S Cluster Pool - name: $pool.name"
                echo "DO K8S Cluster Pool - count: $pool.count"
                echo "DO K8S Cluster Pool - size: $pool.size"
            }
        }
    }

    // Authentication
    withCredentials([string(credentialsId: credentials, variable: 'TOKEN')]) {

        // Creating the cluster
        if (logging && verbose) {
            echo "DO K8S Cluster - creation..."
        }
        def clusterCreationRequest = JsonUtils.toJsonString([
                name      : name,
                region    : region,
                version   : version,
                tags      : tags,
                node_pools: pools.collect { pool ->
                    [
                            name : pool.name,
                            count: pool.count,
                            size : pool.size,
                    ]
                }
        ])
        if (logging && verbose) {
            echo "DO K8S Cluster - request: $clusterCreationRequest"
        }
        def clusterCreationResponse = httpRequest(
                url: "$url",
                acceptType: "APPLICATION_JSON",
                customHeaders: [[
                                        name     : "Authorization",
                                        value    : "Bearer ${TOKEN}",
                                        maskValue: true,
                                ]],
                httpMode: "POST",
                contentType: "APPLICATION_JSON",
                requestBody: clusterCreationRequest,
        )
        def clusterCreation = readJSON(text: clusterCreationResponse.content)
        def clusterId = clusterCreation.kubernetes_cluster.id as String
        if (logging) {
            echo "DO K8S Cluster - id: $clusterId"
        }

        try {

            // Waiting for the cluster to be ready

            String status = ""
            int tries = 0
            while (status != "running" && tries < retries) {
                tries++
                if (logging && verbose) {
                    echo "DO K8S Cluster - ($tries/$retries) waiting $interval seconds for running cluster..."
                }
                //noinspection GroovyAssignabilityCheck
                sleep(time: interval, unit: 'SECONDS')
                def clusterStatusResponse = httpRequest(
                        url: "$url/$clusterId",
                        acceptType: "APPLICATION_JSON",
                        customHeaders: [[
                                                name     : "Authorization",
                                                value    : "Bearer ${TOKEN}",
                                                maskValue: true,
                                        ]],
                        httpMode: "GET",
                )
                def clusterStatus = readJSON(text: clusterStatusResponse.content)
                status = clusterStatus.kubernetes_cluster.status.state
                if (logging && verbose) {
                    echo "DO K8S Cluster - status = $status"
                }
            }

            if (status != "running") {
                throw new IllegalStateException("Could not create the cluster in less than ${retries * interval} seconds.")
            } else if (logging && verbose) {
                echo "DO K8S Cluster - running"
            }

            // Getting the configuration file

            if (logging && verbose) {
                echo "DO K8S Cluster - getting connection configuration..."
            }
            def clusterConfigResponse = httpRequest(
                    url: "$url/$clusterId/kubeconfig",
                    customHeaders: [[
                                            name     : "Authorization",
                                            value    : "Bearer ${TOKEN}",
                                            maskValue: true,
                                    ]],
                    httpMode: "GET",
            )
            def clusterConfig = clusterConfigResponse.content as String

            // Writes configuration in file
            writeFile(file: configFile, text: clusterConfig)
            if (logging && verbose) {
                echo "DO K8S Cluster - cluster $clusterId config written in $configFile"
            }

            // OK, consolidate the cluster information
            def cluster = new K8SCluster(
                    params,
                    clusterId
            )

            if (logging) {
                echo "DO K8S Cluster - cluster $clusterId being ready."
            }

            // Runs the code
            body(cluster)

        } finally {

            if (destroy) {
                if (logging) {
                    echo "DO K8S Cluster - cluster $clusterId being destroyed..."
                }
                httpRequest(
                        url: "$url/$clusterId",
                        customHeaders: [[
                                                name     : "Authorization",
                                                value    : "Bearer ${TOKEN}",
                                                maskValue: true,
                                        ]],
                        httpMode: "DELETE",
                )
            }

        }

    }
}
