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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.gandrille.aws.glacier.cli.commands.Common.PrettyPrinterUtil;

public class VaultInventory {
	
	private String vaultARN;
	private String inventoryDate;
	private List<Archive> archiveList;

	public String vaultARN() {
		return vaultARN;
	}

    @JsonProperty("VaultARN")
	public void setVaultARN(String vaultARN) {
		this.vaultARN = vaultARN;
	}

	public String getInventoryDate() {
		return inventoryDate;
	}

	@JsonProperty("InventoryDate")
	public void setInventoryDate(String inventoryDate) {
		this.inventoryDate = inventoryDate;
	}

	public List<Archive> getArchiveList() {
		return archiveList;
	}

	@JsonProperty("ArchiveList")
	public void setArchiveList(List<Archive> archiveList) {
		this.archiveList = archiveList;
	}

	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append("vaultARN : ").append(vaultARN).append("\n");
		sb.append("InventoryDate : ").append(inventoryDate).append("\n");
		sb.append("Nb elements : ").append(archiveList.size()).append("\n");
		
		for (Archive a : archiveList) {
			sb.append(" * ArchiveId : ").append(a.getArchiveId()).append("\n");
			sb.append("   ArchiveDescription : ").append(a.getArchiveDescription()).append("\n");
			sb.append("   CreationDate : ").append(a.getCreationDate()).append("\n");			
			sb.append("   SizeInBytes : ").append(PrettyPrinterUtil.extendedSizePrettyPrinter(a.getSize())).append("\n");
			sb.append("   SHA256TreeHash : ").append(a.getSHA256TreeHash()).append("\n");
		}

		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((archiveList == null) ? 0 : archiveList.hashCode());
		result = prime * result + ((inventoryDate == null) ? 0 : inventoryDate.hashCode());
		result = prime * result + ((vaultARN == null) ? 0 : vaultARN.hashCode());
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
		VaultInventory other = (VaultInventory) obj;
		if (archiveList == null) {
			if (other.archiveList != null)
				return false;
		} else if (!archiveList.equals(other.archiveList))
			return false;
		if (inventoryDate == null) {
			if (other.inventoryDate != null)
				return false;
		} else if (!inventoryDate.equals(other.inventoryDate))
			return false;
		if (vaultARN == null) {
			if (other.vaultARN != null)
				return false;
		} else if (!vaultARN.equals(other.vaultARN))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VaultInventory [vaultARN=" + vaultARN + ", inventoryDate=" + inventoryDate + ", archiveList="
				+ archiveList + "]";
	}
}
