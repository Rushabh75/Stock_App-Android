package com.example.hw4.ui.theme;

import com.example.hw4.PortfolioItem;
import com.example.hw4.StockResponse;
import com.example.hw4.Wallet;
import com.example.hw4.WatchlistItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/watchlist/{userId}")
    Call<List<WatchlistItem>> getWatchlist(@Path("userId") String userId);

    @POST("api/watchlist")
    Call<Void> addToWatchlist(@Body WatchlistItem watchlistItem);

    @DELETE("api/watchlist/{userId}/{symbol}")
    Call<Void> removeFromWatchlist(@Path("userId") String userId, @Path("symbol") String symbol);

    @GET("query?function=GLOBAL_QUOTE")
    Call<StockResponse> getGlobalQuote(@Query("symbol") String symbol, @Query("apikey") String apiKey);

    @GET("/api/portfolio/{userId}")
    Call<List<PortfolioItem>> getPortfolio(@Path("userId") String userId);

    @POST("/api/portfolio/buy")
    Call<Void> buyStock(@Body PortfolioItem portfolioItem);

    @POST("/api/portfolio/sell")
    Call<Void> sellStock(@Body PortfolioItem portfolioItem);
    @GET("/api/wallet/{userId}")
    Call<Wallet> getWallet(@Path("userId") String userId);


}