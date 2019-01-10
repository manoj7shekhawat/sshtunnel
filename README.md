Synopsis

This project is created for demo for:
 - A jump box (or bastion host) it is a common security setup for servers. 
 - It requires users to first SSH to the bastion host and then SSH to the remote server. 
 - Remote server can NOT be accessed directly. It can be accessed vai bastion host ONLY.
 - Then we want to connect to a oracle DB host via remote host. Oracle DB port is by default 1521 
 - End goal is to connect to DB by creating 2 local port forwarding (remote host and DB host), so that we can access DB from our local m/c like this: ```jdbc:oracle:thin:@//localhost:9999/system-Indentifier```

Code Example
- to run code: ```./gradlew clean jumpHost```
- Manually 2 hop port forwarding: ```ssh -v -i id_rsa -L 9999:localhost:7777 root@BastionHostName -t ssh -v -i id_rsa -N -L 7777:<dbHostName>:1521 root@<RemoteHostName>```

Motivation

 Online examples are mostly of one hop, not the double needed. This code does 2 hops, but you can enchance it to support any number of hops.

Installation
- Clone and import project just update hostname (bastion host, remote host, DB host, etc etc) in ```Demo``` class

API Reference
- http://www.jcraft.com/jsch/
- http://logging.apache.org/log4j/1.2/
- https://git-scm.com/


Tests
- Not avaiable

Contributors

- Manoj Shekhawat

License

It is a free software; you can redistribute it and/or modify it under the terms of either:
- a) the GNU General Public License as published by the Free Software Foundation; either version 1, or (at your option) any later version,
-   or
- b) the "Artistic License" which comes with this Kit.