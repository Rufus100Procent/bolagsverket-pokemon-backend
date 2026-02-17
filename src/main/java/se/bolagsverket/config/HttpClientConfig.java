package se.bolagsverket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import se.bolagsverket.api.external.PokemonClient;

@Configuration
public class HttpClientConfig {

    @Bean
    public PokemonClient externalAPiC() {

        RestClient restClient = RestClient.builder()
                .baseUrl("https://pokeapi.co/api/v2")
                .build();

        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory
                        .builderFor(RestClientAdapter.create(restClient))
                        .build();

        return factory.createClient(PokemonClient.class);
    }
}
