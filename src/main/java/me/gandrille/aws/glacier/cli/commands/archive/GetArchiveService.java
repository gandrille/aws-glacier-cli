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
package me.gandrille.aws.glacier.cli.commands.archive;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.ResourceNotFoundException;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManagerBuilder;

import me.gandrille.aws.glacier.cli.commands.Common.GetService;
import me.gandrille.aws.glacier.cli.params.MandatoryParameter;
import me.gandrille.aws.glacier.cli.params.OptionalParameter;
import me.gandrille.aws.glacier.cli.params.ParamsManager;
import me.gandrille.aws.glacier.cli.params.ParamsManager.ParamsParserResult;

public class GetArchiveService extends GetService {

	private final static String name = "get-archive";
	private final static String description = "gets an archive";
	
	private static final Logger log = getLogger(GetArchiveService.class);
	
	public GetArchiveService() {
		super(name, description, getParamMgr());
	}
		
	private static ParamsManager getParamMgr() {
		List<MandatoryParameter> mandatoryParams = new ArrayList<>();
		List<OptionalParameter> optionalParams = new ArrayList<>();
		return new ParamsManager(mandatoryParams, optionalParams, 3, 3, "the vault name, the job id, and a file path");
	}
	
	public void execute(AmazonGlacierClient client, ParamsParserResult params) throws AmazonServiceException, AmazonClientException {	
		String vaultName = params.args.get(0);
		String jobId = params.args.get(1);
		String filePath = params.args.get(2);
		
		try {
			assertJobIsFinished(client, vaultName, jobId);
			ArchiveTransferManagerBuilder builder = new ArchiveTransferManagerBuilder();
			ArchiveTransferManager atm = builder.withGlacierClient(client).build();
			atm.downloadJobOutput(null, vaultName, jobId, new File(filePath));				
		} catch (ResourceNotFoundException e) {
			log.error("Can't find jobId in vault");
			return;
		} catch (RuntimeException e) {
			log.error(e);
			return;
		}
	}
}
