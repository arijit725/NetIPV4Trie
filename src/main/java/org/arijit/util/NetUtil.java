package org.arijit.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.arijit.netipv4trie.exception.PrefixCalculationException;

public class NetUtil {

	private static int MAX_MASK = 32;

	/**
	 * This method will identify an entity whether IP or CIDR based on its pattern
	 * only.
	 * 
	 * @param entity
	 * @return
	 */
	public static boolean isCidr(String entity) {
		if (entity.contains("/"))
			return true;
		return false;
	}
	/**
	 * <pre>
	 * binaryPrefixString will make sure prefix is properly padded with leading 0.
	 * Example:
	 * 	Consider ip: 10.93.194.195.
	 *  This creates binray string representation of length 28.
	 *  Now Consider ip: 110.93.194.195
	 *  This creates binray string representation of length 31.
	 *  Now we need to make sure both of them are of size 32. 
	 *  Unless when we will build NetIPV4Trie, trie will not have proper branching and it will impact parent child relation.
	 *  For this reason we need to add padding to make Sure IP string representation are all in 32 charecter.
	 *  
	 *  For same reason we padd prefix of CIDR ranges as well.
	 *  This logic is the heart of success of NetIPV4Trie building.
	 *  
	 *  In one word, we need to make sure branching starts from 0 index of binaryPrefix Array
	 * </pre> 
	 * @param prefix
	 * @param mask
	 * @return
	 */

	private static String binaryPrefixString(long prefix, int mask) {
		/*
		 * Base mask for an ip is 32 bit. 
		 */
		long baseMask = 0xffffffffl;
		baseMask = baseMask >> (32 - mask);
		/* an ip is at max 32bit long. So we are here stting 32 bit with 0 preceeding
		   with 1
		   */
		long pad = 0x100000000L; 
		/*
		 * arranging the pad bit. at any point we need to make sure prefixString length
		 * is 32. So we are padding (32-mask) number of bits
		 */
		pad = pad >> (32 - mask);
		String binaryPrefix = Long.toBinaryString(prefix & baseMask |pad).substring(1);
//		System.out.println("Normal: "+Long.toBinaryString(prefix)+" length: "+Long.toBinaryString(prefix).length());
//		System.out.println("Masked: "+binaryPrefix+" length: "+binaryPrefix.length());		
		return binaryPrefix;
	}

	/**
	 * This method will find prefix for the IP/CIDR using masking
	 * 
	 * @param entity
	 * @throws PrefixCalculationException
	 */
	public static String calculatePrefix(String entity) throws PrefixCalculationException {
		/* default mask setting to 32. If IP this will be treated as mask */
		int mask = 32;
		try {
			if (isCidr(entity)) {
				String split[] = entity.split("/");
				entity = split[0];
				mask = Integer.parseInt(split[1]);
			}
			long ip = ipToLong(entity);
			long prefix = ip >>> (MAX_MASK - mask);
			String prefixArray = binaryPrefixString(prefix, mask);
			return prefixArray;
		} catch (Exception ex) {
			throw new PrefixCalculationException("Unable to calculate prefix for: " + entity, ex);
		}

	}

	/**
	 * Convert ipString to long
	 * 
	 * @param ipStr
	 * @return
	 */
	public static long ipToLong(String ipStr) {
		long ip = 0;
		String ipPart[] = ipStr.split("\\.");
		for (int i = 0; i < ipPart.length; i++) {
			ip = ip | (Long.parseLong(ipPart[i]) << ((3 - i) * 8));
		}
		return ip;
	}

	/**
	 * Convert long ip to String representation
	 * 
	 * @param ip
	 * @return
	 */
	public static String longToIp(long ip) {
		String ipStr = (ip >>> 24 & 0xFF) + "." + (ip >>> 16 & 0xFF) + "." + (ip >>> 8 & 0xFF) + "." + (ip & 0xFF);
		return ipStr;
	}

	public static void ipToBinaryString(long ip) {

	}
}
