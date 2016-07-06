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

	private final String serverPath ;
	private String accessToken;

	public SaveTypeDef(final String accessToken, final String serverPath) {
		this.accessToken = accessToken;
		this.serverPath = serverPath;
	}

	public void createPackage(final List<String> npackages) throws UnirestException {
		Unirest.post(serverPath + "/api/namespaces").header("Content-Type", "application/json;charset=UTF-8")
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

				final List<? extends DeployUnit> arrayList = new ArrayList<>();
				typeDefinition.setDeployUnits(arrayList);
				final String serialize = defaultKevoreeFactory.createJSONSerializer().serialize(typeDefinition);
				Unirest.post(serverPath + "/api/namespaces/{namespace}/tdefs")
						.routeParam("namespace", namespace).header("Content-Type", "application/json;charset=UTF-8")
						.header("Accept", "application/json").header("Authorization", "Bearer " + accessToken)
						.body(tdJson.put("model", serialize)).asJson();
			} catch (final ClassCastException e) {
				e.printStackTrace();
			}
		} catch (final Exception e) {
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
