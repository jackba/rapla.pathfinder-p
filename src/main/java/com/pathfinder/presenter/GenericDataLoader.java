package com.pathfinder.presenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.pathfinder.model.Attribut;
import com.pathfinder.model.FreeRoomModel;
import com.pathfinder.util.properties.ApplicationProperties;
import com.pathfinder.util.properties.PropertiesKey;
import com.vaadin.data.util.BeanItemContainer;

/**
 * 
 * GenericDataLoader will get the Data to the runtime
 * 
 * @author Myracle
 * @version 0.1
 * 
 */

public class GenericDataLoader implements GenericDataLoaderSpec {

	private static final Logger LOGGER = LogManager
			.getLogger(GenericDataLoader.class);

	private final String BASE_URL = ApplicationProperties.getInstance()
			.getProperty(PropertiesKey.RAPLA_BASE_URL);

	private final String FREE_RESOURCES = BASE_URL + "/getFreeResources";
	private final String MASSAGE_ERROR_URL_NOT_READABLE = "Error loading URL: ";

	private final JSONParser parser = new JSONParser();
	private JSONObject jsonObject = null;

	private BufferedReader br;

	@Override
	public BeanItemContainer<FreeRoomModel> getFreeResources() {

		FreeRoomModel freeRoom = null;

		BeanItemContainer<FreeRoomModel> freeRoomContainer = new BeanItemContainer<FreeRoomModel>(
				FreeRoomModel.class);

		try {
			br = new BufferedReader(new InputStreamReader(new URL(
					FREE_RESOURCES).openStream()));

			jsonObject = (JSONObject) parser.parse(br);

			@SuppressWarnings("unchecked")
			List<JSONObject> freeResourcesResult = (List<JSONObject>) jsonObject
					.get("result");

			if (!freeResourcesResult.isEmpty()) {
				for (JSONObject result : freeResourcesResult) {
					for (int i = 0; i == 5; i++) {

						List<JSONObject> freeRoomResources = this
								.getFreeResourcesResources(result);

						freeRoom = new FreeRoomModel((String) freeRoomResources
								.get(0).get("id"), (String) freeRoomResources
								.get(0).get("name"), (String) freeRoomResources
								.get(0).get("link"),
								(String) result.get("start"),
								(String) result.get("end"));
						freeRoomContainer.addItem(freeRoom);

					}
				}

				return freeRoomContainer;

			}
			return null;
		} catch (MalformedURLException e) {
			LOGGER.error(MASSAGE_ERROR_URL_NOT_READABLE, e);
			return null;
		} catch (IOException e) {
			LOGGER.error(MASSAGE_ERROR_URL_NOT_READABLE, e);
			return null;
		} catch (ParseException e) {
			LOGGER.error(MASSAGE_ERROR_URL_NOT_READABLE, e);
			return null;
		}

	}

	@Override
	public List<JSONObject> getFreeResourcesResources(JSONObject jsonObject) {

		this.jsonObject = jsonObject;

		@SuppressWarnings("unchecked")
		List<JSONObject> resources = (List<JSONObject>) this.jsonObject
				.get("resources");

		return resources;
	}

	@Override
	public List<Attribut> getModelDetails(String modelLink) {
		String attributLabel;
		String attributValue;
		JSONObject attributMap;
		Attribut attribut;

		List<Attribut> attributList = new ArrayList<Attribut>();

		try {
			br = new BufferedReader(new InputStreamReader(new URL(BASE_URL
					+ "/" + modelLink).openStream()));

			jsonObject = (JSONObject) parser.parse(br);

			attributMap = (JSONObject) ((JSONObject) jsonObject.get("result"))
					.get("attributeMap");

			@SuppressWarnings("unchecked")
			Set<String> attributeMapSet = attributMap.keySet();

			Iterator<String> attributeMapKeys = attributeMapSet.iterator();

			while (attributeMapKeys.hasNext()) {
				attribut = new Attribut();

				String nextKey = attributeMapKeys.next().toString();

				attributLabel = (String) ((JSONObject) attributMap.get(nextKey))
						.get("label");
				attributValue = (String) ((JSONObject) attributMap.get(nextKey))
						.get("value");

				attribut.setLabel(attributLabel);
				attribut.setValue(attributValue);

				attributList.add(attribut);

			}

			return attributList;

		} catch (MalformedURLException e) {
			LOGGER.error(MASSAGE_ERROR_URL_NOT_READABLE, e);
			return null;
		} catch (IOException e) {
			LOGGER.error(MASSAGE_ERROR_URL_NOT_READABLE, e);
			return null;
		} catch (ParseException e) {
			LOGGER.error(MASSAGE_ERROR_URL_NOT_READABLE, e);
			return null;
		}

	}
}