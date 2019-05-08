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
package me.gandrille.aws.glacier.cli.commands.job;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.DescribeJobRequest;
import com.amazonaws.services.glacier.model.DescribeJobResult;
import com.amazonaws.services.glacier.model.ResourceNotFoundException;

import me.gandrille.aws.glacier.cli.commands.CommandService;
import me.gandrille.aws.glacier.cli.params.MandatoryParameter;
import me.gandrille.aws.glacier.cli.params.OptionalParameter;
import me.gandrille.aws.glacier.cli.params.ParamsManager;
import me.gandrille.aws.glacier.cli.params.ParamsManager.ParamsParserResult;

public class JobStatusService extends CommandService {

	private final static String name = "job-status";
	private final static String description = "gets a job status";
	
	private static final Logger log = getLogger(JobStatusService.class);
	
	public JobStatusService() {
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
			DescribeJobResult jobResult = client.describeJob(new DescribeJobRequest(vaultName, jobId));
		
			log.info("Main job info");
			log.info("  getAction         : {}", jobResult.getAction());
			log.info("  getCreationDate   : {}", jobResult.getCreationDate());		
			log.info("  getCompletionDate : {}", jobResult.getCompletionDate());
			log.info("  getStatusCode     : {}", jobResult.getStatusCode());
			log.info("  getCompleted      : {}", jobResult.getCompleted());
			log.info("  getVaultARN       : {}", jobResult.getVaultARN());
			
		} catch (ResourceNotFoundException e) {
			log.error("Unknown job");
		}
	}
}
