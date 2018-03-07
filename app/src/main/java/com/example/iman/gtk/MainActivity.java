package com.example.iman.gtk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.iman.gtk.adapters.HomePagerAdapter;
import com.example.iman.gtk.fragments.ChatFragment;
import com.example.iman.gtk.fragments.HomeFragment;
import com.example.iman.gtk.util.AdMob;
import com.example.iman.gtk.util.NetworkHandler;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MAIN_ACTIVITY";
    private static final int REQUEST_CODE = 1000;
    private TabLayout tabLayout;
    TabLayout.Tab tabItem;
    private ViewPager viewPager;

    private AdView mAdView;
    private AdMob admob;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (!NetworkHandler.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(MainActivity.this,
                    getString(R.string.no_internet),
                    Toast.LENGTH_SHORT).show();
        }

        initViews();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    getSupportActionBar().setSubtitle("Hi, " + user.getDisplayName() + "!");
                }
            }
        };

        if (savedInstanceState != null) {
            initAdMob();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initAdMob();
                }
            }, 5000);
        }

    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.activity_home_pager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.activity_home_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabItem = tabLayout.getTabAt(0);
        tabItem.setIcon(R.drawable.tab_home_selector);
        tabItem.select();
        tabItem = tabLayout.getTabAt(1);
        tabItem.setIcon(R.drawable.tab_chat_selector);
    }

    private void setupViewPager(ViewPager viewPager) {
        HomePagerAdapter adapter = new HomePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Login");
        adapter.addFragment(new ChatFragment(), "Chat");
        viewPager.setAdapter(adapter);
    }

    private void initAdMob() {
        mAdView = (AdView) findViewById(R.id.adView);
        admob = new AdMob(this, mAdView);
        admob.requestAdMob();

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //Got something from server. Read that.
                new WebviewResultReader(MainActivity.this).execute(data);
            }
        }
    }

    public class WebviewResultReader extends AsyncTask<Intent, Void, Void> {
        private Context mContext;
        private ProgressDialog mProgressDialog;

        public WebviewResultReader(Context mContext) {
            this.mContext = mContext;
            this.mProgressDialog = new ProgressDialog(this.mContext);
            this.mProgressDialog.setTitle("Wait");
            this.mProgressDialog.setMessage("Reading result...");
        }

        @Override
        protected void onPreExecute() {
            this.mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (this.mProgressDialog != null)
                this.mProgressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Intent... intents) {
            Uri uri = intents[0].getData();
            //TODO write your logic here to read data came from webview.
            Log.i("MY_TAG", "Got response from server : \n" + uri.toString());
            return null;
        }
    }

    private void removeAds() {
        mAdView.setVisibility(View.GONE);
        if (admob != null) {
            admob.stopRepeatingTask();
        }
    }
}