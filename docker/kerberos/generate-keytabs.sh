#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License

kadmin.local -q "addprinc -randkey nn/namenode.example.com@EXAMPLE.COM"
kadmin.local -q "ktadd -k /data/nn.service.keytab nn/namenode.example.com@EXAMPLE.COM"
kadmin.local -q "addprinc -randkey dn/datanode.example.com@EXAMPLE.COM"
kadmin.local -q "ktadd -k /data/dn.service.keytab dn/datanode.example.com@EXAMPLE.COM"
kadmin.local -q "addprinc -randkey zookeeper/zookeeper.example.com@EXAMPLE.COM"
kadmin.local -q "ktadd -k /data/zookeeper.service.keytab zookeeper/zookeeper.example.com@EXAMPLE.COM"
kadmin.local -q "addprinc -randkey solr/solr.example.com@EXAMPLE.COM"
kadmin.local -q "ktadd -k /data/solr.service.keytab solr/solr.example.com@EXAMPLE.COM"
kadmin.local -q "addprinc -randkey logsearch/logsearch.example.com@EXAMPLE.COM"
kadmin.local -q "ktadd -k /data/logsearch.service.keytab logsearch/logsearch.example.com@EXAMPLE.COM"
kadmin.local -q "addprinc -randkey logfeeder/logfeeder.example.com@EXAMPLE.COM"
kadmin.local -q "ktadd -k /data/logfeeder.service.keytab logfeeder/logfeeder.example.com@EXAMPLE.COM"
kadmin.local -q "addprinc -randkey HTTP/krb5.example.com@EXAMPLE.COM"
kadmin.local -q "ktadd -k /data/http.service.keytab HTTP/krb5.example.com@EXAMPLE.COM"