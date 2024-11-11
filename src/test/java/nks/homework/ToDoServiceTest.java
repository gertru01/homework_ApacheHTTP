package nks.homework;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ToDoServiceTest {

    private static final String URL = "https://todo-app-sky.herokuapp.com/";
    private HttpClient client;

    @BeforeEach
    public void setUp() {
        client = HttpClientBuilder.create().build();
    }

    @Test
    public void getToDoList() throws IOException {

        //Отправить get запрос
        HttpGet getRequest = new HttpGet(URL);
        HttpResponse response = client.execute(getRequest);

        //Проверить статус-код и заголовок Content-Type
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("application/json; charset=utf-8", response.getHeaders("Content-Type")[0].getValue());
    }

    @Test
    public void createToDoTask() throws IOException {

        //Отправить запрос на создание записи
        HttpResponse response = createTask();
        String respBody = EntityUtils.toString(response.getEntity());
        String idTast = respBody.substring(6, 10);

        //Проверить ответ
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("application/json; charset=utf-8", response.getHeaders("Content-Type")[0].getValue());
        assertEquals("{\"id\":" + idTast + ",\"title\":\"tests\",\"completed\":null}", respBody);

    }

    @Test
    public void renameToDoTask() throws IOException {

        //Отправить запрос на создание записи
        HttpResponse response = createTask();
        String respBody = EntityUtils.toString(response.getEntity());
        String idTast = respBody.substring(6, 10);

        //Отправить запрос на редактирование записи
        HttpPatch patchRequest = new HttpPatch(URL + idTast);
        patchRequest.setEntity(new StringEntity("{\"title\": \"tests123\"}", ContentType.APPLICATION_JSON));
        response = client.execute(patchRequest);
        respBody = EntityUtils.toString(response.getEntity());

        //Проверить ответ
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("application/json; charset=utf-8", response.getHeaders("Content-Type")[0].getValue());
        assertEquals("{\"id\":" + idTast + ",\"title\":\"tests123\",\"completed\":null}", respBody);
    }

    @Test
    public void completeToDoTask() throws IOException {

        //Отправить запрос на создание записи
        HttpResponse response = createTask();
        String respBody = EntityUtils.toString(response.getEntity());
        String idTast = respBody.substring(6, 10);

        //Отправить запрос на редактирование записи (выполнение задачи)
        HttpPatch patchRequest = new HttpPatch(URL + idTast);
        patchRequest.setEntity(new StringEntity("{\"completed\":true}", ContentType.APPLICATION_JSON));
        response = client.execute(patchRequest);
        respBody = EntityUtils.toString(response.getEntity());

        //Проверить ответ
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("application/json; charset=utf-8", response.getHeaders("Content-Type")[0].getValue());
        assertEquals("{\"id\":" + idTast + ",\"title\":\"tests\",\"completed\":true}", respBody);

    }

    @Test
    public void deleteToDoTask() throws IOException {

        //Отправить запрос на создание записи
        HttpResponse response = createTask();
        String respBody = EntityUtils.toString(response.getEntity());
        String idTast = respBody.substring(6, 10);

        //Отправить запрос на удаление записи
        HttpDelete deleteRequest = new HttpDelete(URL + idTast);
        response = client.execute(deleteRequest);
        respBody = EntityUtils.toString(response.getEntity());

        //Проверить ответ
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("application/json; charset=utf-8", response.getHeaders("Content-Type")[0].getValue());
        assertEquals("\"todo was deleted\"", respBody);
    }

    //Метод для создания записи
    private HttpResponse createTask() throws IOException {

        HttpPost postRequest = new HttpPost(URL);
        postRequest.setEntity(new StringEntity("""
                {"title": "tests"}
                """, ContentType.APPLICATION_JSON));

        return client.execute(postRequest);
    }
}
