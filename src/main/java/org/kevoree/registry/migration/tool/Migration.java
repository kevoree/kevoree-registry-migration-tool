package org.kevoree.registry.migration.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.kevoree.ContainerRoot;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Migration {
	public void wholeProcess(final String login, final String password, final String serverPath, final String filePath)
			throws UnirestException, FileNotFoundException {
		final HttpResponse<JsonNode> res = Unirest.post(serverPath + "/oauth/token")
				.basicAuth("kevoree_registryapp", "kevoree_registryapp_secret").header("Accept", "application/json")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.body("username=" + login + "&password=" + password
						+ "&grant_type=password&scope=read%20write&client_secret=kevoree_registryapp_secret&client_id=kevoree_registryapp")
				.asJson();

		System.out.println(res.getBody());

		final String accessToken = res.getBody().getObject().getString("access_token");
		new SaveTypeDef(accessToken, serverPath).recPackages(loadModel(filePath));
		new SaveDeployUnits(accessToken, serverPath).recPackages(loadModel(filePath));
	}

	private ContainerRoot loadModel(final String filePath) throws FileNotFoundException {
		final KevoreeFactory kevoreeFactory = new DefaultKevoreeFactory();
		return (ContainerRoot) kevoreeFactory.createJSONLoader()
				.loadModelFromStream(new FileInputStream(new File(filePath))).get(0);
	}
}
