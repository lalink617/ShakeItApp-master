package project.proyectointegradoraquelgutierrez.MainFragment;

/**
 * Created by Raquel on 27/04/2017.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import project.proyectointegradoraquelgutierrez.R;

/**
 * Clase que hereda de Fragment, se usará en MainActivity y mostrará un Login(Para acceder).
 */
public class FragmentLogin extends android.support.v4.app.Fragment {

    /**
     * Instancia un nuevo FragmentLogin
     */
    public FragmentLogin() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflo el layout del fragmento
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

}
