package org.arijit.netipv4trie.core.trie;

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
		int start = 0;
		String partEntity = entity;
		while (!isInserted) {
			if (start == entity.length()) {
				// item is already present.
				isInserted = true;
				continue;
			}
//			String partEntity = entity.substring(start);
			if (partEntity.charAt(0) == '0') {
				if (tmpRoot.getLeftChild() == null) {
					// this is going to be first entry
					NetTrieNode<String, T> node = NetTrieNode.create(partEntity, value);
					tmpRoot.setLeftChild(node);
					isInserted = true;
				} else {
					tmpRoot = tmpRoot.getLeftChild();
					String tmpEntity = tmpRoot.getKey();

					int index = StringUtil.findCommonPrefix(partEntity, tmpEntity);
					start = index + 1;
					if(start==partEntity.length()) {
						//this is parent of the exisitng node.
						//so make a node here
						tmpEntity = tmpEntity.substring(start);
						NetTrieNode<String, T> node = NetTrieNode.create(tmpEntity, tmpRoot.getValue());
						node.setLeftChild(tmpRoot.getLeftChild());
						node.setRightChild(tmpRoot.getRightChild());
						
						tmpRoot.setKey(partEntity);
						tmpRoot.setValue(value);
						if(tmpEntity.charAt(0)=='0')
							tmpRoot.setLeftChild(node);
						else
							tmpRoot.setRightChild(node);
						isInserted = true;
						
					}
					else {
					// we need to move to next element of common part
					// break here. branching is required at this point.
					partEntity = entity.substring(start);
					
					if (start == tmpEntity.length()) {
						entity = partEntity;
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
					String tmpEntity = tmpRoot.getKey();

					int index = StringUtil.findCommonPrefix(partEntity, tmpEntity);
					start = index + 1;
					if(start==partEntity.length()) {
						//this is parent of the exisitng node.
						//so make a node here
						tmpEntity = tmpEntity.substring(start);
						NetTrieNode<String, T> node = NetTrieNode.create(tmpEntity, value);
						node.setLeftChild(tmpRoot.getLeftChild());
						node.setRightChild(tmpRoot.getRightChild());
						
						tmpRoot.setKey(partEntity);
						tmpRoot.setValue(value);
						if(tmpEntity.charAt(0)=='0')
							tmpRoot.setLeftChild(node);
						else
							tmpRoot.setRightChild(node);
						
					}
					else {
					partEntity = entity.substring(start);
					// we need to move to next element of common part
					// break here. branching is required at this point.
					if (start == tmpEntity.length()) {
						// tmpEntity ends here. So partEntity is going to be child.
						entity = partEntity;
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
				}
			}
		}
	}

	private void createBranch(NetTrieNode<String, T> tmpRoot, String partEntity, int start) {
		String tmpEntity = tmpRoot.getKey();
		int index = StringUtil.findCommonPrefix(partEntity, tmpEntity);
		start = index + 1;// we need to move to next element of common part
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
}
