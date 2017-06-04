package project.proyectointegradoraquelgutierrez;

import android.content.Intent;
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


import java.util.ArrayList;
import java.util.List;

import project.proyectointegradoraquelgutierrez.MainFragment.FragmentLogin;
import project.proyectointegradoraquelgutierrez.MainFragment.FragmentSingup;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EditText user, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        user = (EditText) findViewById(R.id.etLoginUsername);
        password = (EditText) findViewById(R.id.etLoginPassword);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentLogin(), getResources().getString(R.string.login).toUpperCase());
        adapter.addFragment(new FragmentSingup(), getResources().getString(R.string.singup).toUpperCase());
        viewPager.setAdapter(adapter);
    }

    public void btGuestSessionOnClick(View view) {
        new CallAPI() {
            @Override
            protected void onPostExecute(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result.equals("ok")) {
                            startActivity(new Intent(MainActivity.this, ShakeActivity.class));
                            Credentials.user = user.getText().toString();
                            Credentials.password = password.getText().toString();
                        } else
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.bad_login), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.execute(
                Uri.parse("http://192.168.3.44/check_login.php")
                .buildUpon()
                .appendQueryParameter("user", user.getText().toString())
                .appendQueryParameter("pass", password.getText().toString())
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
