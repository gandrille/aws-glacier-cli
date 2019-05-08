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
package me.gandrille.aws.glacier.cli.params;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamsManager {

	private final List<MandatoryParameter> mandatoryParams;
	private final List<OptionalParameter> optionalParams;
	private final int minArg;
	private final int maxArg;
	private final String descArgs;
	
	public ParamsManager(List<MandatoryParameter> mandatoryParams, List<OptionalParameter> optionalParams, int minArg, int maxArg, String descArgs) {
		this.mandatoryParams = mandatoryParams;
		this.optionalParams = optionalParams;
		this.minArg = minArg;
		this.maxArg = maxArg;
		this.descArgs = descArgs;
	}
	
	public List<MandatoryParameter> getMandatoryParams() {
		return mandatoryParams;
	}

	public List<OptionalParameter> getOptionalParams() {
		return optionalParams;
	}
	
	public int getMinArg() {
		return minArg;
	}
	
	public int getMaxArg() {
		return maxArg;
	}
	
	public String getDescArgs() {
		return descArgs;
	}
	
	public ParamsParserResult parseParameters(String[] params) {
		Map<String, String> kv = new HashMap<>();
		List<String> args = new ArrayList<>();
		
		boolean inParam = true;
		String readParam = null;
		for (int i=1 /*idx 0 is the command !*/; i< params.length; i++) {
			String cur = params[i];
			if (inParam) {
				// is it a new parameter ?
				if (readParam == null) {
					if (cur.equals("--"))
						inParam = false;
					else if (cur.startsWith("--"))
						readParam = cur.substring(2);
					else {
						inParam = false;
						args.add(cur);
					}
				
				// parameter value
				} else {
					kv.put(readParam, cur);
					readParam = null;
				}
			} else {
				args.add(cur);
			}
		}
		
		if (readParam != null)
			throw new RuntimeException("parameter " + readParam + " has no value");
		
		// All mandatory parameters must be defined
		for (MandatoryParameter param : mandatoryParams)
			if (kv.get(param.name) == null)
				throw new RuntimeException("mandatory parameter " + param.name + " undefined");
		
		// all parameters must be mandatory or optional
		for (String key : kv.keySet()) {
			boolean found = false;
			
			for (int i=0; i<mandatoryParams.size() && !found; i++)
				if (mandatoryParams.get(i).name.equals(key))
					found = true;
			for (int i=0; i<optionalParams.size() && !found; i++)
				if (optionalParams.get(i).name.equals(key))
					found = true;
			
			if (!found)
				throw new RuntimeException("invalid parameter " + key);
		}
		
		// args size checking
		if (args.size() < minArg)
			throw new RuntimeException("you must provide at least " + minArg + " parameters");
		if (maxArg != -1 && args.size() > maxArg)
			throw new RuntimeException("you must provide less than " + maxArg + " parameters");
		
		// default values management
		for (OptionalParameter param : optionalParams)
			if (kv.get(param.name) == null)
				kv.put(param.name, param.defaultValue);
		
		return new ParamsParserResult(kv, args);
	}
	
	public class ParamsParserResult {

		public final Map<String, String> kv;
		public final List<String> args;
		
		public ParamsParserResult(Map<String, String> kv, List<String> args) {
			this.kv = kv;
			this.args = args;
		}
	}
}
