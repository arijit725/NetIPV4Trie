package org.arijit.netipv4trie.test;

import java.util.List;

import org.arijit.netipv4trie.core.trie.NetIPV4Trie;
import org.arijit.netipv4trie.exception.PrefixCalculationException;

public class TestTrie {

	public static void main(String args[]) throws PrefixCalculationException {
		NetIPV4Trie<String> trie = NetIPV4Trie.create();
		String cidr = "10.211.213.189/30";
		String cidr2 = "10.211.213.190/31";
		String ip1 = "10.211.213.191";
		String ip2 = "10.211.213.192";
		String ip3 = "10.211.213.190";
		String ip4 = "110.233.43.255";
		
		
		trie.insert(ip1, ip1);
		trie.insert(ip2, ip2);
		trie.insert(cidr, cidr);
		trie.insert(cidr2, cidr2);
		trie.insert(ip3, ip3);
		trie.insert(ip3, ip3);
		trie.insert(ip4, ip4);
		
		String search = "10.211.213.190/31";
		List<String> parents = trie.findParents(search,false);
		System.out.println("Entity: "+search+" parents: "+parents);
	}
}
