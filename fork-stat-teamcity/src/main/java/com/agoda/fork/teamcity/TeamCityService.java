package com.agoda.fork.teamcity;

import com.agoda.fork.teamcity.entities.Builds;
import com.agoda.fork.teamcity.entities.TestResult;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TeamCityService {
    @GET("/httpAuth/app/rest/buildTypes/id:{configuration}/builds/?locator=count:10&fields=build:id")
    Single<Builds> builds(@Path("configuration") String configuration);

    @GET("/httpAuth/app/rest/testOccurrences")
    Single<TestResult> tests(@Query("locator") String locator);
}
