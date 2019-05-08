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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.ResourceNotFoundException;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManagerBuilder;
import com.amazonaws.services.glacier.transfer.UploadResult;

import me.gandrille.aws.glacier.cli.commands.CommandService;
import me.gandrille.aws.glacier.cli.commands.Common.PrettyPrinterUtil;
import me.gandrille.aws.glacier.cli.params.MandatoryParameter;
import me.gandrille.aws.glacier.cli.params.OptionalParameter;
import me.gandrille.aws.glacier.cli.params.ParamsManager;
import me.gandrille.aws.glacier.cli.params.ParamsManager.ParamsParserResult;

public class UploadService extends CommandService {

	private final static String name = "upload";
	private final static String description = "uploads a single file to a vault in one shot";
	private final static String msgKey = "the file path";
	
	private static final Logger log = getLogger(UploadService.class);
	
	public UploadService() {
		super(name, description, getParamMgr());
	}
		
	private static ParamsManager getParamMgr() {
		List<MandatoryParameter> mandatoryParams = new ArrayList<>();
		List<OptionalParameter> optionalParams = new ArrayList<>();

		optionalParams.add(new OptionalParameter("description", "archive description", msgKey));
		
		return new ParamsManager(mandatoryParams, optionalParams, 2, 2, "the vault name and the file to be uploaded");
	}
	
	public void execute(AmazonGlacierClient client, ParamsParserResult params) throws AmazonServiceException, AmazonClientException {	
		String vaultName = params.args.get(0);
		String filePath = params.args.get(1);
		File file = new File(filePath);
		String desc = params.kv.get("description");
		if (desc.equals(msgKey)) desc = filePath;
		desc = PrettyPrinterUtil.DescriptionPrettyPrinter(desc);
		
		ArchiveTransferManager atm = new ArchiveTransferManagerBuilder().withGlacierClient(client).build();
		
        try {
        	UploadResult result = atm.upload(vaultName, desc, file);
			log.info("ArchiveID: {}", result.getArchiveId());
		} catch (FileNotFoundException e) {
			log.error("Can't find file {}", filePath);
		} catch (ResourceNotFoundException e) {
			log.error("Vault {} not found", vaultName);
		} catch (AmazonClientException e) {
			log.error(e);
		}
	}
}
