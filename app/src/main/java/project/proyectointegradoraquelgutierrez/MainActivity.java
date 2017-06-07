package project.proyectointegradoraquelgutierrez;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import project.proyectointegradoraquelgutierrez.MainFragment.FragmentLogin;
import project.proyectointegradoraquelgutierrez.MainFragment.FragmentSingup;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EditText user, password;
    private URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();

        try {
            url = new URL("http://www.iesmurgi.org:85/raquel/check_login.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentLogin(), getResources().getString(R.string.login).toUpperCase());
        adapter.addFragment(new FragmentSingup(), getResources().getString(R.string.singup).toUpperCase());
        viewPager.setAdapter(adapter);

    }

    public void btGuestSessionOnClick(View view) {
        if (checkConnection())
            startActivity(new Intent(MainActivity.this, ShakeActivity.class));
        else Toast.makeText(MainActivity.this, getResources().getString(R.string.no_conex), Toast.LENGTH_LONG).show();
    }

    public boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void btLoginOnClick(View view) {
        user = (EditText) findViewById(R.id.etLoginUsername);
        password = (EditText) findViewById(R.id.etLoginPassword);

        if (user.getText() == null || user.getText().toString().equals("") || password.getText() == null || password.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.empty_login), Toast.LENGTH_LONG).show();
            return;
        }
        if (!checkConnection()) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_conex), Toast.LENGTH_LONG).show();
            return;
        }

        new CallAPI() {
            @Override
            protected void onPostExecute(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result.equals("ok")) {
                            startActivity(new Intent(MainActivity.this, ShakeActivity.class));
                            Credentials.user = user.getText().toString().trim();
                            Credentials.password = password.getText().toString().trim();
                        } else
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.bad_login), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.execute(
                Uri.parse("http://www.iesmurgi.org:85/raquel2017/check_login.php")
                        .buildUpon()
                        .appendQueryParameter("user", user.getText().toString().trim())
                        .appendQueryParameter("pass", password.getText().toString().trim())
                        .build()
                        .toString()
        );
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
