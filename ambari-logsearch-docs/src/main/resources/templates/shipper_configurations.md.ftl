<!---
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements. See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

## Log Feeder Shipper Descriptor

### Top Level Descriptors

Input, Filter and Output configurations are defined in 3 (at least) different files. (note: there can be multiple input configuration files, but only 1 output and global configuration)

input.config-myservice.json example:
```json
{
  "input" : [
  ],
  "filter" : [
  ]
}
```
output.config.json example:
```json
{
  "output" : [
  ]
}
```
global.config.json example:
```json
{
  "global" : {
    "source" : "file",
    "add_fields":{
      "cluster":"cl1"
    },
    "tail" : "true"
  }
}
```
| `Path` | `Description` | `Default` | `Examples` |
|---|---|---|---|
<#if shipperConfigs.topLevelConfigSections??>
    <#list shipperConfigs.topLevelConfigSections as section>
|`${section.path}`|${section.description}|${section.defaultValue}|${section.examples}|
    </#list>
</#if>
|`/output`|A list of output descriptors|`{}`|<ul><li>`{"output": [{"is_enabled" : "true", "destination": "solr", "myfield": "myvalue"}]`</li></ul>|
|`/global`|A map that contains field/value pairs|`EMPTY`|<ul><li>`{"global": {"myfield": "myvalue"}}`</li></ul>|

### Input Descriptor

Input configurations (for monitoring logs) can be defined in the input descriptor section.
Example:
```json
{
  "input" : [
    {
      "type": "simple_service",
      "rowtype": "service",
      "path": "/var/logs/my/service/service_sample.txt",
      "group": "Ambari",
      "cache_enabled": "true",
      "cache_key_field": "log_message",
      "cache_size": "50"
    },
    {
      "type": "simple_service_json",
      "rowtype": "service",
      "path": "/var/logs/my/service/service_sample.json",
      "properties": {
        "myKey1" : "myValue1",
        "myKey2" : "myValue2"
      }
    },
    {
      "type": "simple_audit_service",
      "rowtype": "audit",
      "path": "/var/logs/my/service/service_audit_sample.txt",
      "is_enabled": "true",
      "add_fields": {
        "logType": "AmbariAudit",
        "enforcer": "ambari-acl",
        "repoType": "1",
        "repo": "ambari",
        "level": "INFO"
      }
    },
    {
     "type": "wildcard_log_service",
     "rowtype": "service",
     "path": "/var/logs/my/service/*/service_audit_sample.txt",
     "init_default_fields" : "true",
     "detach_interval_min": "50",
     "detach_time_min" : "300",
     "path_update_interval_min" : "10",
     "max_age_min" : "800"
    },
    {
      "type": "service_socket",
      "rowtype": "service",
      "port": 61999,
      "protocol" : "tcp",
      "secure" : "false",
      "source" : "socket",
      "log4j": "true"
    },
    {
      "type": "docker_service",
      "rowtype": "service",
      "docker" : "true",
      "default_log_levels" : [
         "FATAL", "ERROR", "WARN", "INFO", "DEBUG"
      ]
    }
  ]
}
```
Built-in input shipper configurations:
| `Path` | `Description` | `Default` | `Examples` |
|---|---|---|---|
<#if shipperConfigs.inputConfigSections??>
    <#list shipperConfigs.inputConfigSections as section>
|`${section.path}`|${section.description}|${section.defaultValue}|${section.examples}|
    </#list>
</#if>

### Filter Descriptor

