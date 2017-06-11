package project.proyectointegradoraquelgutierrez;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Clase para hacer peticiones HTTP en segundo plano
 */
public abstract class CallAPI extends AsyncTask<String, String, String> {

    /**
     * Instancia un nuevo CallAPI
     */
    public CallAPI() {}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0];
        String resultToDisplay = "";
        InputStream in;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }

        try {
            resultToDisplay = IOUtils.toString(in, "UTF-8");
            //para [convertir][1] byte stream a string
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultToDisplay;
    }


    @Override
    protected abstract void onPostExecute(String result);
}