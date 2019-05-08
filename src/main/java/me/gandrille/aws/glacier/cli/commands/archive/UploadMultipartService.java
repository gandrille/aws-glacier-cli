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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.TreeHashGenerator;
import com.amazonaws.services.glacier.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.glacier.model.CompleteMultipartUploadResult;
import com.amazonaws.services.glacier.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.glacier.model.InitiateMultipartUploadResult;
import com.amazonaws.services.glacier.model.UploadMultipartPartRequest;
import com.amazonaws.services.glacier.model.UploadMultipartPartResult;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.util.BinaryUtils;

import me.gandrille.aws.glacier.cli.commands.CommandService;
import me.gandrille.aws.glacier.cli.commands.Common.PrettyPrinterUtil;
import me.gandrille.aws.glacier.cli.params.MandatoryParameter;
import me.gandrille.aws.glacier.cli.params.OptionalParameter;
import me.gandrille.aws.glacier.cli.params.ParamsManager;
import me.gandrille.aws.glacier.cli.params.ParamsManager.ParamsParserResult;

/**
 * This class was used in the first implementation. 
 * Thanks to {@link ArchiveTransferManager} class, it is not needed anymore (even if it was working well).
 */
@Deprecated
public class UploadMultipartService extends CommandService {

	private final static String name = "upload-multipart";
	private final static String description = "uploads a single file to a vault cutting it into parts";
	
	private final static String msgKey = "the file path";
	
	private static final Logger log = getLogger(UploadMultipartService.class);
	
	public UploadMultipartService() {
		super(name, description, getParamMgr());
	}
		
	private static ParamsManager getParamMgr() {
		List<MandatoryParameter> mandatoryParams = new ArrayList<>();
		
		List<OptionalParameter> optionalParams = new ArrayList<>();
		optionalParams.add(new OptionalParameter("part-size", "the part size (a power of 2), for eg 1MB, 8MB, 64MB, 1GB,...", "128MB"));		
		optionalParams.add(new OptionalParameter("description", "archive description", msgKey));
		
		return new ParamsManager(mandatoryParams, optionalParams, 2, 2, "the vault name, the file to be uploaded");
	}
	
	public void execute(AmazonGlacierClient client, ParamsParserResult params) throws AmazonServiceException, AmazonClientException {	
		String vaultName = params.args.get(0);
		String filePath = params.args.get(1);
		String archDesc = params.kv.get("description");
		if (archDesc.equals(msgKey)) archDesc = filePath;
		archDesc = PrettyPrinterUtil.DescriptionPrettyPrinter(archDesc);
		String ps = params.kv.get("part-size");
	    String partSize = parsePartSize(ps); 

	    try {
	    	
	    	long fileLength = new File(filePath).length();
	    	long l = Long.valueOf(partSize);
	    	long nbIter = fileLength / l;
	    	if (fileLength % l != 0)
	    		nbIter++;
	    	
            log.info("Uploading the archive {}", filePath);
            log.info(" - tot length : {}", PrettyPrinterUtil.extendedSizePrettyPrinter(fileLength));
            log.info(" - part size  : {}", PrettyPrinterUtil.extendedSizePrettyPrinter(partSize));
            log.info(" - nb parts   : {}", nbIter);
            
            // init
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest().withVaultName(vaultName).withArchiveDescription(archDesc).withPartSize(partSize);
            InitiateMultipartUploadResult result = client.initiateMultipartUpload(request);        
            String uploadId = result.getUploadId();
            log.info(" - upload id  : {}", uploadId);

            // upload
            String checksum = uploadParts(client, uploadId, vaultName, filePath, partSize, nbIter);
            
            // finalize
            CompleteMultipartUploadResult uploadResult = CompleteMultiPartUpload(client, uploadId, vaultName, filePath, checksum);
            log.info("Archive uploaded. ArchiveId: {}", uploadResult.getArchiveId());
            
        } catch (Exception e) {
            log.error(e);
        }
	}

    private String parsePartSize(String partSize) {
    	long factor;
    	String radix;
    	if (partSize.endsWith("GB")) {
    		factor = 1073741824;
    		radix = partSize.substring(0, partSize.length()-2).trim();
    	}
    	else if (partSize.endsWith("MB")) {
    		factor = 1048576;
      		radix = partSize.substring(0, partSize.length()-2).trim();
    	}
    	else if (partSize.endsWith("KB")) {
    		factor = 1024;
      		radix = partSize.substring(0, partSize.length()-2).trim();
    	}
    	else
    		throw new IllegalArgumentException("part size " + partSize + " malformed");
    	
    	long nb = Long.valueOf(radix);
    	long value = nb * factor; 
		
    	return Long.toString(value);
	}

	private static String uploadParts(AmazonGlacierClient client, String uploadId, String vaultName, String filePath, String partSize, long nbIter) throws AmazonServiceException, NoSuchAlgorithmException, AmazonClientException, IOException {

        int filePosition = 0;
        long currentPosition = 0;
        byte[] buffer = new byte[Integer.valueOf(partSize)];
        List<byte[]> binaryChecksums = new LinkedList<byte[]>();
        
        File file = new File(filePath);
        FileInputStream fileToUpload = new FileInputStream(file);
        String contentRange;
        int read = 0;
        for (int cpt = 0; currentPosition < file.length(); cpt++)
        {
            read = fileToUpload.read(buffer, filePosition, buffer.length);
            if (read == -1) { break; }
            byte[] bytesRead = Arrays.copyOf(buffer, read);

            contentRange = String.format("bytes %s-%s/*", currentPosition, currentPosition + read - 1);
            String checksum = TreeHashGenerator.calculateTreeHash(new ByteArrayInputStream(bytesRead));
            byte[] binaryChecksum = BinaryUtils.fromHex(checksum);
            binaryChecksums.add(binaryChecksum);
            
            
            DateTime now1 = DateTime.now();
            String ts1=String.format("%02d:%02d", now1.getHourOfDay(), now1.getMinuteOfHour());
            log.info(ts1 + " " + (cpt+1) + "/" + nbIter + " uploading " + contentRange);
                        
            //Upload part.
            UploadMultipartPartRequest partRequest = new UploadMultipartPartRequest()
            .withVaultName(vaultName)
            .withBody(new ByteArrayInputStream(bytesRead))
            .withChecksum(checksum)
            .withRange(contentRange)
            .withUploadId(uploadId);               
        
            UploadMultipartPartResult partResult = client.uploadMultipartPart(partRequest);
            
            DateTime now2 = DateTime.now();
            String ts2=String.format("%02d:%02d", now2.getHourOfDay(), now2.getMinuteOfHour());
            log.info(ts2 + " " + (cpt+1) + "/" + nbIter + " uploaded, checksum: " + partResult.getChecksum());
            
            currentPosition = currentPosition + read;
        }
        fileToUpload.close();
        String checksum = TreeHashGenerator.calculateTreeHash(binaryChecksums);
        return checksum;
    }

    private static CompleteMultipartUploadResult CompleteMultiPartUpload(AmazonGlacierClient client, String uploadId, String vaultName, String filePath, String checksum) throws NoSuchAlgorithmException, IOException {
        
        File file = new File(filePath);

        CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest()
            .withVaultName(vaultName)
            .withUploadId(uploadId)
            .withChecksum(checksum)
            .withArchiveSize(String.valueOf(file.length()));
        
        return client.completeMultipartUpload(compRequest);
    }
}