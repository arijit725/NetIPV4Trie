package org.arijit.netipv4trie.core.trie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.arijit.netipv4trie.exception.PrefixCalculationException;
import org.arijit.util.NetUtil;
import org.arijit.util.StringUtil;

/**
 * NetIPV4Trie will arrange NetTrieNode based on prefix order. Here we will use
 * Patricia trie to make trie memory optimised which means only when branching
 * required, we will split the prefix and make children.
 * 
 * In this trie key will be always String type. But user can insert value of any
 * type.
 * 
 * At any point of time maximum depth of trie will be 33. [Bit count in IP (32)
 * + dummy root]. As the depth is deterministic (max 32), we can use recursion.
 * 
 * This trie can not be used in multithreaded mode. We need to create trie in
 * sequence. Unless we will not be able to maintain parent-child relationship of
 * CIDR and IP.
 * 
 * @author ARIJIT
 *
 */
public class NetIPV4Trie<T extends Object> {

	/**
	 * Root is a dummy node, will not contain any value.
	 */
	NetTrieNode<String, T> root;

	private NetIPV4Trie() {
		root = NetTrieNode.create(null, null);
	}

	/**
	 * Create a trie. Specify the Value type.
	 * 
	 * @param <T>
	 * @return
	 */
	public static <T> NetIPV4Trie<T> create() {
		NetIPV4Trie<T> trie = new NetIPV4Trie<T>();
		return trie;
	}

	public void insert(String entity, T value) throws PrefixCalculationException {
		NetTrieNode<String, T> tmpRoot = root;
		boolean isInserted = false;
		System.out.print("Entity : " + entity);
		entity = NetUtil.calculatePrefix(entity);
		System.out.println(" Binary Prefix: " + entity);
		String partEntity = entity;
		while (!isInserted) {
			if (partEntity.charAt(0) == '0') {
				if (tmpRoot.getLeftChild() == null) {
					// this is going to be first entry
					NetTrieNode<String, T> node = NetTrieNode.create(partEntity, value);
					tmpRoot.setLeftChild(node);
					isInserted = true;
				} else {
					tmpRoot = tmpRoot.getLeftChild();
					if (tmpRoot.getKey().equals(partEntity)) {
						// whole partEntity is already present.
						isInserted = true;
					} else {
						partEntity = createBranch(tmpRoot, partEntity, value);
						if (partEntity.length() == 0) {
							// item already present
							isInserted = true;
						}
					}
				}
				// entry will be made on left side.
			} else {
				// entry will be made on right side.
				if (tmpRoot.getRightChild() == null) {
					NetTrieNode<String, T> node = NetTrieNode.create(partEntity, value);
					tmpRoot.setRightChild(node);
					isInserted = true;
				} else {
					// move to next right
					tmpRoot = tmpRoot.getRightChild();
					if (tmpRoot.getKey().equals(partEntity)) {
						// whole partEntity is already present.
						isInserted = true;
					} else {
						partEntity = createBranch(tmpRoot, partEntity, value);
						if (partEntity.length() == 0) {
							// item already present
							isInserted = true;
						}
					}
				}
			}
		}

	}

	/**
	 * This method will check whether any new branch is need to be created or not.
	 * The new branch could be an intermediate branch to make two leaf entry or to
	 * make a parent entry of an exising IP.
	 * 
	 * @param tmpRoot
	 * @param partEntity
	 * @param value
	 * @return
	 */
	private String createBranch(NetTrieNode<String, T> tmpRoot, String partEntity, T value) {
		String tmpEntity = tmpRoot.getKey();

		int index = StringUtil.findCommonPrefix(partEntity, tmpEntity);
		int start = index + 1;
		if (start == partEntity.length()) {
			// this is parent of the exisitng node.
			// so inject a node here down tempRoot and move existing left and right node to
			// newly created node
			tmpEntity = tmpEntity.substring(start);
			NetTrieNode<String, T> node = NetTrieNode.create(tmpEntity, tmpRoot.getValue());
			node.setLeftChild(tmpRoot.getLeftChild());
			node.setRightChild(tmpRoot.getRightChild());

			tmpRoot.setKey(partEntity);
			tmpRoot.setValue(value);
			if (tmpEntity.charAt(0) == '0')
				tmpRoot.setLeftChild(node);
			else
				tmpRoot.setRightChild(node);
			// once parent is inserted means there is nothing to set any more. so set
			// partEntity as empty.
			partEntity = "";
		} else {
			// upadet partEntity with the part which is not common with exising key string
			// in tmpRoot
			partEntity = partEntity.substring(start);
			// we need to move to next element of common part
			// break here. branching is required at this point.
			if (start == tmpEntity.length()) {
				// tmpEntity ends here. So partEntity is going to be child.

			} else {
				// create a new node with rest of part
				T existingValue = tmpRoot.getValue();
				NetTrieNode<String, T> existingLeft = tmpRoot.getLeftChild();
				NetTrieNode<String, T> existingRight = tmpRoot.getRightChild();
				String tmpCommonPart = tmpEntity.substring(0, start);
				tmpRoot.setKey(tmpCommonPart);
				tmpRoot.setValue(null);
				String tmpRestPart = tmpEntity.substring(start);
				// creating node with rest part and setting it to exsisting tmpRoot.
				NetTrieNode<String, T> node = NetTrieNode.create(tmpRestPart, existingValue);
				node.setLeftChild(existingLeft);
				node.setRightChild(existingRight);
				// reset left and right child of tmpRoot as we are making branch here. so left
				// and right child should move down to branch
				tmpRoot.setLeftChild(null);
				tmpRoot.setRightChild(null);

				if (tmpRestPart.charAt(0) == '0')
					tmpRoot.setLeftChild(node);
				else
					tmpRoot.setRightChild(node);
			}
		}
		return partEntity;
	}

