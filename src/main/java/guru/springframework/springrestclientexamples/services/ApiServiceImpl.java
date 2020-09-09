package guru.springframework.springrestclientexamples.services;

import guru.springframework.api.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ApiServiceImpl implements ApiService{

    private final String api_url;

    RestTemplate restTemplate;

    public ApiServiceImpl(@Value("${api.url}") String api_url, RestTemplate restTemplate) {
        this.api_url = api_url;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<User> getUsers(Integer limit) {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromUriString(api_url)
                .queryParam("limit", limit);
       List<User> userData = restTemplate.getForObject(uriComponentsBuilder.toUriString(), List.class);
        return userData;
    }

    @Override
    public Flux<User> getUsers(Mono<Integer> limit) {
        return WebClient.create(api_url)
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam("limit", limit.block()).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(List.class))
                .flatMapIterable(list -> list);
    }
}
