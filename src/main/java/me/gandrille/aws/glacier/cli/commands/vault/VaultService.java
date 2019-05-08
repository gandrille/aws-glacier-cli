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
package me.gandrille.aws.glacier.cli.commands.vault;

import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.DescribeVaultResult;

import me.gandrille.aws.glacier.cli.commands.CommandService;
import me.gandrille.aws.glacier.cli.commands.Common.PrettyPrinterUtil;
import me.gandrille.aws.glacier.cli.params.ParamsManager;

public abstract class VaultService extends CommandService {	
	
	public VaultService(String name, String description, ParamsManager paramManager) {
		super(name, description, paramManager);
	}

	private static String vaultInfoAsString(String name, String creationDate, String inventoryDate, long nbArchives, long size, String arn) {
		StringBuilder sb = new StringBuilder();	
		sb.append("Information for vault ").append(name).append("\n");
		sb.append(" - CreationDate     : ").append(creationDate).append("\n");
		sb.append(" - LastInventoryDate: ").append(inventoryDate).append("\n");
		sb.append(" - NumberOfArchives : ").append(nbArchives).append("\n");
		sb.append(" - SizeInBytes      : ").append(PrettyPrinterUtil.extendedSizePrettyPrinter(size)).append("\n");
		sb.append(" - VaultARN         : ").append(arn).append("\n");
		return sb.toString();
	}
	
	protected static String vaultInfoAsString(DescribeVaultResult vault) {
		return vaultInfoAsString(vault.getVaultName(), vault.getCreationDate(), vault.getLastInventoryDate(), vault.getNumberOfArchives(), vault.getSizeInBytes(), vault.getVaultARN());
	}
		
	protected static String vaultInfoAsString(DescribeVaultOutput vault) {	
		return vaultInfoAsString(vault.getVaultName(), vault.getCreationDate(), vault.getLastInventoryDate(), vault.getNumberOfArchives(), vault.getSizeInBytes(), vault.getVaultARN());
	}
}
