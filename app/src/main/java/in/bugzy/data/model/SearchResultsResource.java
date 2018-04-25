package in.bugzy.data.model;


public class SearchResultsResource<T> {
    private String query;
    Resource<T> mResource;

    public SearchResultsResource(String query, Resource<T> resource) {
        this.query = query;
        mResource = resource;
    }

    public Resource<T> getResource() {
        return mResource;
    }

    public String getQuery() {
        return query;
    }
}
