## `waitForDigitalOceanLoadBalancer`

This step waits for a [Digital Ocean load balancer](https://www.digitalocean.com/products/load-balancer/) created by a service to be ready and returns its external IP when ready.

Given a *service* declaration like:

```YAML
apiVersion: v1
kind: Service
metadata:
  name: my-service
  labels:
    app: my-app
spec:
  selector:
    app: my-app
  type: LoadBalancer
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
      name: http
```

The following code will wait for the load balancer associated with the `my-service` service to be ready:

```groovy
String ip = waitForDigitalOceanLoadBalancer(
   service: "my-service",
   logging: true,
)
```

> This step returns the external IP but for a declarative Jenkins pipeline, an environment variable is also set (see *Parameters* below).

### Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| maxTries | int | `20` | Number of retries when waiting for the load balancer to be ready. |
| interval | int | `30` | Number of seconds to wait between each retry when waiting for the load balancer to be ready. |
| logging | boolean | `false` | Set to `true` to load the waiting operations |
| service | String | _Required_ | Name of K8S *Service* to wait for |
| outputVariable | String | `SERVICE_IP` | Name of the environment variable to set with the external IP of the service load balancer. |

### Return

This step returns the external IP of the service load balancer as a `String`.

### Example

```groovy
waitForDigitalOceanLoadBalancer(
   service: "my-service",
   outputVariable: "MY_SERVICE_IP",
   logging: true,
)
echo "IP = ${MY_SERVICE_IP}"
```
