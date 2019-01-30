import static net.nemerosa.jenkins.pipeline.digitalocean.ParamUtils.getBooleanParam
import static net.nemerosa.jenkins.pipeline.digitalocean.ParamUtils.getParam

def call(Map<String,?> params, Closure body) {
    String file = getParam(params, "file")
    boolean delete = getBooleanParam(params, "delete", false)

    try {
        sh "kubectl apply -f $file"
        body()
    } finally {
        if (delete) {
            sh "kubectl delete -f $file"
        }
    }
}