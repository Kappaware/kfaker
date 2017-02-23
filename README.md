# kfaker

kfaker is a small project intended to generate a flow of streaming message in a kafka topic.

***
## Usage

kfaker is provided as rpm packages (Sorry, only this packaging is currently provided. Contribution welcome), on the [release pages](https://github.com/Kappaware/kfaker/releases).

Once installed, basic usage is the following:

    # kfaker --brokers "brk1.my.domain.com:9092,brk2.my.domain.com:9092,brk3.my.domain.com:9092" --topic t1 --messon
   
This will send one message per second in the topic t1. Messages will be displayed.
  
    
Here all all the parameters
	
	Option (* = required)             Description
	---------------------             -----------
	* --brokers <br1:9092,br2:9092>   Comma separated values of Target Kafka brokers
	--burstCount <Integer: count>     Burst count (default: 1)
	--forceProperties                 Force unsafe properties
	--initialCounter <Long: counter>  Initial counter value (default: 0)
	--messon                          Display all generated messages
	--period <Long: period(ms)>       Period between two bursts (ms) (default: 1000)
	--property <prop=val>             Producer property (May be specified several times)
	--sender <sender name>            Sender of the message (default: Chuck NORRIS)
	* --topic <topic>                 Target topic

***
## Generated message

The generated messages value is a JSON string with the following format:

	{
		"body": "Chuck Norris breaks RSA 128-bit encrypted codes in milliseconds.",
		"overallCounter": 3,
		"recipient": "JADE",
		"recipientCounter": 0,
		"sender": "Chuck NORRIS",
		"timestamp": 1487878257783
	}
	
Here is some field description:

* `body`: A random text string.

* `overallCounter`: The count of messages sent since the launch, added with the value provided by --initialCounter parameter.

* `recipient`: A first name randomly choose in a list of 100.

* `recipientCounter`: The count of message sent to this recipient since the launch

* `sender`: A string provided by the --sender parameters. Useful when launching several kfaker process.

* `timestamp`: The sending timestamp (Number of milliseconds since epoch). 

The message key is the recipient. This will ensure all message to a given recipient will be pushed in the same partition. 

***
## Kerberos support

If kerberos is activated, you will need to define a jaas configuration file as java option. 

This can easily be achieved by uncomment this following line in `/etc/kfaker/setenv.sh`
    
    JOPTS="$JOPTS -Djava.security.auth.login.config=/etc/kfaker/kafka_client_jaas.conf"
    
You can of course modify the `kafka_client_jaas.conf` file to adjust to your needs or target another existing one.

But, keep in mind, you must also perform a `kinit` command, with a principal granting write access to target topic

***
## Ansible integration

You will find an Ansible role [at this location](http://github.com/BROADSoftware/bsx-roles/tree/master/kappatools/kfaker).

This role can be used as following;
	
	- hosts: kafka_brokers
	
	- hosts: cmd_node
	  vars:
        kdescribe_rpm_url: https://github.com/Kappaware/kfaker/releases/download/v0.2.0/kfaker-0.2.0-1.noarch.rpm
        kerberos: true
        kafka_port: 9092
	  roles:
	  - kappatools/kfaker
	  
> Note `- hosts: kafka_brokers` at the beginning, which force ansible to grab info about the hosts in the `[kafka_brokers]` group, to be able to fulfill this info into kfaker configuration. Of course, such a group must be defined in the inventory. In this case, brokers information will no longer be required on the command line.


***
## Build

Just clone this repository and then:

    $ gradlew rpm

This should build everything. You should be able to find generated packages in build/distribution folder.


***
## License

    Copyright (C) 2017 BROADSoftware

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	    http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.




