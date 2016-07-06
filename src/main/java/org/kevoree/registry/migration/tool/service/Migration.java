package org.kevoree.registry.migration.tool.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.kevoree.ContainerRoot;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.registry.client.api.OAuthRegistryClient;
import org.kevoree.registry.client.api.RegistryRestClient;
import org.kevoree.registry.migration.tool.traversal.SaveDeployUnits;
import org.kevoree.registry.migration.tool.traversal.SaveTypeDef;

import com.mashape.unirest.http.exceptions.UnirestException;

public class Migration {
	public void wholeProcess(final String login, final String password, final String serverPath, final String filePath)
			throws UnirestException, FileNotFoundException {
		final String accessToken = new OAuthRegistryClient(serverPath).getToken(login, password);
		final RegistryRestClient client = new RegistryRestClient(serverPath, accessToken);
		new SaveTypeDef(client).recPackages(loadModel(filePath));
		new SaveDeployUnits(client).recPackages(loadModel(filePath));
	}

	private ContainerRoot loadModel(final String filePath) throws FileNotFoundException {
		final KevoreeFactory kevoreeFactory = new DefaultKevoreeFactory();
		return (ContainerRoot) kevoreeFactory.createJSONLoader()
				.loadModelFromStream(new FileInputStream(new File(filePath))).get(0);
	}
}
