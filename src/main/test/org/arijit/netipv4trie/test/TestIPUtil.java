package org.arijit.netipv4trie.test;

import org.arijit.netipv4trie.exception.PrefixCalculationException;
import org.arijit.util.NetUtil;
import org.arijit.util.StringUtil;

public class TestIPUtil {

	public static void main(String args[]) throws PrefixCalculationException {
		String ipStr = "10.93.194.195";
		long ip=NetUtil.ipToLong(ipStr);
		System.out.println(ip);
		
		System.out.println(NetUtil.longToIp(ip));
		String parentCidr = "10.93.194.193/30";
		String ipprefix = NetUtil.calculatePrefix(ipStr);
		String parentprefix = NetUtil.calculatePrefix(parentCidr);
		System.out.println(parentCidr+" "+parentprefix+" Length: "+parentprefix.length());
		System.out.println(ipStr+" "+ipprefix+" Length: "+ipprefix.length());
		
		int commonIndex = StringUtil.findCommonPrefix(ipprefix, parentprefix);
		if(commonIndex==-1)
			System.out.println("No match Found");
		else 
			System.out.println("Common Part: "+parentprefix.substring(0, commonIndex+1));
		
	}
}
