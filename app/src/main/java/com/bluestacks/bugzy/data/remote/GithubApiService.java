package com.bluestacks.bugzy.data.remote;

import com.bluestacks.bugzy.data.model.GitUser;

import android.arch.lifecycle.LiveData;
import java.util.List;

import retrofit2.http.GET;

public interface GithubApiService {
    @GET("/repos/cpunq/bugzy/contributors")
    LiveData<ApiResponse<List<GitUser>>> getBugzyContributors();
}
