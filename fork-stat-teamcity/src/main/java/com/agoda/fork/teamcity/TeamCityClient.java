package com.agoda.fork.teamcity;

import io.reactivex.schedulers.Schedulers;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class TeamCityClient {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String ACCEPT_HEADER = "Accept";
    private static final String ACCEPT_TYPE = "application/json";

    private final TeamCityConfig config;

    private final Interceptor authInterceptor = chain -> {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header(AUTHORIZATION_HEADER, credentials())
                .header(ACCEPT_HEADER, ACCEPT_TYPE).build();
        return chain.proceed(request);
    };

    public TeamCityClient(TeamCityConfig config) {
        this.config = config;
    }

    private String credentials() {
        return Credentials.basic(config.getUser(), config.getPassword());
    }

    private OkHttpClient createHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(authInterceptor);
        if (config.isDebug()) {
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        return builder.build();
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder().client(createHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(config.getUrl())
                .build();
    }

    public TeamCityService teamCityService() {
        return createRetrofit().create(TeamCityService.class);
    }
}
