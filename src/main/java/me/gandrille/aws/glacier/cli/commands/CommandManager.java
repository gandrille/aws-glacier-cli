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
package me.gandrille.aws.glacier.cli.commands;

import java.util.ArrayList;
import java.util.List;

import me.gandrille.aws.glacier.cli.commands.archive.AskArchiveService;
import me.gandrille.aws.glacier.cli.commands.archive.DeleteArchiveService;
import me.gandrille.aws.glacier.cli.commands.archive.GetArchiveService;
import me.gandrille.aws.glacier.cli.commands.archive.TreeHashService;
import me.gandrille.aws.glacier.cli.commands.archive.UploadService;
import me.gandrille.aws.glacier.cli.commands.job.JobStatusService;
import me.gandrille.aws.glacier.cli.commands.vault.AskVaultInventoryService;
import me.gandrille.aws.glacier.cli.commands.vault.CreateVaultService;
import me.gandrille.aws.glacier.cli.commands.vault.DeleteVaultService;
import me.gandrille.aws.glacier.cli.commands.vault.DescribeAllVaultsService;
import me.gandrille.aws.glacier.cli.commands.vault.DescribeVaultService;
import me.gandrille.aws.glacier.cli.commands.vault.GetVaultInventoryService;

public class CommandManager {

	private static final CommandManager INSTANCE = new CommandManager();
	
	private final List<CommandService> services;
	
	private CommandManager() {
		services = new ArrayList<>();
		
		// common
		services.add(new JobStatusService());
		
		// Vault services
		services.add(new CreateVaultService());
		services.add(new DescribeVaultService());
		services.add(new DescribeAllVaultsService());
		services.add(new DeleteVaultService());
		services.add(new AskVaultInventoryService());
		services.add(new GetVaultInventoryService());

		// Archive services
		services.add(new UploadService());
		//services.add(new UploadMultipartService());
		services.add(new AskArchiveService());
		services.add(new GetArchiveService());
		services.add(new DeleteArchiveService());
		services.add(new TreeHashService());
	}
	
	public static CommandManager getInstance() {
		return INSTANCE;
	}
	
	public CommandService getService(String name) {
		for (CommandService cs : services)
			if (cs.getName().equals(name))
				return cs;
		return null;
	}

	public Object getFullDocumentation() {		
		StringBuilder sb = new StringBuilder();
		for (CommandService service : services) {			
			sb.append("\n");
			sb.append(service.getDocumentation());
		}
		return sb.toString();
	}
	
	public Object getLightDocumentation() {		
		int max = 0;
		for (CommandService service : services) {			
			if (max < service.getName().length())
				max = service.getName().length();
		}
		
		StringBuilder sb = new StringBuilder();
		for (CommandService service : services) {			
			sb.append("--");
			sb.append(service.getName());
			for (int i=0; i <= max -service.getName().length(); i++)
				sb.append(" ");
			sb.append(service.getDescription());
			sb.append("\n");
		}
		return sb.toString();
	}
}
