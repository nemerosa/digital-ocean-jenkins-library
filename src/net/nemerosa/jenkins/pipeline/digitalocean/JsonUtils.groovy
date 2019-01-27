package net.nemerosa.jenkins.pipeline.digitalocean

import net.sf.json.JSONSerializer

class JsonUtils {

    static String toJsonString(Object any) {
        return JSONSerializer.toJSON(any).toString(3)
    }

}
