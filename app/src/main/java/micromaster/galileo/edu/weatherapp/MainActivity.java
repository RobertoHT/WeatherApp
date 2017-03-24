package micromaster.galileo.edu.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

import micromaster.galileo.edu.weatherapp.API.WeatherInterface;
import micromaster.galileo.edu.weatherapp.model.WeatherData;
import micromaster.galileo.edu.weatherapp.model.WeatherResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TextView countryName, weather, temperature, pressure, humidity;

    private final static String BASE_URL = "http://api.wunderground.com/api/";
    private final static String API_KEY = "3376acf27920013e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getViews();

        new DownloadWeatherTask(MainActivity.this).execute();
    }

    private void getViews(){
        countryName = (TextView) findViewById(R.id.countryName);
        weather = (TextView) findViewById(R.id.weather);
        temperature = (TextView) findViewById(R.id.temperature);
        pressure = (TextView) findViewById(R.id.pressure);
        humidity = (TextView) findViewById(R.id.humidity);
    }

    private void setData(WeatherData weatherData){
        countryName.setText(weatherData.getDisplayLocation().getCityName());
        weather.setText(weatherData.getWeather());
        temperature.setText(weatherData.getTemp());
        pressure.setText(String.valueOf(weatherData.getPressure()));
        humidity.setText(weatherData.getHumidity());
    }

    private class DownloadWeatherTask extends AsyncTask<Void, Integer, WeatherData>{
        private ProgressDialog dialog;

        public DownloadWeatherTask(Context context) {
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(getString(R.string.download_data));
        }

        @Override
        protected WeatherData doInBackground(Void... voids) {
            WeatherData weatherData = null;

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            WeatherInterface weatherInterface = retrofit.create(WeatherInterface.class);
            Call<WeatherResponse> call = weatherInterface.getWeatherFromSanFrancisco(API_KEY);
            WeatherResponse weatherResponse = null;
            try {
                weatherResponse = call.execute().body();
                weatherData = weatherResponse.getWeatherData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return weatherData;
        }

        @Override
        protected void onPostExecute(WeatherData weatherData) {
            super.onPostExecute(weatherData);

            if(weatherData != null){
                setData(weatherData);
            }

            dialog.dismiss();
        }
    }
}