	/**
	 * This method will build childParent map for all the entry in Trie.
	 * 
	 * @return
	 */
	public Map<T, T> buildChildParentsMap() {
		Map<T, T> childParentMap = new LinkedHashMap<T, T>();
		return childParentMap;
	}

	public Map<String, T> findParentsWithVal(String entity) {
		Map<String, T> parents = new LinkedHashMap<String, T>();
		return parents;
	}

	public List<T> findParents(String entity, boolean inclusive) throws PrefixCalculationException {
		List<T> parents = new ArrayList<T>();
		NetTrieNode<String, T> tmpRoot = root;
		String binaryIPString = NetUtil.calculatePrefix(entity);
		System.out.println("Searching for entity: " + entity + " Binary Prefix: " + binaryIPString);
		boolean doBreak = false;
		// partInfo contains the part of binaryIPString which is yet to be searched
		String partInfo = binaryIPString;
		while (!doBreak) {
			if (partInfo.charAt(0) == '0') {
				// go ot left;
				tmpRoot = tmpRoot.getLeftChild();
				if (tmpRoot == null) {
					// we have reached end. So we do not have any more node to check
					doBreak = true;
					continue;
				}
				// check if tmpRoot is an legit node. if so make an entry in parents
				String tmpEntity = tmpRoot.getKey();
				if (partInfo.length() == tmpEntity.length()) {
					// their length are same, so two of either will match or do not match. Also this
					// is the last node after which no part of partInfo will be left to be matched
					// further.
					// in either case, the tmpEntity will not be parent of partInfo. if inclusive
					// add entity else break the flow.

					if (inclusive && partInfo.equals(tmpEntity))
						parents.add(tmpRoot.getValue());
					doBreak = true;
					continue;
				}

				int index = StringUtil.findCommonPrefix(partInfo, tmpEntity);
				int start = index + 1;
				// if partInfo complety inside tmpEntity then only tmpRoot can be parent of
				// searching entity.
				if (start == tmpEntity.length() && tmpRoot.getValue() != null) {
					parents.add(tmpRoot.getValue());
				}
				partInfo = partInfo.substring(start);
				if (partInfo.isEmpty()) {
					// we have reached to the end of requested entity.
					doBreak = true;
				}
			} else {
				tmpRoot = tmpRoot.getRightChild();
				if (tmpRoot == null) {
					// we have reached end. So we do not have any more node to check
					doBreak = true;
					continue;
				}
				// check if tmpRoot is an legit node. if so make an entry in parents

				String tmpEntity = tmpRoot.getKey();
				if (partInfo.length() == tmpEntity.length()) {
					// their length are same, so two of either will match or do not match. Also this
					// is the last node after which no part of partInfo will be left to be matched
					// further.
					// in either case, the tmpEntity will not be parent of partInfo. if inclusive
					// add entity else break the flow.

					if (inclusive && partInfo.equals(tmpEntity))
						parents.add(tmpRoot.getValue());
					doBreak = true;
					continue;
				}
				int index = StringUtil.findCommonPrefix(partInfo, tmpEntity);
				int start = index + 1;
				if (start == tmpEntity.length() && tmpRoot.getValue() != null) {
					parents.add(tmpRoot.getValue());
				}
				partInfo = partInfo.substring(start);
				if (partInfo.isEmpty()) {
					// we have reached to the end of requested entity.
					doBreak = true;
				}

			}
		}
		return parents;
	}
}
