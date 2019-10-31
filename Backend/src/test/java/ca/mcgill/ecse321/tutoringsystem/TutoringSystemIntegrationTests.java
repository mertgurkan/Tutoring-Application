package ca.mcgill.ecse321.tutoringsystem;

import org.junit.Test;

import ca.mcgill.ecse321.tutoringsystem.model.TimeSlot;
import ca.mcgill.ecse321.tutoringsystem.model.Wage;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class TutoringSystemIntegrationTests {

	// Integration tests go here: Testing of REST

	private final String APP_URL = "http://tutoringsystem-backend-14.herokuapp.com";
	private JSONObject response;
	private final String restName = "TestUser";
	private final String restEmail = "ecse321test@protonmail.com";
	private final String restEmailManager = "ecse321test+manager@protonmail.com";
	private final String restPassword = "userpassword";

	@Test
	public void contextLoads() {
		System.out.println("ran Integration file");
	}

	@After
	public void clearDatabase() {

	}
	
	/*
	 * TESTING
	 */

	@Test
	public void testCreateTutor() {
		try {
			response = send("POST", APP_URL, "/tutors/create",
					"name=" + restName + "&email=" + restEmail + "&password=" + restPassword);
			System.out.println("Received: " + response.toString());
			assertEquals(restName, response.getString("name"));
		} catch (JSONException e) {
			fail();
		}
	}

	@Test
	public void testUpdateTutorPassword() {
		try {
			Set<TimeSlot> timeslots = null;
			Set<Wage> wages =null;
			String newPassword = "userpassword321";
			response = send("POST", APP_URL, "/tutors/create",
					"name=" + restName + "&email=" + restEmail + "&password=" + restPassword);
			System.out.println(response.getString("userId"));
			response = send("PUT", APP_URL, "/tutors/update/" + response.getString("userId"),
					"name=" + restName + "&email=" + restEmail + "&password=" + newPassword + "&timeslots=" + timeslots + "&wage=" + wages);
			System.out.println("Received: " + response.toString());
			assertEquals(newPassword, response.getString("password"));
		} catch (JSONException e) {
			fail();
		}
	}
	
	/*
	 * APPLICATION
	 */
	
	@Test
	public void testCreateApplication() {
		try {
			response = send("POST", APP_URL, "/applications/create",
					"existing=false" + "&name=" + restName + "&email=" + restEmail + "&courses=ECSE321");
			System.out.println("Received: " + response.toString());
			assertEquals(restName, response.getString("name"));
		} catch (JSONException e) {
			fail();
		}
	}
	
	/*
	 * COURSE
	 */
	
	@Test
	public void testCreateCourse() {
		try {
			response = send("POST", APP_URL, "/courses/create",
					"name=MATH262" + "&institution=McGillU" + "&subject=Mathematics");
			System.out.println("Received: " + response.toString());
			assertEquals("MATH262", response.getString("name"));
		} catch (JSONException e) {
			fail();
		}
	}
	
	/*
	 * INSTITUTION
	 */
	
	@Test
	public void testCreateInstitution() {
		try {
			response = send("POST", APP_URL, "/institutions/create",
					"name=McGill"+ "&level=University");
			System.out.println("Received: " + response.toString());
			assertEquals("McGill", response.getString("institutionName"));
		} catch (JSONException e) {
			fail();
		}
	}
	
	/*
	 * MANAGER
	 */
	@Test
	public void testCreateManager() {
		try {
			response = send("POST", APP_URL, "/managers/create",
					"name=" + restName + "&email=" + restEmailManager + "&password=" + restPassword);
			System.out.println("Received: " + response.toString());
			assertEquals(restName, response.getString("name"));
		} catch (JSONException e) {
			fail();
		}
	}
	
	@Test
	public void testCreateRoom() {
		try {
			response = send("POST", APP_URL, "/rooms/create", "id=21" + "&capacity=23");
			System.out.println("Received: " + response.toString());
			assertEquals(21, response.getString("capacity"));
		} catch (JSONException e) {
			fail();
		}
	}

	public JSONObject send(String type, String appURL, String path, String parameters) {
		try {
			URL URL = new URL(appURL + path + ((parameters == null) ? "" : ("?" + parameters)));
			System.out.println("Sending: " + URL.toString());
			HttpURLConnection c = (HttpURLConnection) URL.openConnection();
			c.setRequestMethod(type);
			c.setRequestProperty("Accept", "application/json");
			System.out.println(c.getContentType());
			if (c.getResponseCode() != 200) {
				throw new RuntimeException(URL.toString() + " Returned error: " + c.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((c.getInputStream())));
			String response = br.readLine();
			if (response != null) {
				JSONObject json = new JSONObject(response);
				c.disconnect();
				return json;
			}
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
