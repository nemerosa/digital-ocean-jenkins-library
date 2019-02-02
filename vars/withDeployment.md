## `withDeployment`

This step, given a YAML file, deploys it (`apply`), runs some code in its body, and optionally undeploys it (`delete`).

> The `kubectl` must already be configured (see [`withDigitalOceanK8SCluster`](withDigitalOceanK8SCluster.md) for example).

### Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| file | String | _Required_ | Path, relative to the workspace or the current directory, to the file to apply
| delete | boolean | `true` | If `true`, the `kubectl delete -f` command is run against the _same_ file at the end of this step's execution. |

### Example

```groovy
withDigitalOceanK8SCluster(...) { cluster ->
   withDeployment(
      file: "deployment.yaml",
      delete: true,
   ) {
      // Runs some code after deployment is started...
   }
   // Here, the deployment.yaml has been deleted (kubectl delete -f)
}
```
