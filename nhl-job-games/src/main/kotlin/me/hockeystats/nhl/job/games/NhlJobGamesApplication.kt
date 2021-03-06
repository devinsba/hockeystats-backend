package me.hockeystats.nhl.job.games

import me.hockeystats.CommonConfiguration
import org.springframework.boot.WebApplicationType
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webflux.webFlux

val app = application(WebApplicationType.REACTIVE) {
    beans {
        bean<CommonConfiguration>()
        bean {
            Handler(ref(), ref(), ref())
        }
    }
    listener<ApplicationReadyEvent> {
        ref<CommonConfiguration>().initialize(it.applicationContext as GenericApplicationContext)
    }
    webFlux {
        port = (env.getProperty("server.port") ?: "8080").toInt()
        router {
            val handler = ref<Handler>()
            GET("/") {
                handler.today().apply(it)
            }
            GET("/yesterday") {
                handler.yesterday().apply(it)
            }
            GET("/delete-all") {
                handler.deleteAll().apply(it)
            }
            POST("/") {
                handler.requested().apply(it)
            }
        }
        codecs {
            string()
            jackson()
        }
    }
}

fun main() {
    app.run()
}