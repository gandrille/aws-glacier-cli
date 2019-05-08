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

import java.math.BigInteger;
import java.util.Map;

public abstract class PrettyPrinterUtil {
	
	public static String extendedSizePrettyPrinter(BigInteger size) {
		return sizePrettyPrinter(size) + " (" + size + " B)";
	}
	
	public static String extendedSizePrettyPrinter(String size) {
		return sizePrettyPrinter(size) + " (" + size + " B)";
	}
	
	public static String extendedSizePrettyPrinter(long size) {
		return sizePrettyPrinter(size) + " (" + size + " B)";
	}
	
	public static String sizePrettyPrinter(String size) {
		return sizePrettyPrinter(new BigInteger(size));
	}
	
	public static String sizePrettyPrinter(long size) {
		return sizePrettyPrinter(new BigInteger(String.valueOf(size)));
	}
	
	public static String DescriptionPrettyPrinter(String desc) {
		String oldChar = "éèêàç";
		String newChar = "eeeac";
		
		for (int i = 0; i< oldChar.length(); i++) {
			desc = desc.replace(oldChar.substring(i, i+1), newChar.substring(i, i+1));
		}
		return desc;	
	}
	
	public static String sizePrettyPrinter(BigInteger size) {
		 
		String[] postfix = {"B","KB","MB","GB", "TB", "PB"};
		BigInteger pow = new BigInteger("1024");
		BigInteger remain = BigInteger.ZERO;
		
		for (int i=0; i<postfix.length; i++) {
			if (size.compareTo(pow) == -1) {
				if (remain.equals(BigInteger.ZERO))
					return String.format("%d %s", size, postfix[i]);	
				else
					return String.format("%d.%03d %s", size, remain, postfix[i]);
			}
			remain = size.mod(pow);
			size = size.divide(pow);
		}
		throw new RuntimeException("Don't you think it's a too big archive to be recored in the cloud ?");
	}
	
	public static String listPrettyPrinter(Map<String, String> map, String beforeLine) {
		StringBuilder sb = new StringBuilder();
		
		int max = 0;
		for (String key : map.keySet())
			if (max < key.length())
				max = key.length();
		
		for (String key : map.keySet()) {
			sb.append(beforeLine);
			sb.append(key);
			for (int i=0; i<max - key.length(); i++)
				sb.append(" ");
			sb.append(" : ");
			sb.append(map.get(key));
			sb.append("\n");
		}

		return sb.toString();
	}
}
