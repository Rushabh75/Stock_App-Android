package com.example.hw4;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hw4.ui.theme.ApiService;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Search.OnDataFetchedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RelativeLayout headerLayout;
    private RelativeLayout searchLayout;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<NewsItem> newsList;
    private LinearLayout stockDetailsContainer;
    private LinearLayout charts1;
    private LinearLayout portfolio;
    private LinearLayout Stats;
    private LinearLayout About;
    private ImageButton btnSearch;
    private ImageButton btnBack;
    private ImageButton btnClear;
    private ImageView imageButton3;
    private AutoCompleteTextView etSearch;
    private AutoCompleteStockAdapter adapter;
    private Search search;

    // TextViews for stock details
    private TextView tvStockTicker;
    private TextView tvCompanyName;
    private TextView tvStockPrice;
    private ImageView ivPriceChangeIcon;
    private TextView tvPriceChange;
    private Stack<String> searchHistory = new Stack<>();
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextView tvOpenPrice, tvHighPrice, tvLowPrice, tvPrevClose;
    private TextView tvIPOStartDate, tvIndustry, tvWebpage;
    private LinearLayout llCompanyPeers;
    private LinearLayout sentiments;
    private TextView tvCompanysName, tvTotalMSRP, tvTotalChange, tvPositiveMSRP, tvNegativeMSRP,tvPositiveChange,tvNegativeChange;

    public String symbol;
    private LinearLayout recommendation;
    private LinearLayout eps;
    private RecyclerView watchlistRecyclerView;
    private WatchlistAdapter watchlistAdapter;
    private List<WatchlistItem> watchlistItems = new ArrayList<>();
    private ApiService apiService;
    private String currentProfileResponse; // JSON response for profile
    private String currentQuoteResponse;
    private RecyclerView portfolioRecyclerView;
    private PortfolioAdapter portfolioAdapter;
    private List<PortfolioItem> portfolioItems = new ArrayList<>();
    private TextView tvSharesOwned, tvAvgCost, tvTotalCost, tvMarketValue, tvChange;
    private final Handler quoteUpdateHandler = new Handler(Looper.getMainLooper());
    private Runnable quoteUpdateRunnable;
    private Handler handler = new Handler();

    @Override
    protected void onResume() {
        super.onResume();
        startRepeatingTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRepeatingTask();
    }

    private void startRepeatingTask() {
        quoteUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                search.fetchStockQuote(symbol);
                quoteUpdateHandler.postDelayed(this, 15000); // Schedule the next execution in 15 seconds
            }
        };
        quoteUpdateHandler.post(quoteUpdateRunnable);
    }

    private void stopRepeatingTask() {
        quoteUpdateHandler.removeCallbacks(quoteUpdateRunnable);
    }
    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            // Call fetchPortfolio first
            fetchPortfolio();

            // Then, with a delay of 1 second, call fetchWallet
            handler.postDelayed(() -> {
                fetchWallet();
            }, 1000);

            // Re-post this Runnable to run again in 15 seconds
            handler.postDelayed(this, 15000);
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks and messages
        handler.removeCallbacks(updateTask);
    }


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout); // Make sure this is the name of your layout file

        ProgressBar progressBar = findViewById(R.id.progressBar);
        headerLayout = findViewById(R.id.header_layout);
        searchLayout = findViewById(R.id.search_layout);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        btnClear = findViewById(R.id.btnClear);
        imageButton3 = findViewById(R.id.imageButton3); // Reference to the favorite icon
        etSearch = findViewById(R.id.etSearch);
        setupStarButton();

        tvStockTicker = findViewById(R.id.tvStockTicker);
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvStockPrice = findViewById(R.id.tvStockPrice);
        ivPriceChangeIcon = findViewById(R.id.ivPriceChangeIcon);
        tvPriceChange = findViewById(R.id.tvPriceChange);
        stockDetailsContainer = findViewById(R.id.stockDetailsContainer);
        charts1 = findViewById(R.id.charts1);
        portfolio = findViewById(R.id.portfolio);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);
        setupViewPager(viewPager, String.valueOf(tvStockTicker));

        Stats = findViewById(R.id.Stats);
        tvOpenPrice = findViewById(R.id.tvOpenPrice);
        tvHighPrice = findViewById(R.id.tvHighPrice);
        tvLowPrice = findViewById(R.id.tvLowPrice);
        tvPrevClose = findViewById(R.id.tvPrevClose);

        About = findViewById(R.id.About);
        tvIPOStartDate = findViewById(R.id.tvIPOStartDate);
        tvIndustry = findViewById(R.id.tvIndustry);
        tvWebpage = findViewById(R.id.tvWebpage);
        llCompanyPeers = findViewById(R.id.llCompanyPeers);

        sentiments = findViewById(R.id.sentiments);
        tvCompanysName = findViewById(R.id.tvCompanysName);
        tvTotalMSRP = findViewById(R.id.tvTotalMSRP);
        tvTotalChange = findViewById(R.id.tvTotalChange);
        tvPositiveMSRP = findViewById(R.id.tvPositiveMSRP);
        tvNegativeMSRP = findViewById(R.id.tvNegativeMSRP);
        tvPositiveChange = findViewById(R.id.tvPositiveChange);
        tvNegativeChange = findViewById(R.id.tvNegativeChange);

        Button btnTrade = findViewById(R.id.btnTrade);

        recyclerView = findViewById(R.id.recycler_view); // Replace with your actual ID
        recommendation = findViewById(R.id.recommedation);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, newsList);

        TextView dateTextView = findViewById(R.id.dateTextView);
        dateTextView.setText(getCurrentFormattedDate());


        eps = findViewById(R.id.eps);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
                    // Hide the ProgressBar after 2 seconds
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    findViewById(R.id.dateTextView).setVisibility(View.VISIBLE);
                    findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
                    findViewById(R.id.fav).setVisibility(View.VISIBLE);
                    findViewById(R.id.port).setVisibility(View.VISIBLE);
                    findViewById(R.id.Wallet).setVisibility(View.VISIBLE);
                    findViewById(R.id.tvPoweredBy).setVisibility(View.VISIBLE);
                    findViewById(R.id.portfolioRecyclerView).setVisibility(View.VISIBLE);
        },2000);

