package tunet.model;

import tunet.Util.JsonParser;

public class FilterForm {
    private final String token;
    private final Integer[] range;
    private final Integer rating;
    private final String[] genres;
    private final Double[] location;

    public FilterForm(String token, Integer[] range, Integer rating, String[] genres, Double[] location) {
        this.token = token;
        this.range = range;
        this.rating = rating;
        this.genres = genres;
        this.location = location;
    }

    public static FilterForm createFromJson(String body) {
        return JsonParser.fromJson(body, FilterForm.class);
    }

    public String getToken() {
        return token;
    }

    public Integer[] getRange() {
        return range;
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
}
