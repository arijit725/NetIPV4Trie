NetIPV4Trie

NetIPV4Trie is an algorithm to search parent child CIDR/IP combination based on Longest Prefix Matching algorithm, which is implemented in Router.
This algorithm performs below tasks:

	- 1. It can search an IP or a CIDR in a list of CIDR for all the parents present
	- 2. It can search for immediate parent of an IP
	- 3. It can filter out IPS from the list whose parents are already present.
	
These are the techniques any router uses to find to which hop incoming packets will be forwarded.
Normally. Hop with Longest Prefix match is the next hop.
Routers basically look at destination address’s IP prefix, searches the forwarding table for a match and forwards the packet to corresponding next hop in forwarding table.
When Prefixes overlap routers use Longest Prefix Matching rule. The rule is to find the entry in table which has the longest prefix matching with incoming packet’s destination IP, and forward the packet to corresponding next hope.

- In this algorithm we are trying to build this NAT table in-memory.	
