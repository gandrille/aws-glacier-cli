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

import static org.apache.logging.log4j.LogManager.getLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.GetJobOutputResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.gandrille.aws.glacier.cli.commands.Common.GetService;
import me.gandrille.aws.glacier.cli.model.VaultInventory;
import me.gandrille.aws.glacier.cli.params.MandatoryParameter;
import me.gandrille.aws.glacier.cli.params.OptionalParameter;
import me.gandrille.aws.glacier.cli.params.ParamsManager;
import me.gandrille.aws.glacier.cli.params.ParamsManager.ParamsParserResult;

public class GetVaultInventoryService extends GetService {

	private final static String name = "get-vault-inventory";
	private final static String description = "gets a vault inventory in one shot";

	private static final Logger log = getLogger(GetVaultInventoryService.class);
	
	public GetVaultInventoryService() {
		super(name, description, getParamMgr());
	}

	private static ParamsManager getParamMgr() {
		List<MandatoryParameter> mandatoryParams = new ArrayList<>();
		List<OptionalParameter> optionalParams = new ArrayList<>();
		return new ParamsManager(mandatoryParams, optionalParams, 2, 2, "the vault name and the job id");
	}

	public void execute(AmazonGlacierClient client, ParamsParserResult params) throws AmazonServiceException, AmazonClientException {
		String vaultName = params.args.get(0);
		String jobId = params.args.get(1);

		try {
			GetJobOutputResult getJobOutputResult = getJobResult(client, vaultName, jobId);
			try {
				VaultInventory inventory = new ObjectMapper().readValue(getJobOutputResult.getBody(), VaultInventory.class);
				// looking forward to use Java 11 and String::lines
				for (String line : inventory.prettyPrint().split("\n"))
					log.info(line);
			} catch (IOException e) {
				log.error("Vault {} can't be listed: {}", vaultName, e.getMessage());
			}
		} catch (Exception e) {
			log.error("Error while listing vault {}: {}", vaultName, e.getMessage());
		}
	}
}
