package org.kevoree.registry.migration.tool;

import java.util.List;
import java.util.Objects;

import org.json.JSONException;
import org.json.JSONObject;
import org.kevoree.DeployUnit;
import org.kevoree.TypeDefinition;
import org.kevoree.Value;
import org.kevoree.factory.DefaultKevoreeFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class SaveDeployUnits extends ProcessModel {

	public SaveDeployUnits(String accessToken) {
		super(accessToken);
	}

	@Override
	public void submitDu(String namespace, DeployUnit deployUnit, String tdefName, String tdefVersion)
			throws JSONException, UnirestException {
		final String platform = deployUnit.getFilters().stream().filter(t -> Objects.equals(t.getName(), "platform"))
				.map(Value::getValue).findFirst().orElse("");
		final HttpResponse<JsonNode> res = Unirest
				.post("http://localhost:8080/api/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus")
				.routeParam("namespace", namespace).routeParam("tdefName", tdefName)
				.routeParam("tdefVersion", tdefVersion).header("Content-Type", "application/json;charset=UTF-8")
				.header("Accept", "application/json")

				.header("Authorization",
						"Bearer " + accessToken)
				.body(new JSONObject()
						.put("model", new DefaultKevoreeFactory().createJSONSerializer().serialize(deployUnit))
						.put("name", deployUnit.getName()).put("platform", platform)
						.put("version", deployUnit.getVersion()))
				.asJson();

		System.out.println(res.getBody());

	}

	@Override
	public void createTypeDefinition(String namespace, TypeDefinition typeDefinition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createPackage(List<String> npackages) {
		// TODO Auto-generated method stub

	}

}
