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
package me.gandrille.aws.glacier.cli.commands.Common;

import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.DescribeJobRequest;
import com.amazonaws.services.glacier.model.GetJobOutputRequest;
import com.amazonaws.services.glacier.model.GetJobOutputResult;

import me.gandrille.aws.glacier.cli.commands.CommandService;
import me.gandrille.aws.glacier.cli.params.ParamsManager;

public abstract class GetService extends CommandService {

	public GetService(String name, String description, ParamsManager paramManager) {
		super(name, description, paramManager);
	}

	public static GetJobOutputResult getJobResult(AmazonGlacierClient client, String vaultName, String jobId) {
		assertJobIsFinished(client, vaultName, jobId);
		GetJobOutputRequest request = new GetJobOutputRequest().withVaultName(vaultName).withJobId(jobId);
		return client.getJobOutput(request);
	}

	protected static void assertJobIsFinished(AmazonGlacierClient client, String vaultName, String jobId) {
		String status = client.describeJob(new DescribeJobRequest(vaultName, jobId)).getStatusCode();
		if (!status.equals("Succeeded")) {
			throw new RuntimeException("job has status " + status);
		}
	}
}
