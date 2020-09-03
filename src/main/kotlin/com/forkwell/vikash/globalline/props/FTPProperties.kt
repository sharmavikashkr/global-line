package com.forkwell.vikash.globalline.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "app.ftp")
class FTPProperties {
    var host: String = "localhost"
    var port: Int = 22
    var username: String = ""
    var password: String = ""
    var workspace: String = "/"
}
