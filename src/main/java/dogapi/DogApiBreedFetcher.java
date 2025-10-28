package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    // Add the missing endpoint constant used to query sub-breeds from dog.ceo
    private static final String SUB_BREED_ENDPOINT = "https://dog.ceo/api/breed/%s/list";

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        if (breed == null || breed.isBlank()) {
            throw new BreedNotFoundException(String.valueOf(breed));
        }
        String url = String.format(Locale.ROOT, SUB_BREED_ENDPOINT, breed.toLowerCase(Locale.ROOT));
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new BreedNotFoundException(breed);
            }
            String body = response.body().string();
            JSONObject obj = new JSONObject(body);
            String status = obj.optString("status", "").toLowerCase(Locale.ROOT);
            if (!"success".equals(status)) {
                throw new BreedNotFoundException(breed);
            }
            JSONArray message = obj.getJSONArray("message");
            List<String> result = new ArrayList<>();
            for (int i = 0; i < message.length(); i++) {
                result.add(message.getString(i));
            }
            return Collections.unmodifiableList(result);
        } catch (IOException e) {
            throw new BreedNotFoundException(breed);
        }
    }
}