import net.nemerosa.jenkins.pipeline.digitalocean.JsonUtils
import net.nemerosa.jenkins.pipeline.digitalocean.ParamUtils
import net.nemerosa.jenkins.pipeline.digitalocean.k8s.K8SPool

def call(Map<String, ?> params, Closure body) {

    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    boolean destroy = ParamUtils.getBooleanParam(params, "destroy", true)
    String credentials = ParamUtils.getParam(params, "credentials")
    String name = ParamUtils.getParam(params, "name")
    String region = ParamUtils.getParam(params, "region")
    String version = ParamUtils.getParam(params, "version", "1.13.1-do.2")

    String url = "https://api.digitalocean.com/v2/kubernetes/clusters"

    List<K8SPool> pools = []
    def poolDefs = params.pools
    if (poolDefs && poolDefs instanceof Collection) {
        poolDefs.each { poolDef ->
            pool = new K8SPool(
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
        pools.each { pool ->
            echo "DO K8S Cluster Pool - name: $pool.name"
            echo "DO K8S Cluster Pool - count: $pool.count"
            echo "DO K8S Cluster Pool - size: $pool.size"
        }
    }

    // Authentication
    withCredentials([string(credentialsId: credentials, variable: 'TOKEN')]) {

        // Creating the cluster
        if (logging) {
            echo "DO K8S Cluster - creation..."
        }
        def clusterCreationRequest = JsonUtils.toJsonString([
                name      : name,
                region    : region,
                version   : version,
                node_pools: pools.collect { pool ->
                    [
                            name : pool.name,
                            count: pool.count,
                            size : pool.size,
                    ]
                }
        ])
        // if (logging) {
        //     echo "DO K8S Cluster - request: $clusterCreationRequest"
        // }
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

    }
}