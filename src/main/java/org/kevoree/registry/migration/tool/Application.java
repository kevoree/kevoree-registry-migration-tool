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

/**
 * Created by mleduc on 05/07/16.
 */
public class Application {

	public static void main(final String[] args) throws UnirestException, FileNotFoundException {

		final String login = "admin";
		final String password = "admin";
		final HttpResponse<JsonNode> res = Unirest.post("http://localhost:8080/oauth/token")
				.basicAuth("kevoree_registryapp", "kevoree_registryapp_secret").header("Accept", "application/json")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.body("username=" + login + "&password=" + password
						+ "&grant_type=password&scope=read%20write&client_secret=kevoree_registryapp_secret&client_id=kevoree_registryapp")
				.asJson();

		System.out.println(res.getBody());

		final String accessToken = res.getBody().getObject().getString("access_token");

		final KevoreeFactory kevoreeFactory = new DefaultKevoreeFactory();

		new SaveTypeDef(accessToken).recPackages((ContainerRoot) kevoreeFactory.createJSONLoader()
				.loadModelFromStream(new FileInputStream(new File("/tmp/kevoree-registry.json"))).get(0));

		new SaveDeployUnits(accessToken).recPackages((ContainerRoot) kevoreeFactory.createJSONLoader()
				.loadModelFromStream(new FileInputStream(new File("/tmp/kevoree-registry.json"))).get(0));

	}

}
