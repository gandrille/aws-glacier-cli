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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.ListVaultsRequest;
import com.amazonaws.services.glacier.model.ListVaultsResult;

import me.gandrille.aws.glacier.cli.commands.Common.PrettyPrinterUtil;
import me.gandrille.aws.glacier.cli.params.MandatoryParameter;
import me.gandrille.aws.glacier.cli.params.OptionalParameter;
import me.gandrille.aws.glacier.cli.params.ParamsManager;
import me.gandrille.aws.glacier.cli.params.ParamsManager.ParamsParserResult;

public class DescribeAllVaultsService extends VaultService {

	private final static String name = "describe-vaults";
	private final static String description = "gets metadata for ALL vaults";
	
	private static final Logger log = getLogger(DescribeAllVaultsService.class);
	
	public DescribeAllVaultsService() {
		super(name, description, getParamMgr());
	}
		
	private static ParamsManager getParamMgr() {
		List<MandatoryParameter> mandatoryParams = new ArrayList<>();
		List<OptionalParameter> optionalParams = new ArrayList<>();
		return new ParamsManager(mandatoryParams, optionalParams, 0, 0, null);
	}
	
	public void execute(AmazonGlacierClient client, ParamsParserResult params) throws AmazonServiceException, AmazonClientException {	
		ListVaultsRequest request = new ListVaultsRequest();
        ListVaultsResult result = client.listVaults(request);

        List<DescribeVaultOutput> vaultList = result.getVaultList();
        long nbArchives = 0;
        BigInteger totSize = BigInteger.ZERO;
        for (DescribeVaultOutput vault : vaultList) {
        	log.info(VaultService.vaultInfoAsString(vault));
            nbArchives += vault.getNumberOfArchives();
            totSize = totSize.add(new BigInteger(new Long(vault.getSizeInBytes()).toString()));
        }
        
        String size = PrettyPrinterUtil.extendedSizePrettyPrinter(totSize);
        log.info("TOTAL: {} vault(s), {} archive(s), {}", vaultList.size(), nbArchives, size);
	}
}
