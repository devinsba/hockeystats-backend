package me.hockeystats.nhl.job.backfill

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
            Handler(ref())
        }
    }
    listener<ApplicationReadyEvent> {
        ref<CommonConfiguration>().initialize(it.applicationContext as GenericApplicationContext)
    }
    webFlux {
        port = (env.getProperty("server.port") ?: "8080").toInt()
        router {
            val handler = ref<Handler>()
            GET("/games-for-season") {
                handler.gamesForSeason().apply(it)
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