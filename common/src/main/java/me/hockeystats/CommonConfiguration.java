package me.hockeystats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import com.jmethods.catatumbo.EntityManager;
import com.jmethods.catatumbo.EntityManagerFactory;
import me.hockeystats.nhl.api.stats.StatsApi;
import me.hockeystats.nhl.game.Games;
import me.hockeystats.nhl.season.Seasons;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class CommonConfiguration implements ApplicationContextInitializer<GenericApplicationContext> {

    @Override
    public void initialize(GenericApplicationContext context) {
        context.registerBean(EntityManager.class, this::entityManager);
        context.registerBean(StatsApi.class, () -> nhlStatsApi(context.getBean(ObjectMapper.class)));
        context.registerBean(Games.class, () -> games(context.getBean(EntityManager.class)));
        context.registerBean(Seasons.class, () -> seasons(context.getBean(EntityManager.class)));
    }

    @Bean
    public EntityManager entityManager() {
        EntityManagerFactory emf = EntityManagerFactory.getInstance();
        return emf.createDefaultEntityManager();
    }

    @Bean
    public StatsApi nhlStatsApi(ObjectMapper objectMapper) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://statsapi.web.nhl.com")
                .addCallAdapterFactory(ReactorCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        return retrofit.create(StatsApi.class);
    }

    @Bean
    public Seasons seasons(EntityManager entityManager) {
        return new Seasons(entityManager);
    }

    @Bean
    public Games games(EntityManager entityManager) {
        return new Games(entityManager);
    }
}
