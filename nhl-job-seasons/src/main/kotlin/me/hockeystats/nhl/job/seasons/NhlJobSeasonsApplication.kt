package me.hockeystats.nhl.job.seasons

import com.fasterxml.jackson.databind.ObjectMapper
import me.hockeystats.CommonConfiguration
import me.hockeystats.nhl.api.stats.StatsApi
import me.hockeystats.nhl.season.Seasons
import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webflux.webFlux

val app = application(WebApplicationType.REACTIVE) {
	beans {
		bean<CommonConfiguration>()
		bean {
			ref<CommonConfiguration>().nhlStatsApi(ref<ObjectMapper>())
		}
		bean {
			ref<CommonConfiguration>().seasons()
		}
		bean {
			Handler(ref<StatsApi>(), ref<Seasons>())
		}
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