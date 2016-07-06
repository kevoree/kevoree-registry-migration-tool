package org.kevoree.registry.migration.tool.traversal;

import java.util.List;
import java.util.Objects;

import org.json.JSONException;
import org.kevoree.DeployUnit;
import org.kevoree.TypeDefinition;
import org.kevoree.Value;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.registry.client.api.RegistryRestClient;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;

public class SaveDeployUnits extends TraverseModel {

	private final RegistryRestClient client;

	public SaveDeployUnits(final RegistryRestClient client) {
		this.client = client;
	}

	@Override
	public void submitDu(final String namespace, final DeployUnit deployUnit, final String tdefName,
			final String tdefVersion) throws JSONException, UnirestException {
		final String platform = deployUnit.getFilters().stream().filter(t -> Objects.equals(t.getName(), "platform"))
				.map(Value::getValue).findFirst().orElse("");
		final String model = new DefaultKevoreeFactory().createJSONSerializer().serialize(deployUnit);
		final String duName = deployUnit.getName();
		final String duVersion = deployUnit.getVersion();
		final HttpResponse<JsonNode> res = this.client.submitDU(namespace, tdefName, tdefVersion, platform, model,
				duName, duVersion);

		System.out.println(res.getBody());

	}

	@Override
	public void createTypeDefinition(final String namespace, final TypeDefinition typeDefinition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createPackage(final List<String> npackages) {
		// TODO Auto-generated method stub

	}

}
