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
 * Clase que hereda Fragment, se usará en MainActivity y mostrará un Sign Up(Para registrarse).
 */
public class FragmentSignup extends android.support.v4.app.Fragment {

    /**
     * Instancia un nuevo FragmentSignUp.
     */
    public FragmentSignup() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout del fragmento
        return inflater.inflate(R.layout.fragment_singup, container, false);
    }

}
