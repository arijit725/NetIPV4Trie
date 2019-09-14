package org.arijit.netipv4trie.core.trie;

import java.io.Serializable;

/**
 * NetTrieNode will have a key value pair.
 * Trie will be build based on Key K
 * Value V will contain user specific data if any present.
 * 
 * @author ARIJIT
 *
 * @param <K>
 * @param <V>
 */
public class NetTrieNode<K extends Object, V extends Object> implements Serializable {

	/*
	 * Based on key tire will be build. This case it will be birnary String prefix 
	 */
	private K key;
	/*
	 * User defined value will be set here
	 */
	private V value;
	private boolean isEnd;
	/*any string starting with 0 will go to left*/
	NetTrieNode<K, V> leftChild;
	/*any String starting with 1 will go to right*/
	NetTrieNode<K, V> rightChild;
	
	private NetTrieNode(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	public static <K,V> NetTrieNode<K, V> create(K key, V value) {
		NetTrieNode<K, V> node = new NetTrieNode<K, V>(key, value);
		return node;
	}
	public K getKey() {
		return key;
	}
	public NetTrieNode<K, V> getLeftChild() {
		return leftChild;
	}
	public NetTrieNode<K, V> getRightChild() {
		return rightChild;
	}
	public V getValue() {
		return value;
	}
	public void setLeftChild(NetTrieNode<K, V> leftChild) {
		this.leftChild = leftChild;
	}
	public void setRightChild(NetTrieNode<K, V> rightChild) {
		this.rightChild = rightChild;
	}
	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
	public void setKey(K key) {
		this.key = key;
	}
	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "NetTrieNode [key=" + key + ", value=" + value + "]";
	}
	
	
}
