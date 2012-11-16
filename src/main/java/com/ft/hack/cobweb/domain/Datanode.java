package com.ft.hack.cobweb.domain;

import java.util.ArrayList;
import java.util.List;

public class Datanode {
 
    Long nodeId;
    
    String name;
    String type;
    List<Datanode> associations = new ArrayList<Datanode>();
    
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<Datanode> getAssociations() {
		return associations;
	}
	public void setAssociations(List<Datanode> associations) {
		this.associations = associations;
	}
	public void addAssociation(Datanode node) {
		associations.add(node);
	}
}
