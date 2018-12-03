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

### Top Level Descriptor Sections

| `Path` | `Description` | `Default` | `Examples` |
|---|---|---|---|
<#if topLevelSections??>
    <#list topLevelSections as section>
|`${section.path}`|${section.description}|${section.defaultValue}|${section.examples}|
    </#list>
</#if>
|`/output`|A list of output descriptors|`{}`||

### Input Descriptor Sections

| `Path` | `Description` | `Default` | `Examples` |
|---|---|---|---|
<#if inputConfigSection??>
    <#list inputConfigSection as section>
|`${section.path}`|${section.description}|${section.defaultValue}|${section.examples}|
    </#list>
</#if>

### Filter Descriptor Sections

| `Path` | `Description` | `Default` | `Examples` |
|---|---|---|---|
<#if filterConfigSection??>
    <#list filterConfigSection as section>
|`${section.path}`|${section.description}|${section.defaultValue}|${section.examples}|
    </#list>
</#if>

### Mapper Descriptor Sections

| `Path` | `Description` | `Default` | `Examples` |
|---|---|---|---|
<#if postMapValuesSection??>
    <#list postMapValuesSection as section>
|`${section.path}`|${section.description}|${section.defaultValue}|${section.examples}|
    </#list>
</#if>

### Output Descriptor Sections

| `Path` | `Description` | `Default` | `Examples` |
|---|---|---|---|
|`/output/[]/is_enabled`|A flag to show if the output should be used.|true|<ul><li>`true`</li><li>`false`</li></ul>|