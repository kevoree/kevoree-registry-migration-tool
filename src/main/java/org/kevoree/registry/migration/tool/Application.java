package org.kevoree.registry.migration.tool;

import java.io.FileNotFoundException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by mleduc on 05/07/16.
 */
public class Application {

	public static void main(final String[] args) throws UnirestException, FileNotFoundException {

		final Options options = new Options();
		options.addOption("l", "login", true, "New registry login");
		options.addOption("p", "password", true, "New registry password");
		options.addOption("s", "server", true, "New registry server url (eg http://host:port)");
		options.addOption("f", "file", true, "Old registry json model dump");

		final CommandLineParser parser = new DefaultParser();
		try {
			final CommandLine res = parser.parse(options, args);

			final String login = getMandatoryValue(res, 'l');
			final String password = getMandatoryValue(res, 'p');
			final String serverPath = getMandatoryValue(res, 's');
			final String filePath = getMandatoryValue(res, 'f');

			new Migration().wholeProcess(login, password, serverPath, filePath);
		} catch (final ParseException e) {
			System.out.println(e.getMessage());
			new HelpFormatter().printHelp("registry-migration-tool", options);
		}

	}

	private static String getMandatoryValue(CommandLine res, char optionValue) throws ParseException {
		final String login;
		if (res.getOptionValue(optionValue) != null) {
			login = res.getOptionValue(optionValue);
		} else {
			throw new ParseException("option -" + optionValue + " not defined");
		}
		return login;
	}

}
