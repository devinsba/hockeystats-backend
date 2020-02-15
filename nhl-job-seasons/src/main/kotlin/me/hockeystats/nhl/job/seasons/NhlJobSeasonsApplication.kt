package me.hockeystats.nhl.job.seasons

import me.hockeystats.CommonConfiguration
import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webflux.webFlux

val app = application(WebApplicationType.REACTIVE) {
	beans {
		bean<CommonConfiguration>()
		bean<Handler>()
	}
	webFlux {
		port = (env.getProperty("server.port") ?: "8080").toInt()
		router {
			val handler = ref<Handler>()
			GET("/", handler::handle)
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