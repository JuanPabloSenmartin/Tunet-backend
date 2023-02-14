package tunet.model.forms;

import tunet.Util.JsonParser;

public class FilterForm {
    private final String token;
    private final Integer maxDistance;
    private final Integer rating;
    private final String[] genres;
    private final String[] dateRange;
    private final Double[] location;

    public FilterForm(String token, Integer maxDistance, Integer rating, String[] genres, Double[] location, String[] dateRange) {
        this.token = token;
        this.maxDistance = maxDistance;
        this.rating = rating;
        this.genres = genres;
        this.location = location;
        this.dateRange = dateRange;
    }

    public static FilterForm createFromJson(String body) {
        return JsonParser.fromJson(body, FilterForm.class);
    }

    public String getToken() {
        return token;
    }

    public Integer getMaxDistance() {
        return maxDistance;
    }

    public Integer getRating() {
        return rating;
    }

    public String[] getGenres() {
        return genres;
    }

    public double[] getLocation() {
        return new double[]{location[0], location[1]};
    }

    public String[] getDateRange() {
        return dateRange;
    }
}
