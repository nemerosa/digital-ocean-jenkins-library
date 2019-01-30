import static net.nemerosa.jenkins.pipeline.digitalocean.ParamUtils.*

def call(Map<String, ?> params) {

    int tries = 0
    int maxTries = getIntParam(params, "tries", 20)
    int interval = getIntParam(params, "interval", 30)
    boolean logging = getBooleanParam(params, "logging", false)

    String service = getParam(params, "service")
    String outputVariable = getParam(params, "outputVariable", "SERVICE_IP")

    String ip = ""
    while (!ip && tries < maxTries) {
        tries++
        if (logging) echo "${tries}/${maxTries} Waiting for service '${service}' to be available..."
        int status = sh(script: "kubectl get service ${service} --output json > service.json", returnStatus: true)
        if (status == 0) {
            def json = readJSON(file: "service.json")
            if (logging) echo "JSON = $json"
            ip = json.status?.loadBalancer?.ingress?.first()?.ip?.text()
            if (!ip) {
                sleep(time: interval, unit: 'SECONDS')
            }
        } else {
            sleep(time: interval, unit: 'SECONDS')
        }
    }
    if (!ip) {
        error "Could not get '${service}' load balancer IP in less than ${interval * maxTries} seconds"
    }
    env[outputVariable] = ip

    // OK
    return ip
}