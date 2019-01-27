package net.nemerosa.jenkins.pipeline.digitalocean.k8s

class K8SCluster {

    private final Map<String, ?> params
    private final String id
    private final String config

    K8SCluster(Map<String, ?> params, String id, String config) {
        this.params = params
        this.id = id
        this.config = config
    }

    Map<String, ?> getParams() {
        return params
    }

    String getId() {
        return id
    }

    String getConfig() {
        return config
    }
}
