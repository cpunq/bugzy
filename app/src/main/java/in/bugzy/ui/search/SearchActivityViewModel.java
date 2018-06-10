package in.bugzy.ui.search;

import in.bugzy.data.CasesRepository;
import in.bugzy.data.SearchSuggestionRepository;
import in.bugzy.data.model.RecentSearch;
import in.bugzy.data.model.SearchResultsResource;
import in.bugzy.data.model.SearchSuggestion;
import in.bugzy.data.remote.model.ListCasesData;
import in.bugzy.utils.SingleLiveEvent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;


public class SearchActivityViewModel extends ViewModel {
    public static final String TAG = SearchActivityViewModel.class.getName();
    private CasesRepository mCasesRepository;
    private MutableLiveData<String> mSearchLiveData = new MutableLiveData<>();
    private MediatorLiveData<SearchResultsResource<ListCasesData>> mSearchResponse;
    private SingleLiveEvent<Void> mClearSearchEvent = new SingleLiveEvent<>();
    private String mCurrentQuery = null;
    private LiveData<List<SearchSuggestion>> mSearchSuggestions;
    private SearchSuggestionRepository mSearchSuggestionRepository;
    private MutableLiveData<String> mSearchTextLive = new MutableLiveData<>();
    private SingleLiveEvent<String> mSearchChangeEvent = new SingleLiveEvent<>();

    private LiveData<List<RecentSearch>> mRecentSearches;

    @Inject
    public SearchActivityViewModel(CasesRepository repository, SearchSuggestionRepository ssRepository) {
        mCasesRepository = repository;
        mSearchSuggestionRepository = ssRepository;
        LiveData<SearchResultsResource<ListCasesData>> tempLiveData = Transformations
                .switchMap(mSearchLiveData, value -> {
                    return mCasesRepository.searchCases(value);
                });
        mSearchResponse = new MediatorLiveData<>();
        mSearchResponse.addSource(tempLiveData, v -> {
            if (v.getQuery().equals(mCurrentQuery)) {
                // If the query is equal to the current query, only then dispatch the event
                mSearchResponse.setValue(v);
            }
        });

        mSearchTextLive = new MutableLiveData<>();
        mSearchSuggestions = Transformations.switchMap(mSearchTextLive, query -> {

            // TODO: Do all of this on a background thread.
            String q = ""; // The query that will be finally submitted to mSearchTextLive
            if (!TextUtils.isEmpty(query) && query.length() > 0) {
                int lastPartStartingIndex = getCrucialIndex(query);
                q = query.substring(lastPartStartingIndex, query.length());
            }

            if (TextUtils.isEmpty(q)) {
                return AbsentLiveData.create();
            }
            return mSearchSuggestionRepository.search(q.trim());
        });

        mRecentSearches = Transformations.switchMap(mSearchTextLive, query -> {
            if (query.equals("")) {
                return mCasesRepository.getRecentSearches();
            }
            return AbsentLiveData.create();
        });
    }


    public void searchTextChanged(String query) {
        Log.d(TAG, "serachTextChagned");
        mSearchTextLive.setValue(query);
    }

    private int getCrucialIndex(String query) {
        int quotesOccurence = Math.max(query.split("'").length - 1, 0);
        int lastSpaceBeforeQuote = 0;
        if (quotesOccurence % 2 == 0) {
            lastSpaceBeforeQuote = query.lastIndexOf(" ");
        } else {
            int lastQuote = query.lastIndexOf("'");
            lastSpaceBeforeQuote = query.substring(0, lastQuote).lastIndexOf(" ");
        }
        return Math.max(lastSpaceBeforeQuote, 0);
    }

    public void search(String query) {
        mCurrentQuery = query;
        mSearchLiveData.setValue(query);
    }

    public void clearSearch() {
        mCurrentQuery = null;
        mClearSearchEvent.call();
    }

    public void searchSuggestionSelected(SearchSuggestion suggestion) {
        String query = mSearchTextLive.getValue();
        int lastPartStartingIndex = getCrucialIndex(query);
        String p1 = query.substring(0, lastPartStartingIndex);
        if (!TextUtils.isEmpty(p1)) {
            mSearchChangeEvent.setValue(p1 + " " + suggestion.getText() +" ");
        } else {
            mSearchChangeEvent.setValue(suggestion.getText() + " ");
        }
    }

    public LiveData<SearchResultsResource<ListCasesData>> getSearchResponse() {
        return mSearchResponse;
    }

    public LiveData<List<SearchSuggestion>> getSearchSuggestions() {
        return mSearchSuggestions;
    }

    public SingleLiveEvent<Void> getClearSearchEvent() {
        return mClearSearchEvent;
    }

    public SingleLiveEvent<String> getSearchChangeEvent() {
        return mSearchChangeEvent;
    }

    public LiveData<List<RecentSearch>> getRecentSearches() {
        return mRecentSearches;
    }
}