Filter configurations can be defined in the filter descriptor section.
Example:
```json
{
  "input" : [
  ],
  "filter": [
    {
      "filter": "json",
      "conditions": {
        "fields": {
          "type": [
            "simple_service_json"
          ]
        }
      }
    }
    {
      "filter": "grok",
      "deep_extract": "false",
      "conditions":{
        "fields":{
          "type":[
            "simple_service",
            "simple_audit_service",
            "docker_service"
          ]
        }
      },
      "log4j_format":"%d{ISO8601} %5p [%t] %c{1}:%L - %m%n",
      "multiline_pattern":"^(%{TIMESTAMP_ISO8601:logtime})",
      "message_pattern":"(?m)^%{TIMESTAMP_ISO8601:logtime}%{SPACE}%{LOGLEVEL:level}%{SPACE}\\[%{DATA:thread_name}\\]%{SPACE}%{JAVACLASS:logger_name}:%{INT:line_number}%{SPACE}-%{SPACE}%{GREEDYDATA:log_message}",
      "post_map_values":{
        "logtime":{
          "map_date":{
            "target_date_pattern":"yyyy-MM-dd HH:mm:ss,SSS"
          }
        }
      }
    },
    {
      "filter": "grok",
      "conditions": {
        "fields": {
          "type": [
            "ambari_audit"
          ]
        }
      },
      "log4j_format": "%d{ISO8601} %-5p %c{2} (%F:%M(%L)) - %m%n",
      "multiline_pattern": "^(%{TIMESTAMP_ISO8601:evtTime})",
      "message_pattern": "(?m)^%{TIMESTAMP_ISO8601:evtTime},%{SPACE}%{GREEDYDATA:log_message}",
      "post_map_values": {
        "evtTime": {
          "map_date": {
            "target_date_pattern": "yyyy-MM-dd'T'HH:mm:ss.SSSXX"
          }
        }
      }
    },
    {
      "filter": "keyvalue",
      "sort_order": 1,
      "conditions": {
        "fields": {
          "type": [
            "ambari_audit"
          ]
        }
      },
      "source_field": "log_message",
      "field_split": ", ",
      "value_borders": "()",
      "post_map_values": {
        "User": {
          "map_field_value": {
            "pre_value": "null",
            "post_value": "unknown"
          },
          "map_field_name": {
            "new_field_name": "reqUser"
          }
        }
      }
    }
  ]
}
```
Built-in filter shipper configurations:
| `Path` | `Description` | `Default` | `Examples` |
|---|---|---|---|
<#if shipperConfigs.filterConfigSections??>
    <#list shipperConfigs.filterConfigSections as section>
|`${section.path}`|${section.description}|${section.defaultValue}|${section.examples}|
    </#list>
</#if>

### Mapper Descriptor

Mapper configurations are defined inside filters, it can alter fields.
Example:
```json
{
  "input": [
  ],
  "filter": [
    {
      "filter": "keyvalue",
      "sort_order": 1,
      "conditions": {
        "fields": {
          "type": [
            "ambari_audit"
          ]
        }
      },
      "source_field": "log_message",
      "field_split": ", ",
      "value_borders": "()",
      "post_map_values": {
        "Status": {
          "map_field_value": {
            "pre_value": "null",
            "post_value": "unknown"
          },
          "map_field_name": {
            "new_field_name": "ws_status"
          }
        },
        "StatusWithRepeatedKeys": [
          {
            "map_field_value": {
              "pre_value": "Failed",
              "post_value": "0"
            }
          },
          {
            "map_field_value": {
              "pre_value": "Failed to queue",
              "post_value": "0"
            }
          }
        ]
      }
    }
  ]
}
```
Built-in mapper shipper configurations:
| `Path` | `Description` | `Default` | `Examples` |
|---|---|---|---|
<#if shipperConfigs.postMapValuesConfigSections??>
    <#list shipperConfigs.postMapValuesConfigSections as section>
|`${section.path}`|${section.description}|${section.defaultValue}|${section.examples}|
    </#list>
</#if>

### Output Descriptor

Output configurations can be defined in the output descriptor section. (it can support any extra - output specific - key value pairs)
Example:

```json
{
  "output": [
    {
      "is_enabled": "true",
      "comment": "Output to solr for service logs",
      "collection" : "hadoop_logs",
      "destination": "solr",
      "zk_connect_string": "localhost:9983",
      "type": "service",
      "conditions": {
        "fields": {
          "rowtype": [
            "service"
          ]
        }
      }
    },
    {
      "comment": "Output to solr for audit records",
      "is_enabled": "true",
      "collection" : "audit_logs",
      "destination": "solr",
      "zk_connect_string": "localhost:9983",
      "type": "audit",
      "conditions": {
        "fields": {
          "rowtype": [
            "audit"
          ]
        }
      }
    }
  ]
}
```
Built-in output shipper configurations:
| `Path` | `Description` | `Default` | `Examples` |
|---|---|---|---|
|`/output/[]/conditions`|The conditions of which input to filter.|`EMPTY`||
|`/output/[]/conditions/fields`|The fields in the input element of which's value should be met.|`EMPTY`|<ul><li>`"fields"{"type": ["hdfs_audit", "hdfs_datanode"]}`</li></ul>|
|`/output/[]/conditions/fields/type`|The acceptable values for the type field in the input element.|`EMPTY`|<ul><li>`"ambari_server"`</li><li>`"spark_jobhistory_server", "spark_thriftserver", "livy_server"`</li></ul>|
|`/output/[]/is_enabled`|A flag to show if the output should be used.|true|<ul><li>`true`</li><li>`false`</li></ul>|
<#if shipperConfigs.outputConfigSections??>
  <#list shipperConfigs.outputConfigSections as section>
|`${section.path}`|${section.description}|${section.defaultValue}|${section.examples}|
  </#list>
</#if>