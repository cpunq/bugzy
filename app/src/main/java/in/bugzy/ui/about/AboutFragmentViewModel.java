package in.bugzy.ui.about;


import in.bugzy.data.GithubRepository;
import in.bugzy.data.model.GitUser;
import in.bugzy.data.model.Resource;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

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
