appender('file', FileAppender) {
    file = 'log.txt'
    append = false
    encoder(PatternLayoutEncoder) {
        pattern = '[%d{yyyy/MM/dd HH:mm:ss.SSS}] {%thread} %-5level in %logger: %msg%n'
    }
}

appender('console', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = '%msg%n'
    }
}

logger('com.foursquare4j', WARN)
logger('org.apache.http', WARN)
logger('org.neo4j', WARN)
logger('org.springframework', WARN)
logger('org.springframework.jdbc.core', WARN)

root(ALL, ['console', 'file'])