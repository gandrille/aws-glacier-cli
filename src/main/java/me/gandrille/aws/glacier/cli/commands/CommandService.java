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

import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.glacier.AmazonGlacierClient;

import me.gandrille.aws.glacier.cli.params.MandatoryParameter;
import me.gandrille.aws.glacier.cli.params.OptionalParameter;
import me.gandrille.aws.glacier.cli.params.ParamsManager;
import me.gandrille.aws.glacier.cli.params.ParamsManager.ParamsParserResult;


public abstract class CommandService {

	private final String name;
	private final String description;
	private final ParamsManager paramManager;
	
	public CommandService(String name, String description, ParamsManager paramManager) {
		this.name = name;
		this.description = description;
		this.paramManager = paramManager;
	}

	public Object getDocumentation() {
		StringBuilder sb = new StringBuilder();
		sb.append("--");
		sb.append(name);
		sb.append(" - ");
		sb.append(description);
		sb.append("\n");
				
		List<MandatoryParameter> mandatoryParams = paramManager.getMandatoryParams();
		if (! mandatoryParams.isEmpty()) {
			sb.append("    * Mandatory parameters:\n");
			for (MandatoryParameter param : mandatoryParams) {
				sb.append("      --");
				sb.append(param.name);
				sb.append(" ");
				for (int i=0; i<12 - param.name.length(); i++)
					sb.append(" ");
				sb.append(param.description);
				sb.append("\n");
			}
		}
		
		 List<OptionalParameter> optionalParams = paramManager.getOptionalParams();
		if (! optionalParams.isEmpty()) {
			sb.append("    * Optional parameters:\n");
			for (OptionalParameter param : optionalParams) {
				sb.append("      --");
				sb.append(param.name);
				sb.append(" ");
				for (int i=0; i<12 - param.name.length(); i++)
					sb.append(" ");
				sb.append(param.description);
				if (param.defaultValue != null) {
					sb.append(" (default value: ");
					sb.append(param.defaultValue);
					sb.append(")");
				}
				sb.append("\n");
			}
		}
		
		if (paramManager.getMaxArg() == 0)
			sb.append("    * Extra parameters are not allowed");
		else if (paramManager.getMinArg() == paramManager.getMaxArg()) {
			sb.append("    * You must provide " + paramManager.getMaxArg() + " extra parameter");
			if (paramManager.getMaxArg() != 1)
				sb.append("s");
			if (paramManager.getDescArgs() != null) {
				sb.append(": ");
				sb.append(paramManager.getDescArgs());
			}
		}
		else if (paramManager.getMaxArg() == -1)  {
			sb.append("    * You must provide at least " + paramManager.getMinArg() + " extra parameter");
			if (paramManager.getMinArg() != 1)
				sb.append("s");
			if (paramManager.getDescArgs() != null) {
				sb.append(": ");
				sb.append(paramManager.getDescArgs());
			}
		} else {
			sb.append("    * You must provide between " + paramManager.getMinArg() + " and " + paramManager.getMaxArg() + " extra parameters\n");
			if (paramManager.getDescArgs() != null) {
				sb.append(": ");
				sb.append(paramManager.getDescArgs());
			}
		}
		sb.append("\n");
				
		return sb.toString();
	}	
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

	public abstract void execute(AmazonGlacierClient client, ParamsParserResult ppr) throws AmazonServiceException, AmazonClientException;

	public ParamsParserResult parseParameters(String[] args) {
		return paramManager.parseParameters(args);
	}
}
