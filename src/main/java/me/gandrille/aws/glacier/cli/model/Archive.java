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
package me.gandrille.aws.glacier.cli.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Archive {
	private String archiveId;
	private String archiveDescription;
	private String creationDate;
	private long size;
	private String sha256TreeHash;
	
	public String getArchiveId() {
		return archiveId;
	}
	
    @JsonProperty("ArchiveId")	
	public void setArchiveId(String archiveId) {
		this.archiveId = archiveId;
	}
	
	public String getArchiveDescription() {
		return archiveDescription;
	}
	
	@JsonProperty("ArchiveDescription")
	public void setArchiveDescription(String archiveDescription) {
		this.archiveDescription = archiveDescription;
	}
	
	public String getCreationDate() {
		return creationDate;
	}
	
	@JsonProperty("CreationDate")
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	
	public long getSize() {
		return size;
	}
	
	@JsonProperty("Size")
	public void setSize(long size) {
		this.size = size;
	}
	
	public String getSHA256TreeHash() {
		return sha256TreeHash;
	}
	
	@JsonProperty("SHA256TreeHash")
	public void setSHA256TreeHash(String sha256TreeHash) {
		this.sha256TreeHash = sha256TreeHash;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((archiveDescription == null) ? 0 : archiveDescription.hashCode());
		result = prime * result + ((archiveId == null) ? 0 : archiveId.hashCode());
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((sha256TreeHash == null) ? 0 : sha256TreeHash.hashCode());
		result = prime * result + (int) (size ^ (size >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Archive other = (Archive) obj;
		if (archiveDescription == null) {
			if (other.archiveDescription != null)
				return false;
		} else if (!archiveDescription.equals(other.archiveDescription))
			return false;
		if (archiveId == null) {
			if (other.archiveId != null)
				return false;
		} else if (!archiveId.equals(other.archiveId))
			return false;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (sha256TreeHash == null) {
			if (other.sha256TreeHash != null)
				return false;
		} else if (!sha256TreeHash.equals(other.sha256TreeHash))
			return false;
		if (size != other.size)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Archive [ArchiveId=" + archiveId + ", ArchiveDescription=" + archiveDescription + ", CreationDate="
				+ creationDate + ", Size=" + size + ", SHA256TreeHash=" + sha256TreeHash + "]";
	}
}
