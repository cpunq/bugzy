package in.bugzy.data.remote;

import in.bugzy.data.model.GitUser;

import androidx.lifecycle.LiveData;
import java.util.List;

import retrofit2.http.GET;

public interface GithubApiService {
    @GET("/repos/cpunq/bugzy/contributors")
    LiveData<ApiResponse<List<GitUser>>> getBugzyContributors();
}
