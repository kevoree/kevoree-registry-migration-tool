package org.kevoree.registry.migration.tool;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.kevoree.DeployUnit;
import org.kevoree.TypeDefinition;
import org.kevoree.factory.DefaultKevoreeFactory;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class SaveTypeDef extends ProcessModel {

	public SaveTypeDef(String accessToken) {
		super(accessToken);
	}

	public void createPackage(final List<String> npackages) throws UnirestException {
		Unirest.post("http://localhost:8080/api/namespaces").header("Content-Type", "application/json;charset=UTF-8")
				.header("Accept", "application/json").header("Authorization", "Bearer " + accessToken)
				.body(new JSONObject().put("name", StringUtils.join(npackages, '.'))).asJson();
	}

	public void createTypeDefinition(final String namespace, final TypeDefinition typeDefinition)
			throws JSONException, UnirestException {
		try {
			final DefaultKevoreeFactory defaultKevoreeFactory = new DefaultKevoreeFactory();

			try {
				final JSONObject tdJson = new JSONObject().put("name", typeDefinition.getName()).put("version",
						typeDefinition.getVersion());

				typeDefinition.setDeployUnits((List<? extends DeployUnit>) new ArrayList<>());
				final String serialize = defaultKevoreeFactory.createJSONSerializer().serialize(typeDefinition);
				Unirest.post("http://localhost:8080/api/namespaces/{namespace}/tdefs")
						.routeParam("namespace", namespace).header("Content-Type", "application/json;charset=UTF-8")
						.header("Accept", "application/json").header("Authorization", "Bearer " + accessToken)
						.body(tdJson.put("model", serialize)).asJson();
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			if (!e.getMessage().startsWith("Unresolved")) {
				throw e;
			} else {
				System.out.println("Error on typedef : " + e.getMessage());
			}
		}
	}

	public void submitDu(final String namespace, final DeployUnit deployUnit, final String tdefName,
			final String tdefVersion) throws UnirestException {

	}
}
