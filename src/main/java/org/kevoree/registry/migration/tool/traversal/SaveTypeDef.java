package org.kevoree.registry.migration.tool.traversal;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.kevoree.DeployUnit;
import org.kevoree.TypeDefinition;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.registry.client.api.RegistryRestClient;

import com.mashape.unirest.http.exceptions.UnirestException;

public class SaveTypeDef extends TraverseModel {

	private final RegistryRestClient client;

	public SaveTypeDef(final RegistryRestClient client) {
		this.client = client;
	}

	public void createPackage(final List<String> npackages) throws UnirestException {
		final String namespace = StringUtils.join(npackages, '.');
		this.client.postNamespace(namespace);
	}

	public void createTypeDefinition(final String namespace, final TypeDefinition typeDefinition)
			throws JSONException, UnirestException {
		try {
			final DefaultKevoreeFactory defaultKevoreeFactory = new DefaultKevoreeFactory();

			try {
				final String typeDefName = typeDefinition.getName();
				final String typeDefVersion = typeDefinition.getVersion();

				final List<? extends DeployUnit> arrayList = new ArrayList<>();
				typeDefinition.setDeployUnits(arrayList);
				final String model = defaultKevoreeFactory.createJSONSerializer().serialize(typeDefinition);
				this.client.postTypeDef(namespace, model, typeDefName, typeDefVersion);
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
