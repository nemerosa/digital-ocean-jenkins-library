package net.nemerosa.jenkins.pipeline.digitalocean.k8s

import groovy.transform.Canonical

@Canonical
class K8SPool {
    String name
    int count
    String size
}
