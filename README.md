# CLI for AWS Glacier 

A simple Java CLI application for managing [AWS S3 Glacier](https://aws.amazon.com/glacier/) vaults and archives. 


## Usage

Command syntax is `java -jar aws-glacier-cli.jar <command-name> <command-parameters>`

Output generated with `java -jar aws-glacier-cli.jar --help`

``` 
Available commands:
--job-status          gets a job status
--create-vault        creates a vault if it doesn't already exists, does nothing otherwise
--describe-vault      gets metadata for a given vault
--describe-vaults     gets metadata for ALL vaults
--delete-vault        deletes a vault if it exists, does nothing otherwise
--ask-vault-inventory ask aws to perform an inventory and displays the job id
--get-vault-inventory gets a vault inventory in one shot
--upload              uploads a single file to a vault in one shot
--ask-archive         ask aws to download an archive and displays the job id
--get-archive         gets an archive
--delete-archive      delete archive
--compute-hash        compute LOCAL files SHA256TreeHash

You can have detail information using --help --command-name

```

Output generated with `java -jar aws-glacier-cli.jar --help --all`

```
--job-status - gets a job status
    * You must provide 2 extra parameters: the vault name and the job id

--create-vault - creates a vault if it doesn't already exists, does nothing otherwise
    * You must provide 1 extra parameter: the name of the vault to be created

--describe-vault - gets metadata for a given vault
    * You must provide 1 extra parameter: the vault name

--describe-vaults - gets metadata for ALL vaults
    * Extra parameters are not allowed

--delete-vault - deletes a vault if it exists, does nothing otherwise
    * You must provide 1 extra parameter: the name of an existing vault

--ask-vault-inventory - ask aws to perform an inventory and displays the job id
    * You must provide 1 extra parameter: the vault name

--get-vault-inventory - gets a vault inventory in one shot
    * You must provide 2 extra parameters: the vault name and the job id

--upload - uploads a single file to a vault in one shot
    * Optional parameters:
      --description  archive description (default value: the file path)
    * You must provide 2 extra parameters: the vault name and the file to be uploaded

--ask-archive - ask aws to download an archive and displays the job id
    * You must provide 2 extra parameters: the vault name and the archive Id

--get-archive - gets an archive
    * You must provide 3 extra parameters: the vault name, the job id, and a file path

--delete-archive - delete archive
    * You must provide 2 extra parameters: the vault name and the archive Id

--compute-hash - compute LOCAL files SHA256TreeHash
    * You must provide at least 1 extra parameter: a list of file path
```


## Credentials

You need to set up your AWS security credentials.
You can do this by creating a file named `credentials` at `~/.aws/` 
(`C:\Users\USER_NAME\.aws\` for Windows users) and saving the following lines in the file:

    [default]
    aws_access_key_id = <your access key id>
    aws_secret_access_key = <your secret key>

See the [Security Credentials](http://aws.amazon.com/security-credentials) page
for more information on getting your keys.


## AWS Region

AWS Region is hard codded into `me.gandrille.aws.glacier.cli.Constants.REGION_NAME`.

For sure, it's not that good; but it's fine with me (at least for the moment).


## Resources

* [Maven sample](https://aws.amazon.com/fr/developers/getting-started/java/)
* [AWS Developper Guide](https://docs.aws.amazon.com/fr_fr/sdk-for-java/v1/developer-guide/welcome.html)
* [Javadoc](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/index.html)
* [Glacier Developper API](https://docs.aws.amazon.com/fr_fr/amazonglacier/latest/dev/introduction.html)

This program is using the AWS blocking API (v1).


## Changelog

**v0.2**

* AWS SDK updated to `v1.11.534`.
* Guava library removed.
* Other dependencies updated to latest version. 
* `UploadMultipartService` class is now deprecated since `UploadService` uses AWS multipart API.


**v0.1**

* Initial version using AWS SDK `v1.8.3`.


## License

This application is distributed under the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
