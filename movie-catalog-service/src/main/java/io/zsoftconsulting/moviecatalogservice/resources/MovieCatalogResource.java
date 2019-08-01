package io.zsoftconsulting.moviecatalogservice.resources;

import io.zsoftconsulting.moviecatalogservice.models.CatalogItem;
import io.zsoftconsulting.moviecatalogservice.models.Movie;
import io.zsoftconsulting.moviecatalogservice.models.Rating;
import io.zsoftconsulting.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    // @Autowired means : search in all project for an instance of RestTemplate and give it to me
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
        // First API call to get all ratings movies for a user
        UserRating ratings = restTemplate.getForObject("http://rating-data-service/ratingsData/users/" + userId, UserRating.class);
        return ratings.getUserRatings().stream()
                .map(rating -> {
                    // Second API call to get movie info for each movie ID
                    Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
                    return new CatalogItem(movie.getName(), "des", rating.getRating());
                }).collect(Collectors.toList());
    }
}