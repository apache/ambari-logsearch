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
|`/filter`|A list of filter descriptions|`EMPTY`|<ul><li>`{"filter" : [ {"filter": "json", "conditions": {"fields": { "type": ["mytype1", "mytype2"]} } } ]}`</li></ul>|
|`/input`|A list of input descriptions|`EMPTY`|<ul><li>`{"input" : [ {"type": "myinput_service_type"}] }`</li></ul>|
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
|`/input/[]/add_fields`|The element contains field_name: field_value pairs which will be added to each rows data.|`EMPTY`|<ul><li>`"cluster":"cluster_name"`</li></ul>|
|`/input/[]/cache_dedup_interval`|The maximum interval in ms which may pass between two identical log messages to filter the latter out.|1000|<ul><li>`500`</li></ul>|
|`/input/[]/cache_enabled`|Allows the input to use a cache to filter out duplications.|false|<ul><li>`true`</li><li>`false`</li></ul>|
|`/input/[]/cache_key_field`|Specifies the field for which to use the cache to find duplications of.|log_message|<ul><li>`some_field_prone_to_repeating_value`</li></ul>|
|`/input/[]/cache_last_dedup_enabled`|Allow to filter out entries which are same as the most recent one irrelevant of it's time.|false|<ul><li>`true`</li><li>`false`</li></ul>|
|`/input/[]/cache_size`|The number of entries to store in the cache.|100|<ul><li>`50`</li></ul>|
|`/input/[]/checkpoint_interval_ms`|The time interval in ms when the checkpoint file should be updated.|5000|<ul><li>`10000`</li></ul>|
|`/input/[]/class_name`|Custom class which implements an input type|`EMPTY`|<ul><li>`org.example.MyInputSource`</li></ul>|
|`/input/[]/copy_file`|Should the file be copied (only if not processed).|false|<ul><li>`true`</li><li>`false`</li></ul>|
|`/input/[]/default_log_levels`|Use these as default log levels for the input - overrides the global default log levels.|`EMPTY`|<ul><li>`default_log_levels: ["INFO", "WARN"]`</li></ul>|
|`/input/[]/detach_interval_min`|The period in minutes for checking which files are too old (default: 300)|1800|<ul><li>`60`</li></ul>|
|`/input/[]/detach_time_min`|The period in minutes when the application flags a file is too old (default: 2000)|2000|<ul><li>`60`</li></ul>|
|`/input/[]/docker`|Input comes from a docker container.|false|<ul><li>`true`</li><li>`false`</li></ul>|
|`/input/[]/gen_event_md5`|Generate an event_md5 field for each row by creating a hash of the row data.|true|<ul><li>`true`</li><li>`false`</li></ul>|
|`/input/[]/group`|Group of the input type.|`EMPTY`|<ul><li>`Ambari`</li><li>`Yarn`</li></ul>|
|`/input/[]/init_default_fields`|Init default fields (ip, path etc.) before applying the filter.|false|<ul><li>`true`</li><li>`false`</li></ul>|
|`/input/[]/is_enabled`|A flag to show if the input should be used.|true|<ul><li>`true`</li><li>`false`</li></ul>|
|`/input/[]/log4j`|Use Log4j serialized objects (e.g.: SocketAppender)|false|<ul><li>`true`</li></ul>|
|`/input/[]/max_age_min`|If the file has not modified for long (this time value in minutes), then the checkpoint file can be deleted.|0|<ul><li>`2000`</li></ul>|
|`/input/[]/path`|The path of the source, may contain '*' characters too.|`EMPTY`|<ul><li>`/var/log/ambari-logsearch-logfeeder/logsearch-logfeeder.json`</li><li>`/var/log/zookeeper/zookeeper*.log`</li></ul>|
|`/input/[]/path_update_interval_min`|The period in minutes for checking new files (default: 5, based on detach values, its possible that a new input wont be monitored)|5|<ul><li>`5`</li></ul>|
|`/input/[]/port`|Unique port for specific socket input|`EMPTY`|<ul><li>`61999`</li></ul>|
|`/input/[]/process_file`|Should the file be processed.|true|<ul><li>`true`</li><li>`false`</li></ul>|
|`/input/[]/properties`|Custom key value pairs|`EMPTY`|<ul><li>`{k1 : v1, k2: v2}`</li></ul>|
|`/input/[]/protocol`|Protocol type for socket server (tcp / udp - udp is not supported right now)|tcp|<ul><li>`udp`</li><li>`tcp`</li></ul>|
|`/input/[]/rowtype`|The type of the row.|`EMPTY`|<ul><li>`service`</li><li>`audit`</li></ul>|
|`/input/[]/s3_access_key`|The access key used for AWS credentials. (Not supported yet through shipper configurations)|`EMPTY`||
|`/input/[]/s3_endpoint`|Endpoint URL for S3. (Not supported yet through shipper configurations)|`EMPTY`||
|`/input/[]/s3_secret_key`|The secret key used for AWS credentials. (Not supported yet through shipper configurations)|`EMPTY`||
|`/input/[]/secure`|Use SSL|false|<ul><li>`true`</li></ul>|
|`/input/[]/source`|The type of the input source.|`EMPTY`|<ul><li>`file`</li><li>`s3_file`</li></ul>|
|`/input/[]/tail`|The input should check for only the latest file matching the pattern, not all of them.|true|<ul><li>`true`</li><li>`false`</li></ul>|
|`/input/[]/type`|The log id for this source.|`EMPTY`|<ul><li>`zookeeper`</li><li>`ambari_server`</li></ul>|
|`/input/[]/use_event_md5_as_id`|Generate an id for each row by creating a hash of the row data.|false|<ul><li>`true`</li><li>`false`</li></ul>|

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
|`/filter/[]/conditions`|The conditions of which input to filter.|`EMPTY`||
|`/filter/[]/conditions/fields`|The fields in the input element of which's value should be met.|`EMPTY`|<ul><li>`"fields"{"type": ["hdfs_audit", "hdfs_datanode"]}`</li></ul>|
|`/filter/[]/conditions/fields/type`|The acceptable values for the type field in the input element.|`EMPTY`|<ul><li>`"ambari_server"`</li><li>`"spark_jobhistory_server", "spark_thriftserver", "livy_server"`</li></ul>|
|`/filter/[]/deep_extract`|Keep the full named regex collection for Grok filters.|`EMPTY`|<ul><li>`true`</li></ul>|
|`/filter/[]/field_split`|The string that splits the key-value pairs.|\t|<ul><li>` `</li><li>`,`</li></ul>|
|`/filter/[]/filter`|The type of the filter.|`EMPTY`|<ul><li>`grok`</li><li>`keyvalue`</li><li>`json`</li></ul>|
|`/filter/[]/is_enabled`|A flag to show if the filter should be used.|true|<ul><li>`true`</li><li>`false`</li></ul>|
|`/filter/[]/log4j_format`|The log4j pattern of the log, not used, it is only there for documentation.|`EMPTY`|<ul><li>`%d{ISO8601} - %-5p [%t:%C{1}@%L] - %m%n`</li></ul>|
|`/filter/[]/message_pattern`|The grok pattern to use to parse the log entry.|`EMPTY`|<ul><li>`(?m)^%{TIMESTAMP_ISO8601:logtime}%{SPACE}-%{SPACE}%{LOGLEVEL:level}%{SPACE}\[%{DATA:thread_name}\@%{INT:line_number}\]%{SPACE}-%{SPACE}%{GREEDYDATA:log_message}`</li></ul>|
|`/filter/[]/multiline_pattern`|The grok pattern that shows that the line is not a log line on it's own but the part of a multi line entry.|`EMPTY`|<ul><li>`^(%{TIMESTAMP_ISO8601:logtime})`</li></ul>|
|`/filter/[]/post_map_values`|Mappings done after the filtering provided it's result.|`EMPTY`||
|`/filter/[]/remove_source_field`|Remove the source field after the filter is applied.|false|<ul><li>`true`</li><li>`false`</li></ul>|
|`/filter/[]/skip_on_error`|Skip filter if an error occurred during applying the grok filter.|`EMPTY`|<ul><li>`true`</li></ul>|
|`/filter/[]/sort_order`|Describes the order in which the filters should be applied.|`EMPTY`|<ul><li>`1`</li><li>`3`</li></ul>|
|`/filter/[]/source_field`|The source of the filter, must be set for keyvalue filters.|log_message|<ul><li>`field_further_to_filter`</li></ul>|
|`/filter/[]/value_borders`|The borders around the value, must be 2 characters long, first before it, second after it.|`EMPTY`|<ul><li>`()`</li><li>`[]`</li><li>`{}`</li></ul>|
|`/filter/[]/value_split`|The string that separates keys from values.|=|<ul><li>`:`</li><li>`->`</li></ul>|

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
|`/filter/[]/post_map_values/{field_name}/[]/map_anonymize/hide_char`|The character to hide with|*|<ul><li>`X`</li><li>`-`</li></ul>|
|`/filter/[]/post_map_values/{field_name}/[]/map_anonymize/pattern`|The pattern to use to identify parts to anonymize. The parts to hide should be marked with the "<hide>" string.|`EMPTY`|<ul><li>`Some secret is here: <hide>, and another one is here: <hide>`</li></ul>|
|`/filter/[]/post_map_values/{field_name}/[]/map_copy/copy_name`|The name of the copied field|`EMPTY`|<ul><li>`new_name`</li></ul>|
|`/filter/[]/post_map_values/{field_name}/[]/map_custom/class_name`|Custom class which implements a mapper type|`EMPTY`|<ul><li>`org.example.MyMapper`</li></ul>|
|`/filter/[]/post_map_values/{field_name}/[]/map_custom/properties`|Custom key value pairs|`EMPTY`|<ul><li>`{k1 : v1, k2: v2}`</li></ul>|
|`/filter/[]/post_map_values/{field_name}/[]/map_date/src_date_pattern`|If it is specified than the mapper converts from this format to the target, and also adds missing year|`EMPTY`|<ul><li>`MMM dd HH:mm:ss`</li></ul>|
|`/filter/[]/post_map_values/{field_name}/[]/map_date/target_date_pattern`|If 'epoch' then the field is parsed as seconds from 1970, otherwise the content used as pattern|`EMPTY`|<ul><li>`yyyy-MM-dd HH:mm:ss,SSS`</li><li>`epoch`</li></ul>|
|`/filter/[]/post_map_values/{field_name}/[]/map_field_name/new_field_name`|The name of the renamed field|`EMPTY`|<ul><li>`new_name`</li></ul>|
|`/filter/[]/post_map_values/{field_name}/[]/map_field_value/post_value`|The value to which the field is modified to|`EMPTY`|<ul><li>`new_value`</li></ul>|
|`/filter/[]/post_map_values/{field_name}/[]/map_field_value/pre_value`|The value that the field must match (ignoring case) to be mapped|`EMPTY`|<ul><li>`old_value`</li></ul>|

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
|`/output/[]/destination`|Alias of a supported output (e.g.: solr). The class-alias mapping should exist in the alias config.|`EMPTY`|<ul><li>`"solr"`</li><li>`"hdfs"`</li></ul>|
|`/output/[]/type`|Output type name, right now it can be service or audit|`EMPTY`|<ul><li>`"service"`</li><li>`"audit"`</li></ul>|
