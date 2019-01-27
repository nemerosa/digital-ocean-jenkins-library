package net.nemerosa.jenkins.pipeline.digitalocean.k8s

class K8SPool {
    private final String name
    private final int count
    private final String size

    K8SPool(String name, int count, String size) {
        this.name = name
        this.count = count
        this.size = size
    }

    String getName() {
        return name
    }

    int getCount() {
        return count
    }

    String getSize() {
        return size
    }
}
