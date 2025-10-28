package main;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher delegate;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.delegate = Objects.requireNonNull(fetcher, "fetcher must not be null");
    }

    @Override
    public List<String> getSubBreeds(String breed) {
        String key = breed == null ? null : breed.toLowerCase(Locale.ROOT);
        if (key != null && cache.containsKey(key)) {
            return cache.get(key);
        }
        callsMade++;
        List<String> result = delegate.getSubBreeds(breed);
        if (key != null) {
            cache.put(key, Collections.unmodifiableList(new ArrayList<>(result)));
        }
        return result;
    }

    public int getCallsMade() {
        return callsMade;
    }
}