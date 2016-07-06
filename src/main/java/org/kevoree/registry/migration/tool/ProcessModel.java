package org.kevoree.registry.migration.tool;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.kevoree.ContainerRoot;
import org.kevoree.DeployUnit;
import org.kevoree.Package;
import org.kevoree.TypeDefinition;

import com.mashape.unirest.http.exceptions.UnirestException;

public abstract class ProcessModel {

	public final void recPackages(final ContainerRoot model) throws UnirestException {
		for (final Package p : model.getPackages()) {
			innerLoop(new ArrayList<>(), p);
		}

	}

	private final void innerLoop(final List<String> packages, final Package p) throws UnirestException {
		final List<String> npackages = new ArrayList<>(packages);
		npackages.add(p.getName());
		for (final TypeDefinition typeDefinition : p.getTypeDefinitions()) {
			this.createPackage(npackages);
			final String namespace = StringUtils.join(npackages, '.');
			createTypeDefinition(namespace, typeDefinition);
			for (final DeployUnit du : typeDefinition.getDeployUnits()) {
				submitDu(namespace, du, typeDefinition.getName(), typeDefinition.getVersion());
			}
		}
		recPackages(p, npackages);
	}

	private final void recPackages(final Package currentPackage, final List<String> packages) throws UnirestException {
		for (final Package p : currentPackage.getPackages()) {
			innerLoop(packages, p);
		}

	}

	public abstract void submitDu(String namespace, DeployUnit du, String name, String version) throws UnirestException;

	public abstract void createTypeDefinition(String namespace, TypeDefinition typeDefinition)
			throws JSONException, UnirestException;

	public abstract void createPackage(List<String> npackages) throws UnirestException;

}
