package net.nemerosa.jenkins.pipeline.digitalocean.k8s

class K8SCluster {

    private final Map<String, ?> params
    private final String id

    K8SCluster(Map<String, ?> params, String id) {
        this.params = params
        this.id = id
    }

    Map<String, ?> getParams() {
        return params
    }

    String getId() {
        return id
    }
}
