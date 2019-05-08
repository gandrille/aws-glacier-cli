/**
 * Copyright (C) 2019 Etienne Gandrille <github@etienne.gandrille.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.gandrille.aws.glacier.cli;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import static org.apache.logging.log4j.LogManager.getLogger;
import org.apache.logging.log4j.Logger;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder;

import me.gandrille.aws.glacier.cli.commands.CommandManager;
import me.gandrille.aws.glacier.cli.commands.CommandService;
import me.gandrille.aws.glacier.cli.params.ParamsManager.ParamsParserResult;

public class Runner {

	private static final Logger log = getLogger(Runner.class);
	
	private static CommandManager commandManager = CommandManager.getInstance();
	
	public static void main(String[] args) throws IOException, ReflectiveOperationException {
		
		CommandService service = getService(args);
		AmazonGlacierClient client = getClient();
		ParamsParserResult ppr = getParams(service, args);

		try {
			service.execute(client, ppr);
		} catch (Exception e) {
			log.error(e.getMessage());
			log.error("Maybe you have forgotten to set your credentials...");
			log.error("see : https://aws.amazon.com/fr/developers/getting-started/java/");
			log.error("      https://console.aws.amazon.com/iam/home?#security_credential");
		}
	}

	private static CommandService getService(String[] args) {

		// short help : --help | -h
		if (args.length == 1 && (args[0].equals("--help") || args[0].equals("-h"))) {
			System.out.println("Available commands:");
			System.out.println(commandManager.getLightDocumentation());
			System.out.println("You can have detail information using --help --command-name");
			System.exit(0);
		}

		// full help : --help --all
		if (args.length == 2 && args[0].equals("--help") && args[1].equals("--all")) {
			System.out.println(commandManager.getFullDocumentation());
			System.exit(0);
		}

		// targeted full help : --help --command-name
		if (args.length == 2 && (args[0].equals("--help") || args[1].startsWith("--"))) {
			CommandService service = commandManager.getService(args[1].substring(2));
			if (service == null) {
				log.error("Unknown command " + args[1]);
				System.exit(-1);
			} else {
				System.out.println(service.getDocumentation());
				System.exit(0);
			}
		}

		// no args or arg not starting with --
		if (args.length == 0 || !args[0].startsWith("--")) {
			log.error("Missing parameters.");
			log.error("Why not using --help ?");
			System.exit(-1);
		}

		// first args checking
		CommandService service = commandManager.getService(args[0].substring(2));
		if (service == null) {
			log.error("Unknown command {}", args[0]);
			System.exit(-1);
		}

		return service;
	}

	private static ParamsParserResult getParams(CommandService service, String[] args) {
		ParamsParserResult ppr = null;
		try {
			ppr = service.parseParameters(args);
		} catch (RuntimeException e) {
			log.error(e.getMessage());
			System.exit(-1);
		}
		return ppr;
	}

	private final static AmazonGlacierClient getClient() throws ReflectiveOperationException {
		Regions region = Regions.fromName(Constants.REGION_NAME);

		// I know it is bad to inject environment variables in memory
		// but I didn't find the way to register a RegionProvider or a RegionProviderChain
		// and since the default one is looking into environment variables... here is the trick.
		updateEnvironment(SDKGlobalConfiguration.AWS_REGION_ENV_VAR, region.getName());

		AmazonGlacierClient client = (AmazonGlacierClient) AmazonGlacierClientBuilder.standard()
				.withRegion(region)
				.withCredentials(new ProfileCredentialsProvider())
				.build();

		return client;
	}

	@SuppressWarnings("unchecked")
	private static void updateEnvironment(String name, String val) throws ReflectiveOperationException {
		Map<String, String> env = System.getenv();
		Field field = env.getClass().getDeclaredField("m");
		field.setAccessible(true);
		((Map<String, String>) field.get(env)).put(name, val);
	}
}
