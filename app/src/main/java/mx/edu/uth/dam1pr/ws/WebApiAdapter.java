package mx.edu.uth.dam1pr.ws;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebApiAdapter {

    private static WebApiService API_SERVICE;
    private static final String BASE_URL = "https://grupofersaoficial.com/ws_app/ws/";

    public static WebApiService getApiService() {
        // Interceptor para mostrar el cuerpo de las peticiones y respuestas
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Cliente HTTP con el interceptor
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        // Inicializa Retrofit solo una vez
        if (API_SERVICE == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            API_SERVICE = retrofit.create(WebApiService.class);
        }

        return API_SERVICE;
    }
}

