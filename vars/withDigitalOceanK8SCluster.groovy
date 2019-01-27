import net.nemerosa.jenkins.pipeline.digitalocean.ParamUtils
import net.nemerosa.jenkins.pipeline.digitalocean.k8s.K8SPool

def call(Map<String, ?> params, Closure body) {

    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    boolean destroy = ParamUtils.getBooleanParam(params, "destroy", true)
    String name = ParamUtils.getParam(params, "name")
    String region = ParamUtils.getParam(params, "region")
    String version = ParamUtils.getParam(params, "region", "1.13.1-do.2")

    List<K8SPool> pools = []
    def poolDefs = params.pools
    if (poolDefs && poolDefs instanceof Collection) {
        poolDefs.each { poolDef ->
            pool = new K8SPool(
                    name: ParamUtils.getParam(poolDef as Map<String, ?>, "name"),
                    count: ParamUtils.getIntParam(poolDef as Map<String, ?>, "count"),
                    size: ParamUtils.getParam(poolDef as Map<String, ?>, "size"),
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
}