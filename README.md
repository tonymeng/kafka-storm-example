Local Kafka+Storm Example
------

This example will involve setting up a local Kafka and local Storm "cluster". Useful for understanding how kafka and storm work as well as prototyping new topologies.

The following is used to make this happen.

1. HolmesNL's [kafka-spout] (https://github.com/HolmesNL/kafka-spout)
  * Note: kafka-spout is not available in any repository, to setup use Maven as explained [here] (https://github.com/HolmesNL/kafka-spout/wiki#maven)
2. Apache's Storm [storm-core] (https://github.com/apache/incubator-storm)
3. Apache's Kafka
  * Note: Follow the [quickstart guide] (http://kafka.apache.org/documentation.html#quickstart) to get kafka up and running.

Setup zookeeper and kafka as explained in the Apache Kafka [Quickstart Guide] (http://kafka.apache.org/documentation.html#quickstart). Once they are up and running and you're able to see messages arrive at the Consumer from the Producer, run LogLineTopology.java and you will see the messages get counted and dumped via WordCount. 

Kafka+Storm on EC2 Example
-----

###Cluster Setup
LogLineTopologyCluster was written to be deployed in a storm cluster. To do this, I have setup a cluster on EC2 (for free!) that looks like the following:

1. t1.micro - running zookeeper
  * Security Rule: Enabled inbound port: 2181
2. t1.micro - running kafka
  * Security Rule: Enabled inbound port: 9092
3. t1.micro - running storm (nimbus, supervisors, ui)
  * Security Rule: Enabled inbound ports: 6627 (for deploying topologies), 8080 (for storm UI)

###Zookeeper Setup
Zookeeper has been setup remotely by following the same [quickstart guide] (http://kafka.apache.org/documentation.html#quickstart). No additional configuration needed.

###Kafka Setup
Kafka has also been setup remotely by following the same [quickstart guide] (http://kafka.apache.org/documentation.html#quickstart). Inside config/server.properties, add `advertised.host.name=<public ip>` and set `zookeeper.connect=<internal zookeeper ip>`.
  * Note: the t1.micro only has about 625m of memory, but Kafka tries to allocate 1G. I had to change `bin/kafka-server-start.sh` to set the value of `KAFKA_HEAP_OPTS="-Xmx512m -Xms512m"` to get kafka running.

###Storm Setup
Storm has been setup using [this guide] (http://www.michael-noll.com/tutorials/running-multi-node-storm-cluster/). Scroll down to the "Installing Storm" section, skip the installation of Java6. Start at the installation of ZeroMQ. I tried getting the latest ZMQ but had issues compiling it. Using the RPM's provided in the guide seemed to work fine (both for ZMQ and JZMQ). Python is already on the boxes by default so they are not necessary. When configuring storm's `conf/storm.yaml`. The following need to be set: 
* `storm.zookeeper.servers: -"<internal zookeeper ip>"`
* `nimbus.host: "127.0.0.1"`
* `storm.local.dir: "/tmp/storm"`
* `supervisor.slots.ports: -6700`. 

Once this is done, start all of the daemons. (`bin/storm nimbus`, `bin/storm ui`, `bin/storm supervisor`). Once these are started, you should be able to view the status of storm by going to `http://<public storm node ip>:8080`.

###Deploying a Topology
Once all of the nodes are talking to each other, the next task is to deploy a topology.
* Note: My development box is a Macbook so some of the commands may be different depending on where you're installing from.

1. Install storm (`brew install storm`).
2. Updating configs in `~/.storm/storm.yaml`. These configs need to be updated with `nimbus.host: "<external nimbus ip>"`.
3. Building LogLineTopologyCluster into a jar (with dependencies). Jar up LogLineTopologyCluster with all dependencies in the project (minus org.apache.storm:storm-core:0.9.1-incubating).
4. Deploying using the command `storm jar <path/to/>storm-loglines.jar LogLineTopologyCluster <internal zookeeper ip>:2181 logs`

Now the topology should be visible by typing `storm list`.

###Messaging Kafka on EC2
Messaging Kafka on EC2 is simple. Check TestProducer.java. Change the property for `metadata.broker.list` and set it to your external KAFKA broker's ip.

At this point, you should be able to see values populate `/tmp/filedump` and `/tmp/filedumpcount` on your Storm node.

