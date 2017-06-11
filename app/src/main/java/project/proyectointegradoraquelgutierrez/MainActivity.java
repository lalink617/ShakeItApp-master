package project.proyectointegradoraquelgutierrez;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import project.proyectointegradoraquelgutierrez.MainFragment.FragmentLogin;
import project.proyectointegradoraquelgutierrez.MainFragment.FragmentSignup;

/**
 * Clase principal en la que puedes acceder como invitado, acceder con usuario y contraseña o registrarte.
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EditText user, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentLogin(), getResources().getString(R.string.login).toUpperCase());
        adapter.addFragment(new FragmentSignup(), getResources().getString(R.string.singup).toUpperCase());
        viewPager.setAdapter(adapter);

    }

    /**
     * Acción al pulsar el Botón de sesión de invitado.
     * Inicia sesión como invitado.
     *
     * @param view la vista
     */
    public void btGuestSessionOnClick(View view) {
        //if (checkConnection()) {
            Credentials.invitado = true;
            startActivity(new Intent(MainActivity.this, ShakeActivity.class));
        //}

        //else Toast.makeText(MainActivity.this, getResources().getString(R.string.no_conex), Toast.LENGTH_LONG).show();
    }

    /**
     * Método que comprueba si hay conexión a internet.
     *
     * @return true si hay conexión o false si no la hay.
     */
    public boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Acción al pulsar el Botón de Login (Iniciar sesión).
     * Inicia sesión con usuario y contraseña.
     *
     * @param view la vista
     */
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
                            Credentials.user = user.getText().toString().trim();
                            Credentials.password = password.getText().toString().trim();
                            Credentials.invitado = false;
                            Intent i = new Intent(MainActivity.this, ScoreActivity.class);
                            i.putExtra("menu", true);
                            startActivity(i);
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

    /**
     * Acción al pulsar el Botón de SignUp (Registrar usuario).
     * Registra un usuario con usuario y contraseña.
     *
     * @param view la vista
     */
    public void btSingUpOnClick(View view) {
        user = (EditText) findViewById(R.id.etRegUsername);
        password = (EditText) findViewById(R.id.etRegPassword);

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
                            Credentials.user = user.getText().toString().trim();
                            Credentials.password = password.getText().toString().trim();
                            Credentials.invitado = false;
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.register), Toast.LENGTH_LONG).show();
                            Intent i = new Intent(MainActivity.this, ScoreActivity.class);
                            i.putExtra("menu", true);
                            startActivity(i);
                        } else
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.username_take), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.execute(
                Uri.parse("http://www.iesmurgi.org:85/raquel2017/register.php")
                        .buildUpon()
                        .appendQueryParameter("user", user.getText().toString().trim())
                        .appendQueryParameter("pass", password.getText().toString().trim())
                        .build()
                        .toString()
        );
    }

    /**
     * Acción al pulsar el logotipo.
     * Muestra la información de la aplicación.
     *
     * @param view la vista
     */
    public void logoOnClick(View view) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.about);
        ImageButton ib = (ImageButton) dialog.findViewById(R.id.imageExit);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    /**
     * Clase que inserta los fragmentos Login y Sign Up creando un View Pager.
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        /**
         * Instancia un ViewPageradapter.
         *
         * @param manager el manager
         */
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

        /**
         * Añade un fragmento
         *
         * @param fragment el fragmento
         * @param title    el títutlo
         */
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
