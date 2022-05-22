# Adform Scala Academy IoT API

Api that return rules saved for particular device.


## Configuration

| Name             | Type        | Description     |
| :---             |    :----:   |            ---: |
| http             | [HttpConfig](src/main/scala/com/adform/scalaacademy/http/HttpConfig.scala)                 | Describe general http server behaviour. For more see [http configuration](#http-configuration)   |
| device-service   | [DeviceServiceConf](src/main/scala/com/adform/scalaacademy/device/DeviceServiceConf.scala) | Describe general device service behaviour. For more see [device service configuration](#device-service-configuration)   |


### http configuration

|Name| Type| Description|
|:---| :---| :---|
|host| String| host on which http server wil be lessening requests, should be 0.0.0.0 for most cases|
|port| Int| port on which http server wil be lessening requests|

### device service configuration

|Name| Type| Description|
|:---| :---| :---|
|error-simulator| [DeviceErrorSimulatorConf](src/main/scala/com/adform/scalaacademy/device/DeviceServiceConf.scala)| object describing how often api will return errors for simulating errors and also how big artificial delay will be generated during call each endpoint. For more see [error simulator configuration](#error-simulator-configuration) |


### error simulator configuration

|Name| Type| Description|
|:---| :---| :---|
| max-timeout | FiniteDuration | Configure maximum artificial delay that can be generated when call each service endpoint, cannot be 0. Minimum value is 1 millisecond. |
| error-probability | Double | Describe the probability of random error occurrence when call each api. Values from range [0, +Inf) means always error's will be returned |