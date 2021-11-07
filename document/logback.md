# logback

### Path
- local : {project}/module-api/logs
- dev : 

### Filename
- local : sharedfood-yyyy-MM-dd
- dev : 

### Logging Level
- local : Info
- dev: Error

### Log Console Print Pattern
- Pattern : %green(%d{yyyy-MM-dd HH:mm:ss}) %magenta([%thread]) %blue(%-5level) %yellow(%logger{36}) : %msg%n
- Example : 2021-11-11 11:11:11 [thread] INFO [packagePath] : [msg]

### Log File Print Pattern
- Pattern : %date [%thread] %level : %msg%n
- Example : 2021-11-11 11:11:11,111 [thread] [logLevel] : [msg]