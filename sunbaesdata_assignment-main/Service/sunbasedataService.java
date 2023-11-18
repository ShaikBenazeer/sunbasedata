package com.sunbasedata.service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sunbasedata.entity.sunbasedataEntity;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpHeaders;
import okio.ByteString;
@Service
public class sunbasedataService {

	private OkHttpClient client;
	private Response response;
	private String token;
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
	public String authenticate(String login_id,String password)
	{
		client=new OkHttpClient();
        ByteString requestBodyContent = ByteString.encodeUtf8(
                "{\"login_id\":\"" + login_id + "\",\"password\":\"" + password + "\"}"
        );
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, requestBodyContent);
		Request request=new Request.Builder()
				.url("https://qa2.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp")
				.post(requestBody)
				.build();
		try
		{
			response=client.newCall(request).execute();
	        if (response.isSuccessful()) {
	        	token=response.body().string();
	        	setToken(token);
	        	return getToken();
	        } else {
	            System.out.println("Error: " + response.code() + " - " + response.message());
	        }
		}
		catch(IOException e)
		{
			System.out.print("in catch");
			e.printStackTrace();
		}
		return null;
	}
	
	public List<sunbasedataEntity> getAllList()
	{
        if (getToken() == null) {
            return null;
        }
        String apiUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list";
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + extractTokenFromJson(getToken()))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                sunbasedataEntity[] sunbasedataEntitiesArray = objectMapper.readValue(response.body(), sunbasedataEntity[].class);
                List<sunbasedataEntity> sunbasedataEntities = Arrays.asList(sunbasedataEntitiesArray);
                return sunbasedataEntities;
            }
            else {
                System.err.println("Error: " + response.statusCode() + " - " + response.body());
                return null;
            }   	
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	private String extractTokenFromJson(String jsonString) {
	    try {
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode jsonNode = mapper.readTree(jsonString);
	        return jsonNode.get("access_token").asText();
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	public boolean deleteRecord(String uuid) {
	    String API = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp";
        String url = API + "?cmd=delete&uuid=" + uuid;
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + extractTokenFromJson(getToken()))
                    .POST(HttpRequest.BodyPublishers.noBody()) 
                    .build();
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return true;
            } else if (response.statusCode() == 500) {
                return false;
            } else if (response.statusCode() == 400) {
                return false;
            }
            return false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
	}
	public boolean updateRecord(String first_name,String last_name,String street,String address,String city,String state,String email,String phone,String uuid)
	{
        String API = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp";
        String url = API + "?cmd=update&uuid=" + uuid;
        String requestBodyContent = "{" +
                "\"first_name\":\"" + first_name + "\"," +
                "\"last_name\":\"" + last_name + "\"," +
                "\"street\":\"" + street + "\"," +
                "\"address\":\"" + address + "\"," +
                "\"city\":\"" + city + "\"," +
                "\"state\":\"" + state + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"phone\":\"" + phone + "\"," +
                "\"uuid\":\"" + uuid + "\"" +
                "}";
        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Authorization", "Bearer " + extractTokenFromJson(getToken()))
                    .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(requestBodyContent))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return true;
            } else if (response.statusCode() == 500) {
                return false;
            } else if (response.statusCode() == 400) {
                return false;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}
	public boolean addRecord(String first_name,String last_name,String street,String address,String city,String state,String email,String phone)
	{
        String API = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp";
        String url = API + "?cmd=create";
        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("first_name", first_name);
        requestBodyMap.put("last_name", last_name);
        requestBodyMap.put("street", street);
        requestBodyMap.put("address", address);
        requestBodyMap.put("city", city);
        requestBodyMap.put("state", state);
        requestBodyMap.put("email", email);
        requestBodyMap.put("phone", phone);
        String requestBodyContent = new Gson().toJson(requestBodyMap);
        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Authorization", "Bearer " + extractTokenFromJson(getToken()))
                    .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(requestBodyContent))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                return true;
            } else if (response.statusCode() == 400) {
                return false;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}
}