//        new TabLayoutMediator(tabLayout, viewPager,
//                (tab, position) -> tab.setText(position == 0 ? "Hourly" : "Historical")).attach();
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    // Here you can set the icons for the tabs
                    if (position == 0) {
                        tab.setIcon(R.drawable.chart_hour);
                    } else if (position == 1) {
                        tab.setIcon(R.drawable.chart_historical);
                    }
                }).attach();
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null && tab.getIcon() != null) {
                Drawable icon = DrawableCompat.wrap(tab.getIcon());
                DrawableCompat.setTintList(icon, ContextCompat.getColorStateList(this, R.color.tab_icon_color_selector));
                tab.setIcon(icon);
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.blue);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

                // Load the chart for the selected tab using the current symbol
                if (symbol != null && !symbol.isEmpty()) {
                    ChartFragment fragment = (ChartFragment) getSupportFragmentManager().findFragmentById(R.id.view_pager);
                    if (fragment != null) {
                        fragment.loadChart(tab.getPosition() == 0 ? "Hourly" : "Historical", symbol);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.black);
                Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optionally reload or refresh the chart on reselection
            }
        });

        adapter = new AutoCompleteStockAdapter(this, android.R.layout.simple_dropdown_item_1line);
        etSearch.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> {
            headerLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.VISIBLE);
        });

        btnBack.setOnClickListener(v -> {
            if (!searchHistory.isEmpty()) {
                searchHistory.pop();  // Remove the current view state
                if (!searchHistory.isEmpty()) {
                    String previousSearch = searchHistory.pop();
                    performSearch(previousSearch);
                    etSearch.setText(previousSearch);
                } else {
                    resetSearchView();
                    fetchWatchlist();
                }
            } else {
                resetSearchView();
            }
        });

        btnClear.setOnClickListener(v -> etSearch.setText(""));  // Clear search text

        etSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selection = ((TextView) view).getText().toString().split(" - ")[0];
            symbol = selection;
            etSearch.setText(selection);
            performSearch(selection);
        });
        TextView tvPoweredBy = findViewById(R.id.tvPoweredBy);
        tvPoweredBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFinnhubWebsite();
            }
        });
        // Initialize Search class to handle API calls
        search = new Search(this, this);

        apiService = ApiClient.getClient().create(ApiService.class);
        initializeViews();
        setupRecyclerView();
        setupItemTouchHelper();
        fetchWatchlist();
        setupStarButton();
        setupPortfolioRecyclerView();
        setupPortfolioItemTouchHelper();
        fetchPortfolio();
        new Handler().postDelayed(() -> {
            fetchWallet();
        },1000);
        handler.post(updateTask);


    }
    private String getCurrentFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
        return dateFormat.format(new Date());  // Gets current date and formats it
    }
    private void openFinnhubWebsite() {
        String url = "https://www.finnhub.io";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
    private void initializeViews() {
        // Initialize TextViews or other UI components
        tvSharesOwned = findViewById(R.id.tvSharesOwned);
        tvAvgCost = findViewById(R.id.tvAvgCost);
        tvTotalCost = findViewById(R.id.tvTotalCost);
        tvMarketValue = findViewById(R.id.tvMarketValue);
        tvChange = findViewById(R.id.tvChange);
    }

    private void fetchPortfolioForSymbol(String symbol) {
        apiService.getPortfolio("rushabh75").enqueue(new Callback<List<PortfolioItem>>() {
            @Override
            public void onResponse(Call<List<PortfolioItem>> call, Response<List<PortfolioItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (PortfolioItem item : response.body()) {
                        if (item.getSymbol().equalsIgnoreCase(symbol)) {
                            updatePortfolioUI(item);
                            return;
                        }
                    }
                    resetPortfolioUI(); // Reset UI if no matching item is found
                } else {
                    resetPortfolioUI();
                }
            }

            @Override
            public void onFailure(Call<List<PortfolioItem>> call, Throwable t) {
                Log.e(TAG, "Error loading portfolio", t);
                Toast.makeText(MainActivity.this, "Error loading portfolio", Toast.LENGTH_SHORT).show();
                resetPortfolioUI();
            }
        });
    }

    private void updatePortfolioUI(PortfolioItem item) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (currentQuoteResponse == null) {
                Log.e(TAG, "Current quote response is null");
                resetPortfolioUI();
                return;
            }

            try {
                JSONObject profileJson = new JSONObject(currentProfileResponse);
                JSONObject quoteJson = new JSONObject(currentQuoteResponse);
                if (!quoteJson.has("c")) {
                    Log.e(TAG, "Price key 'c' not found in response");
                    resetPortfolioUI();
                    return;
                }
                String companyname = profileJson.getString("name");
                double currentPrice = quoteJson.getDouble("c");  // Ensuring "c" key exists
                double avgCost = item.getQuantity() > 0 ? item.getTotalCost() / item.getQuantity() : 0;
                double marketValue = currentPrice;
                double change = marketValue - avgCost;

                tvSharesOwned.setText(String.format(Locale.getDefault(), "%d", item.getQuantity()));
                tvAvgCost.setText(String.format(Locale.getDefault(), "$%.2f", avgCost));
                tvTotalCost.setText(String.format(Locale.getDefault(), "$%.2f", item.getTotalCost()));
                tvMarketValue.setText(String.format(Locale.getDefault(), "$%.2f", marketValue));
                tvChange.setText(String.format(Locale.getDefault(), "$%.2f", change));

                int colorId = change >= 0 ? R.color.green : R.color.red;
                tvMarketValue.setTextColor(getResources().getColor(colorId, null));
                tvChange.setTextColor(getResources().getColor(colorId, null));
                Button btnTrade = findViewById(R.id.btnTrade);
                btnTrade.setOnClickListener(v -> {
                    double cashBalance = 25000.00; // Example cash balance, fetch this from a real source
                    showTradeDialog(companyname, item.getSymbol(), currentPrice, "rushabh75");
                });
            } catch (JSONException e) {
                Log.e(TAG, "JSON parsing error in updatePortfolioUI", e);
                resetPortfolioUI();
            }
        }, 2000);  // 2000 milliseconds = 2 seconds
    }

    private void resetPortfolioUI() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                JSONObject profileJson = new JSONObject(currentProfileResponse);
                JSONObject quoteJson = new JSONObject(currentQuoteResponse);
                if (!quoteJson.has("c")) {
                    Log.e(TAG, "Price key 'c' not found in response");
                    resetPortfolioUI();
                    return;
                }
                String companyname = profileJson.getString("name");
                double currentPrice = quoteJson.getDouble("c");  // Ensuring "c" key exists
                double avgCost = 0;
                double marketValue = currentPrice;
                double change = marketValue - avgCost;

                Button btnTrade = findViewById(R.id.btnTrade);
                btnTrade.setOnClickListener(v -> {
                    double cashBalance = 25000.00; // Example cash balance, fetch this from a real source
                    showTradeDialog(companyname, symbol, currentPrice, "rushabh75");
                });
            } catch (JSONException e) {
                Log.e(TAG, "JSON parsing error in updatePortfolioUI", e);
                resetPortfolioUI();
            }
            tvSharesOwned.setText("0");
            tvAvgCost.setText("$0.00");
            tvTotalCost.setText("$0.00");
            tvMarketValue.setText("$0.00");
            tvChange.setText("$0.00");
            tvMarketValue.setTextColor(getResources().getColor(android.R.color.black));
            tvChange.setTextColor(getResources().getColor(android.R.color.black));
        }, 2000);
    }

    private void fetchPortfolio() {
        String userId = "rushabh75"; // Example user ID
        Call<List<PortfolioItem>> call = apiService.getPortfolio(userId);
        call.enqueue(new Callback<List<PortfolioItem>>() {
            @Override
            public void onResponse(Call<List<PortfolioItem>> call, Response<List<PortfolioItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    portfolioItems.clear();
                    portfolioItems.addAll(response.body());
                    updateStockData(); // Fetch stock data for each item
                } else {
                    Log.e(TAG, "Failed to load portfolio items: " + response.message());
                    Toast.makeText(MainActivity.this, "Failed to load portfolio items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PortfolioItem>> call, Throwable t) {
                Log.e(TAG, "Error loading portfolio", t);
                Toast.makeText(MainActivity.this, "Error loading portfolio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPortfolioRecyclerView() {
        portfolioRecyclerView = findViewById(R.id.portfolioRecyclerView);
        portfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        portfolioAdapter = new PortfolioAdapter(this, portfolioItems);
        portfolioRecyclerView.setAdapter(portfolioAdapter);
    }

    private void updateStockData() {
        for (PortfolioItem item : portfolioItems) {
            String url = "https://backend3-419023.wl.r.appspot.com/api/stock-quote?symbol=" + item.getSymbol();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            double current = response.getDouble("c");
                            item.setCurrent(current);
                            portfolioAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                Log.e(TAG, "Error fetching stock quote", error);
            });
            Volley.newRequestQueue(this).add(jsonObjectRequest);
        }
    }
    private void setupPortfolioItemTouchHelper() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) { // Disable swiping by setting swipeDirs to 0
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(portfolioItems, fromPosition, toPosition);
                portfolioAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // No operation for swipe
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(portfolioRecyclerView);
    }

    private void setupRecyclerView() {
        watchlistRecyclerView = findViewById(R.id.recyclerView);
        watchlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        watchlistAdapter = new WatchlistAdapter(this, watchlistItems);
        watchlistRecyclerView.setAdapter(watchlistAdapter);
    }

    private void setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(watchlistItems, fromPosition, toPosition);
                watchlistAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position < watchlistItems.size()) {
                    WatchlistItem itemToRemove = watchlistItems.get(position);
                    removeFromWatchlist(itemToRemove.getUserId(), itemToRemove.getSymbol(), position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 20; // Determines how much the background is offset from the boundaries of the item view

                ColorDrawable background = new ColorDrawable(Color.RED);
                Drawable deleteIcon = ContextCompat.getDrawable(getBaseContext(), R.drawable.delete);
                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                if (dX > 0) { // Swiping to the right
                    int iconLeft = itemView.getLeft() + iconMargin;
                    int iconRight = iconLeft + deleteIcon.getIntrinsicWidth();
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
                } else if (dX < 0) { // Swiping to the left
                    int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // View is unSwiped
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
                deleteIcon.draw(c);
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(watchlistRecyclerView);
    }
    private void setupStarButton() {
        imageButton3 = findViewById(R.id.imageButton3);
        imageButton3.setImageResource(R.drawable.star_border);
        imageButton3.setOnClickListener(v -> toggleFavorite());
    }

    private void toggleFavorite() {
        if (isFavorite(symbol)) {
            removeFromFavorites(symbol);
        } else {
            addToFavorites(symbol, currentProfileResponse, currentQuoteResponse);
        }
    }

    private boolean isFavorite(String symbol) {
        return watchlistItems.stream().anyMatch(item -> item.getSymbol().equalsIgnoreCase(symbol));
    }
    private WatchlistItem createWatchlistItemFromDetails(String profileResponse, String quoteResponse) throws JSONException {
        JSONObject profileJson = new JSONObject(profileResponse);
        JSONObject quoteJson = new JSONObject(quoteResponse);

        String symbol = profileJson.getString("ticker");
        String name = profileJson.getString("name");
        double currentPrice = quoteJson.getDouble("c");
        double change = quoteJson.getDouble("d");
        double changePercent = quoteJson.getDouble("dp");

        return new WatchlistItem("rushabh75", symbol, name, currentPrice, change, changePercent);
    }

    public void addToFavorites(String symbol, String profileResponse, String quoteResponse) {
        try {
            WatchlistItem newItem = createWatchlistItemFromDetails(profileResponse, quoteResponse);

            Call<Void> call = apiService.addToWatchlist(newItem);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        watchlistItems.add(newItem);
                        imageButton3.setImageResource(R.drawable.full_star);
                        Toast.makeText(MainActivity.this, symbol + " added to favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to add symbol: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed to add symbol", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing stock details", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "JSON parsing error", e);
        }
    }
    private void removeFromFavorites(String symbol) {
        Call<Void> call = apiService.removeFromWatchlist("yourUserId", symbol);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    watchlistItems.removeIf(item -> item.getSymbol().equals(symbol));
                    imageButton3.setImageResource(R.drawable.star_border);
                    Toast.makeText(MainActivity.this, "Symbol removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to remove symbol", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to remove symbol", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromWatchlist(String userId, String symbol, int position) {
        Call<Void> call = apiService.removeFromWatchlist(userId, symbol);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    watchlistItems.remove(position);
                    watchlistAdapter.notifyItemRemoved(position);
                    watchlistAdapter.notifyItemRangeChanged(position, watchlistItems.size());
                } else {
                    watchlistAdapter.notifyItemChanged(position); // Revert the view if delete failed
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                watchlistAdapter.notifyItemChanged(position); // Revert the view on network failure
            }
        });
    }
    private void updateStockDataForWatchlist() {
        for (WatchlistItem item : watchlistItems) {
            fetchLiveData(item.getSymbol());
        }
    }

    private void fetchLiveData(String symbol) {
        Call<StockResponse> call = apiService.getGlobalQuote(symbol, "YOUR_API_KEY");
        call.enqueue(new Callback<StockResponse>() {
            @Override
            public void onResponse(Call<StockResponse> call, Response<StockResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StockResponse.GlobalQuote quote = response.body().getGlobalQuote();
                    updateWatchlistItem(symbol, quote.getPrice(), quote.getChange(), quote.getChangePercent());
                }
            }

            @Override
            public void onFailure(Call<StockResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to fetch live data for: " + symbol, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateWatchlistItem(String symbol, double price, double change, double changePercent) {
        for (WatchlistItem item : watchlistItems) {
            if (item.getSymbol().equalsIgnoreCase(symbol)) {
                item.setCurrentPrice(price);
                item.setChange(change);
                item.setChangePercent(changePercent);
                runOnUiThread(() -> watchlistAdapter.notifyDataSetChanged());
                break;
            }
        }
    }

    private void fetchWatchlist() {
        Call<List<WatchlistItem>> call = apiService.getWatchlist("rushabh75");
        call.enqueue(new Callback<List<WatchlistItem>>() {
            @Override
            public void onResponse(Call<List<WatchlistItem>> call, Response<List<WatchlistItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    watchlistItems.clear();
                    watchlistItems.addAll(response.body());
                    watchlistAdapter.notifyDataSetChanged();
                    updateStockDataForWatchlist(); // Fetch live data once watchlist is loaded
                }
            }

            @Override
            public void onFailure(Call<List<WatchlistItem>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to load watchlist", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchWallet() {
        Call<Wallet> call = apiService.getWallet("rushabh75");  // Ensure you have this method in ApiService
        call.enqueue(new Callback<Wallet>() {
            @Override
            public void onResponse(Call<Wallet> call, Response<Wallet> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Wallet wallet = response.body();
                    displayCashBalance(wallet.getBalance());
                    calculateAndDisplayNetWorth(wallet.getBalance());
                    fetchPortfolio();
                } else {
                    Log.e(TAG, "Failed to load wallet: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Wallet> call, Throwable t) {
                Log.e(TAG, "Error loading wallet", t);
            }
        });
    }
    private void displayCashBalance(double balance) {
        TextView tvCashBalance = findViewById(R.id.tvCashBalance);
        tvCashBalance.setText(String.format(Locale.US, "$%.2f", balance));
    }
    private void calculateAndDisplayNetWorth(double cashBalance) {
        double netWorth = cashBalance;  // Start with the cash balance

        for (PortfolioItem item : portfolioItems) {
            netWorth += item.getFinalcost();  // Sum up the total cost of each portfolio item
        }

        TextView tvNetWorth = findViewById(R.id.tvNetWorth);
        tvNetWorth.setText(String.format(Locale.US, "$%.2f", netWorth));
    }


    public void performSearch(String query) {
        symbol = query;
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        search.fetchStockData(query);
        findViewById(R.id.recyclerView).setVisibility(View.GONE);
        findViewById(R.id.dateTextView).setVisibility(View.GONE);
        findViewById(R.id.fav).setVisibility(View.GONE);
        findViewById(R.id.port).setVisibility(View.GONE);
        findViewById(R.id.Wallet).setVisibility(View.GONE);
        findViewById(R.id.tvPoweredBy).setVisibility(View.GONE);
        findViewById(R.id.portfolioRecyclerView).setVisibility(View.GONE);
        headerLayout.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.GONE);
        etSearch.setText("");
        imageButton3.setVisibility(View.INVISIBLE);
        stockDetailsContainer.setVisibility(View.INVISIBLE);
        charts1.setVisibility(View.INVISIBLE);
        portfolio.setVisibility(View.INVISIBLE);
        Stats.setVisibility(View.INVISIBLE);
        About.setVisibility(View.INVISIBLE);
        sentiments.setVisibility(View.VISIBLE);
//        recommendation.setVisibility(View.INVISIBLE);
//        eps.setVisibility(View.INVISIBLE);
        findViewById(R.id.News).setVisibility(View.GONE);
        findViewById(R.id.chart_container).setVisibility(View.GONE);
        findViewById(R.id.recycler_view).setVisibility(View.GONE);
        new Handler().postDelayed(() -> {
            // Hide the ProgressBar after 2 seconds
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            recyclerView.setAdapter(newsAdapter);
            btnClear.setVisibility(View.INVISIBLE);
            imageButton3.setVisibility(View.VISIBLE);
            stockDetailsContainer.setVisibility(View.VISIBLE);
            charts1.setVisibility(View.VISIBLE);
            portfolio.setVisibility(View.VISIBLE);
            Stats.setVisibility(View.VISIBLE);
            About.setVisibility(View.VISIBLE);
            sentiments.setVisibility(View.VISIBLE);
            searchHistory.push(query);
            recommendation.setVisibility(View.GONE);
            eps.setVisibility(View.GONE);
            headerLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
            etSearch.setText(query);
            findViewById(R.id.News).setVisibility(View.VISIBLE);
            findViewById(R.id.chart_container).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
            findViewById(R.id.recycler_view).setVisibility(View.VISIBLE);
            fetchPortfolioForSymbol(query);
            setupStarButton();

            // Continue setting up the rest of the UI elements

        }, 2000); // 2000 milliseconds = 2 seconds
    }

    private void resetSearchView() {
        headerLayout.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.GONE);
        etSearch.setText("");
        imageButton3.setVisibility(View.INVISIBLE);
        stockDetailsContainer.setVisibility(View.GONE);
        charts1.setVisibility(View.GONE);
        portfolio.setVisibility(View.GONE);
        Stats.setVisibility(View.GONE);
        About.setVisibility(View.GONE);
        sentiments.setVisibility(View.GONE);
        recommendation.setVisibility(View.GONE);
        eps.setVisibility(View.GONE);
        findViewById(R.id.News).setVisibility(View.GONE);
        findViewById(R.id.chart_container).setVisibility(View.GONE);
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
        findViewById(R.id.recycler_view).setVisibility(View.GONE);
        findViewById(R.id.recyclerView).setVisibility(View.GONE);
        findViewById(R.id.fav).setVisibility(View.GONE);
        findViewById(R.id.port).setVisibility(View.GONE);
        findViewById(R.id.Wallet).setVisibility(View.GONE);
        findViewById(R.id.tvPoweredBy).setVisibility(View.GONE);
        findViewById(R.id.portfolioRecyclerView).setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            // Hide the ProgressBar after 2 seconds
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            findViewById(R.id.dateTextView).setVisibility(View.VISIBLE);
            findViewById(R.id.fav).setVisibility(View.VISIBLE);
            findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
            findViewById(R.id.port).setVisibility(View.VISIBLE);
            findViewById(R.id.Wallet).setVisibility(View.VISIBLE);
            findViewById(R.id.tvPoweredBy).setVisibility(View.VISIBLE);
            findViewById(R.id.portfolioRecyclerView).setVisibility(View.VISIBLE);
            fetchPortfolio();
            fetchWallet();
        },2000);
    }
    public void updateStockDetails(String profileResponse, String quoteResponse) {
        runOnUiThread(() -> {
            try {
                // Fetch the stored API responses
//                String profileResponse = search.getApiResponse("StockProfile");
//                String quoteResponse = search.getApiResponse("StockQuote");

                // Parse the JSON responses
                JSONObject profileJson = new JSONObject(profileResponse);
                JSONObject quoteJson = new JSONObject(quoteResponse);

                Log.d(TAG, "Profile JSON: " + profileJson);
                Log.d(TAG, "Quote JSON: " + quoteJson);

                // Update the UI with the fetched data
                tvStockTicker.setText(profileJson.getString("ticker"));
                tvCompanyName.setText(profileJson.getString("name"));
                tvStockPrice.setText(String.format("$%.2f", quoteJson.getDouble("c")));

                double change = quoteJson.getDouble("d");
                double changePercent = quoteJson.getDouble("dp");
                tvPriceChange.setText(String.format("%+.2f (%+.2f%%)", change, changePercent));

                if (change >= 0) {
                    ivPriceChangeIcon.setImageResource(R.drawable.trending_up);
                    tvPriceChange.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    ivPriceChangeIcon.setImageResource(R.drawable.trending_down);
                    tvPriceChange.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }


            } catch (JSONException e) {
                Log.e(TAG, "JSON parsing error", e);
            }
        });
    }
    public void stat_update(String quoteResponse){
        runOnUiThread(() -> {
            try {
                JSONObject quoteJson = new JSONObject(quoteResponse);

                tvOpenPrice.setText(String.format("$%.2f", quoteJson.getDouble("o")));
                tvHighPrice.setText(String.format("$%.2f", quoteJson.getDouble("h")));
                tvLowPrice.setText(String.format("$%.2f", quoteJson.getDouble("l")));
                tvPrevClose.setText(String.format("$%.2f", quoteJson.getDouble("pc")));

            } catch (JSONException e) {
                Log.e(TAG, "JSON parsing error", e);
            }
        });
    }
    public void about_update(String profileResponse, String StockPeersResponse) {
        runOnUiThread(() -> {
            try {
                JSONObject profileJson = new JSONObject(profileResponse);
                JSONArray stockPeersArray = new JSONArray(StockPeersResponse);
                String[] companyPeers = new String[stockPeersArray.length()];

                for (int i = 0; i < stockPeersArray.length(); i++) {
                    companyPeers[i] = stockPeersArray.getString(i);
                }

                tvIPOStartDate.setText(profileJson.getString("ipo"));
                tvIndustry.setText(profileJson.getString("finnhubIndustry"));

                // Set the webpage as a clickable link with underline
                String webpageUrl = profileJson.getString("weburl");
                SpannableString webpageSpannable = new SpannableString(webpageUrl);
//                webpageSpannable.setSpan(new UnderlineSpan(), 0, webpageUrl.length(), 0);
                tvWebpage.setText(webpageSpannable);
//                tvWebpage.setTextColor(getResources().getColor(R.color.blue)); // Color for the link
                tvWebpage.setMovementMethod(LinkMovementMethod.getInstance());
                tvWebpage.setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(webpageUrl));
                    startActivity(intent);
                });

                setCompanyPeers(companyPeers);

            } catch (JSONException e) {
                Log.e(TAG, "JSON parsing error", e);
            }
        });
    }

    private void setCompanyPeers(String[] peers) {
        llCompanyPeers.removeAllViews(); // Clear previous views if any
        for (String peer : peers) {
            SpannableString peerSpannable = new SpannableString(peer.trim());
            peerSpannable.setSpan(new UnderlineSpan(), 0, peer.length(), 0);
            TextView textView = new TextView(this);
            textView.setText(peerSpannable);
            textView.setTextColor(getResources().getColor(R.color.blue)); // Color for the link
            textView.setPadding(18, 0, 18, 0); // Add padding for spacing
            textView.setOnClickListener(view -> performSearch(peer.trim()));
            llCompanyPeers.addView(textView);
        }
    }

    private void InsiderSentimentData(String profileResponse, String SentimentsResponse) {
        runOnUiThread(() -> {
            try {
                JSONObject profileJson = new JSONObject(profileResponse);
                JSONObject sentimentsObject = new JSONObject(SentimentsResponse);
                JSONArray dataArray = sentimentsObject.getJSONArray("data");

                double totalMSRP = 0.0;
                double totalChange = 0.0;
                double positiveMSRP = 0.0;
                double negativeMSRP = 0.0;
                double positiveChange = 0.0;
                double negativeChange = 0.0;

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject sentimentObject = dataArray.getJSONObject(i);
                    double msrp = sentimentObject.getDouble("mspr");
                    double change = sentimentObject.getDouble("change");

                    totalMSRP += msrp;
                    totalChange += change;

                    if (msrp > 0) {
                        positiveMSRP += msrp;
                    } else {
                        negativeMSRP += msrp;
                    }
                    if (change > 0) {
                        positiveChange += change;
                    } else {
                        negativeChange += change;
                    }
                }

                tvCompanysName.setText(profileJson.getString("name"));
                tvTotalMSRP.setText(String.format(Locale.getDefault(), "%.2f", totalMSRP));
                tvTotalChange.setText(String.format(Locale.getDefault(), "%.2f", totalChange));
                tvPositiveMSRP.setText(String.format(Locale.getDefault(), "%.2f", positiveMSRP));
                tvNegativeMSRP.setText(String.format(Locale.getDefault(), "%.2f", negativeMSRP));
                tvPositiveChange.setText(String.format(Locale.getDefault(), "%.2f", positiveChange));
                tvNegativeChange.setText(String.format(Locale.getDefault(), "%.2f", negativeChange));

            } catch (JSONException e) {
                Log.e(TAG, "JSON parsing error", e);
            }
        });
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        public NewsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Do something when the news item is clicked
                    NewsItem newsItem = newsList.get(getAdapterPosition());
                    Log.d(TAG, "Clicked 1");
                    // For example, open a dialog with the news summary
                }
            });
        }
    }
    private List<NewsItem> parseNewsApiResponse(String response) {
        List<NewsItem> newsItems = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < 10; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String headline = jsonObject.optString("headline");
                String image = jsonObject.optString("image");
                String summary = jsonObject.optString("summary");
                String source = jsonObject.optString("source");
                String datetime = jsonObject.optString("datetime");
                String url = jsonObject.optString("url");

                // Only add the news item if none of the parameters are empty
                if (!headline.isEmpty() && !image.isEmpty() && !summary.isEmpty() &&
                        !source.isEmpty() && !datetime.isEmpty() && !url.isEmpty()) {
                    newsItems.add(new NewsItem(headline, image, summary, source, datetime, url));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
        return newsItems;
    }

    private void updateNews(String newsResponse) {
        List<NewsItem> newsItems = parseNewsApiResponse(newsResponse);
        // Assuming newsList and newsAdapter are accessible here
        if (newsItems != null && !newsItems.isEmpty()) {
            newsList.clear();
            newsList.addAll(newsItems);
            newsAdapter.notifyDataSetChanged();
        }
    }

    private void updateHistoricalChart(String historicalData) {
        // Find the ChartFragment corresponding to the historical data
        // It's important to fetch the correct fragment based on its position.
        ChartFragment historicalFragment = (ChartFragment) getSupportFragmentManager()
                .findFragmentByTag("f" + viewPager.getCurrentItem());
        if (historicalFragment != null) {
            historicalFragment.updateChartWithData(historicalData, "Historical", symbol);
        }
    }
    private void updateHourlyChart(String hourlyData) {
        // Find the ChartFragment corresponding to the hourly data
        ChartFragment hourlyFragment = (ChartFragment) getSupportFragmentManager()
                .findFragmentByTag("f" + viewPager.getCurrentItem());
        if (hourlyFragment != null) {
            hourlyFragment.updateChartWithData(hourlyData, "Hourly", symbol);
        }
    }
    private void displayEPSChart(String epsData) {
        EPSChartFragment epsFragment = EPSChartFragment.newInstance(epsData);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, epsFragment);  // Assuming you have a container in MainActivity's layout
        transaction.commit();
//        eps.setVisibility(View.INVISIBLE);
    }

    // Call this method with new EPS data as needed
    public void updateEPSData(String newEPSData) {
        displayEPSChart(newEPSData);
    }
    private void loadRecommendationChartFragment(String recommendationData) {
        RecommendationChartFragment recommendfragment = RecommendationChartFragment.newInstance(recommendationData);
        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.replace(R.id.chart_container, recommendfragment);  // Assuming you have a container in MainActivity's layout
        transaction1.commit();
    }


    @Override
    public void onDataFetched(Map<String, String> apiResponses) {
        // This is called when all data has been fetched. Now you can update UI.
        runOnUiThread(() -> {
            String profileResponse = search.getApiResponse("StockProfile");
            currentProfileResponse = profileResponse;
            String quoteResponse = search.getApiResponse("StockQuote");
            currentQuoteResponse= quoteResponse;
            String StockPeersResponse = search.getApiResponse("StockPeers");
            String SentimentsResponse = search.getApiResponse("InsiderSentiment");
            String newsResponse = search.getApiResponse("CompanyNews");
            String historicalResponse = search.getApiResponse("Historical");
            String hourlyResponse = search.getApiResponse("Hourly");
            String recommendationResponse = search.getApiResponse("Recommendation");
            String EPSResponse = search.getApiResponse("StockEarnings");
            // Use this data to update UI
            if (profileResponse != null && quoteResponse != null && StockPeersResponse != null && SentimentsResponse != null
                    && newsResponse != null && historicalResponse != null && hourlyResponse != null && recommendationResponse!=null
            && EPSResponse!=null) {
                updateStockDetails(profileResponse, quoteResponse);
                stat_update(quoteResponse);
                about_update( profileResponse, StockPeersResponse);
                InsiderSentimentData(profileResponse, SentimentsResponse);
                updateNews(newsResponse);
                updateHistoricalChart(historicalResponse);
                updateHourlyChart(hourlyResponse);
                loadRecommendationChartFragment(recommendationResponse);
                displayEPSChart(EPSResponse);

            }
            Log.d(TAG, "Profile JSON: " + profileResponse);
            Log.d(TAG, "Quote JSON: " + quoteResponse);
            Log.d(TAG, "StockPeers JSON: " + StockPeersResponse);
            Log.d(TAG, "Sentiments JSON: " + SentimentsResponse);
            Log.d(TAG, "News JSON: " + newsResponse);
            Log.d(TAG, "Historical JSON: " + historicalResponse);
            Log.d(TAG, "Hourly JSON: " + hourlyResponse);
            Log.d(TAG, "Recommendation JSON: " + recommendationResponse);
            Log.d(TAG, "StockEarnings JSON: " + EPSResponse);
        });
    }

    private void setupViewPager(ViewPager2 viewPager,String ticker) {
        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return ChartFragment.newInstance("Hourly",ticker);
                } else {
                    return ChartFragment.newInstance("Historical",ticker);
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        };
        viewPager.setAdapter(adapter);
    }

    private void showTradeDialog(String companyName, String symbol, double currentPrice, String userId) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.trade_dialog);
        dialog.setTitle("Trade " + companyName + " shares");

        EditText etNumberOfShares = dialog.findViewById(R.id.etNumberOfShares);
        TextView tvCalculation = dialog.findViewById(R.id.tvCalculation);
        TextView tvCashBalanceInfo = dialog.findViewById(R.id.tvCashBalanceInfo);
        Button btnBuy = dialog.findViewById(R.id.btnBuy);
        Button btnSell = dialog.findViewById(R.id.btnSell);
        TextView tvTradeHeader = dialog.findViewById(R.id.tvTradeHeader);
        tvTradeHeader.setText("Trade " + companyName + " shares");

        // Fetch wallet balance and current portfolio details
        final double[] cashBalance = new double[1];
        final int[] ownedShares = new int[1];

        apiService.getWallet(userId).enqueue(new Callback<Wallet>() {
            @Override
            public void onResponse(Call<Wallet> call, Response<Wallet> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cashBalance[0] = response.body().getBalance();
                    tvCashBalanceInfo.setText(String.format(Locale.getDefault(), "$%.2f available to buy %s", cashBalance[0], symbol));
                }
            }

            @Override
            public void onFailure(Call<Wallet> call, Throwable t) {
                tvCashBalanceInfo.setText("Failed to fetch balance");
            }
        });

        apiService.getPortfolio(userId).enqueue(new Callback<List<PortfolioItem>>() {
            @Override
            public void onResponse(Call<List<PortfolioItem>> call, Response<List<PortfolioItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (PortfolioItem item : response.body()) {
                        if (item.getSymbol().equalsIgnoreCase(symbol)) {
                            ownedShares[0] = item.getQuantity();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PortfolioItem>> call, Throwable t) {
                // Handle failure
            }
        });

        etNumberOfShares.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    try {
                        int numShares = Integer.parseInt(s.toString());
                        double totalCost = numShares * currentPrice;
                        tvCalculation.setText(String.format(Locale.getDefault(), "%d * $%.2f/share = $%.2f", numShares, currentPrice, totalCost));
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    tvCalculation.setText("");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        btnBuy.setOnClickListener(v -> {
            try {
                int numShares = Integer.parseInt(etNumberOfShares.getText().toString());
                double totalCost = numShares * currentPrice;
                if (numShares <= 0) {
                    Toast.makeText(MainActivity.this, "Cannot buy non-positive shares", Toast.LENGTH_SHORT).show();
                } else if (totalCost > cashBalance[0]) {
                    Toast.makeText(MainActivity.this, "Not enough money to buy", Toast.LENGTH_SHORT).show();
                } else {
                    performBuy(symbol, numShares, currentPrice, userId, dialog);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Please enter a valid number of shares", Toast.LENGTH_SHORT).show();
            }
        });

        btnSell.setOnClickListener(v -> {
            try {
                int numShares = Integer.parseInt(etNumberOfShares.getText().toString());
                if (numShares <= 0) {
                    Toast.makeText(MainActivity.this, "Cannot sell non-positive shares", Toast.LENGTH_SHORT).show();
                } else if (numShares > ownedShares[0]) {
                    Toast.makeText(MainActivity.this, "Not enough shares to sell", Toast.LENGTH_SHORT).show();
                } else {
                    performSell(symbol, numShares, currentPrice, userId, dialog);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Please enter a valid number of shares", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void performBuy(String symbol, int numShares, double price, String userId, Dialog dialog) {
        PortfolioItem transaction = new PortfolioItem(userId, symbol, numShares, price,0);  // Correct constructor usage
        apiService.buyStock(transaction).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showSuccessDialog(true, numShares);
                    dialog.dismiss();
                    fetchPortfolioForSymbol(symbol);
                } else {
                    try {
                        String errorMessage = response.errorBody().string();
                        Toast.makeText(MainActivity.this, "Transaction failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "Error reading error message", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSell(String symbol, int numShares, double price, String userId, Dialog dialog) {
        PortfolioItem transaction = new PortfolioItem("rushabh75", symbol, numShares, 0, price);
        apiService.sellStock(transaction).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    showSuccessDialog(false, numShares);
                    dialog.dismiss();
                    fetchPortfolioForSymbol(symbol);
                } else {
                    Log.d("API Call", "Sending buy request: " + new Gson().toJson(transaction));
                    Toast.makeText(MainActivity.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showSuccessDialog(boolean isBuy, int numShares) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_success); // Set the custom layout
        dialog.setTitle("Transaction Success");

        TextView tvSuccessDetail = dialog.findViewById(R.id.tvSuccessDetail);
        tvSuccessDetail.setText("You have successfully " + (isBuy ? "bought " : "sold ") + numShares + " shares of" + symbol); // Customize for your needs

        Button btnDone = dialog.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(v -> dialog.dismiss()); // Dismiss the dialog when DONE is clicked

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    // Method to update UI with fetched stock data

}
