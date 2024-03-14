package io.github.udayhe;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;

import static io.github.udayhe.constant.Constant.*;
import static java.lang.System.out;
import static java.util.Objects.nonNull;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/**
 * SSELoadTest is to load test the @Get("/register/{channel}")
 * and @Post("/emit-event/{channel}") endpoints present in EventStreamController
 * TOTAL_REQUESTS - to set number of API calls/requests
 * Throttling TOTAL_REQUESTS will help to know the load which can be handled by the server.
 * For ex. if TOTAL_REQUESTS = 4000, these 4k requests will be divided for calling
 * registerChannel(int channel) and emitEvent(int channel) methods
 */
public class SSELoadTest {


    public static void main(String[] args) {
        run();
    }

    public static void run() {
        ExecutorService executor = null;
        try {
            executor = newFixedThreadPool(TOTAL_REQUESTS);
            for (int i = 1; i <= TOTAL_REQUESTS; i++) {
                final int channel = i;
                executor.submit(() -> registerChannel(channel));
                executor.submit(() -> emitEvent(channel));
            }
            executor.shutdown();
        } catch (Exception e) {
            out.println("Exception in run." + e);
        } finally {
            if (nonNull(executor)) executor.shutdownNow();
        }
    }

    private static void registerChannel(int channel) {
        String url = LOCAL_URL + SSE_REGISTER_CHANNEL_URL + channel;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);
            request.addHeader("Content-Type", "application/json");
            try (CloseableHttpResponse response = httpClient.execute(request); BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    out.println(line);
                }
            }
        } catch (Exception e) {
            out.println("Exception in registerChannel" + e);
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    out.println("Exception in registerChannel, final" + e);
                }
            }
        }
    }

    private static void emitEvent(int channel) {
        String url = LOCAL_URL + SSE_EMIT_EVENT_URL + channel;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost request = new HttpPost(url);
            request.addHeader("Content-Type", "application/json");
            String json = "{\"message\":\"hi\"}";
            request.setEntity(new StringEntity(json, APPLICATION_JSON));
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                out.println("Emit event response status: " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            out.println("Exception in emitEvent");
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    out.println("Exception in emitEvent, final" + e);
                }
            }
        }
    }
}
