package in.bugzy.ui.about;


import in.bugzy.data.GithubRepository;
import in.bugzy.data.model.GitUser;
import in.bugzy.data.model.Resource;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

public class AboutFragmentViewModel extends ViewModel {
    private GithubRepository mGithubRepository;
    private LiveData<Resource<List<GitUser>>> mContributorsLiveData;
    private MutableLiveData<Void> mLoadDataCommand;

    @Inject
    AboutFragmentViewModel(GithubRepository githubRepository) {
        mGithubRepository = githubRepository;

        mLoadDataCommand = new MutableLiveData<>();
        mContributorsLiveData = Transformations.switchMap(mLoadDataCommand, contributorsState -> {
            return mGithubRepository.getContributors(false);
        });
    }

    public void loadData() {
        mLoadDataCommand.setValue(null);
    }

    public LiveData<Resource<List<GitUser>>> getContributorsLiveData() {
        return mContributorsLiveData;
    }
}
