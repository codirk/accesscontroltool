/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.cq.tools.actool.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;

public class QueryHelper {

	/**
	 * Method that returns a set containing all rep:policy nodes from repository
	 * excluding those contained in paths which are excluded from search
	 * 
	 * @param session
	 * @param excludePaths
	 *            paths which are excluded from search
	 * @return all rep:policy nodes delivered by query
	 */
	public static Set<Node> getRepPolicyNodes(final Session session,
			final List<String> excludePaths) {
		NodeIterator nodeIt = null;
		try {
			nodeIt = session.getRootNode().getNodes();
		} catch (RepositoryException e) {
			AcHelper.LOG.error("Exception: {}", e);
		}

		Set<String> paths = new TreeSet<String>();
		while (nodeIt.hasNext()) {
			String currentPath = null;
			Node currentNode = nodeIt.nextNode();
			try {
				currentPath = currentNode.getPath();
			} catch (RepositoryException e) {
				AcHelper.LOG.error("Exception: {}", e);
			}

			try {
				if (!currentNode.hasProperty("rep:AuthorizableFolder")) {
					if (!excludePaths.contains(currentPath)) {
						paths.add(currentPath);
					}
				}
			} catch (RepositoryException e) {
				AcHelper.LOG.error("Exception: {}", e);
			}
		}
		Set<Node> nodes = new LinkedHashSet<Node>();
		try {
			// get the rep:policy node of "/", if existing
			if (session.nodeExists("/rep:policy")) {
				nodes.add(session.getNode("/rep:policy"));
			}
			// get the rep:policy node of "/home", if existing
			if (session.nodeExists("/home/rep:policy")) {
				nodes.add(session.getNode("/home/rep:policy"));
			}
			for (String path : paths) {
				String query = "/jcr:root" + path
						+ "//*[(@jcr:primaryType = 'rep:ACL') ]";
				nodes.addAll(QueryHelper.getNodes(session, query));
			}
		} catch (InvalidQueryException e) {
			AcHelper.LOG.error("InvalidQueryException: {}", e);
		} catch (RepositoryException e) {
			AcHelper.LOG.error("RepositoryException: {}", e);
		}
		return nodes;
	}

	public static Set<Node> getNodes(final Session session,
			final String xpathQuery) throws InvalidQueryException,
			RepositoryException {
		Set<Node> nodes = new HashSet<Node>();

		Query query = session.getWorkspace().getQueryManager()
				.createQuery(xpathQuery, Query.XPATH);
		QueryResult queryResult = query.execute();
		NodeIterator nit = queryResult.getNodes();
		List<String> paths = new ArrayList<String>();

		while (nit.hasNext()) {
			// get the next rep:policy node
			Node node = nit.nextNode();
			// AcHelper.LOG.debug("adding node: {} to node set", node.getPath());
			paths.add(node.getPath());
			nodes.add(node);
		}
		return nodes;
	}

	public static Set<String> getUsersFromHome(final Session session)
			throws RepositoryException {

		Set<String> users = new TreeSet<String>();
		String queryStringUsers = "//*[(@jcr:primaryType = 'rep:User')]";
		Query queryUsers = session.getWorkspace().getQueryManager()
				.createQuery(queryStringUsers, Query.XPATH);
		QueryResult queryResultUsers = queryUsers.execute();
		NodeIterator nitUsers = queryResultUsers.getNodes();

		while (nitUsers.hasNext()) {
			Node node = nitUsers.nextNode();
			String tmp = node.getProperty("rep:principalName").getString();
			users.add(tmp);
		}
		return users;
	}

	public static Set<String> getGroupsFromHome(final Session session)
			throws InvalidQueryException, RepositoryException {
		Set<String> groups = new TreeSet<String>();
		String queryStringGroups = "//*[(@jcr:primaryType = 'rep:Group')]";
		Query queryGroups = session.getWorkspace().getQueryManager()
				.createQuery(queryStringGroups, Query.XPATH);
		QueryResult queryResultGroups = queryGroups.execute();
		NodeIterator nitGroups = queryResultGroups.getNodes();

		while (nitGroups.hasNext()) {
			Node node = nitGroups.nextNode();
			String tmp = node.getProperty("rep:principalName").getString();
			groups.add(tmp);
		}
		return groups;
	}

	public static Set<AclBean> getAuthorizablesAcls(final Session session,
			final Set<String> authorizableIds) throws InvalidQueryException,
			RepositoryException {
		Set<Node> nodeSet = new LinkedHashSet<Node>();

		Iterator<String> authorizablesIdIterator = authorizableIds.iterator();
		
		while(authorizablesIdIterator.hasNext()){
			StringBuilder queryStringBuilder = new StringBuilder();
			queryStringBuilder.append("/jcr:root//*[(@jcr:primaryType = 'rep:GrantACE' or @jcr:primaryType = 'rep:DenyACE' ) and (");

			queryStringBuilder.append(getAuthorizablesQueryStringBuilder(authorizablesIdIterator, 100));
			queryStringBuilder.append(")]");

			nodeSet.addAll(getNodes(session, queryStringBuilder.toString()));
		}

		return buildAclBeansFromNodeSet(session, nodeSet);
	}

	private static Set<AclBean> buildAclBeansFromNodeSet(final Session session,
			Set<Node> nodeSet) throws UnsupportedRepositoryOperationException,
			RepositoryException, PathNotFoundException, AccessDeniedException,
			ItemNotFoundException {
		AccessControlManager aMgr = session.getAccessControlManager();
		AccessControlList acl;
		Set<AclBean> aclSet = new LinkedHashSet<AclBean>();
		for (Node node : nodeSet) {
			acl = (AccessControlList) aMgr.getPolicies(node.getParent()
					.getParent().getPath())[0];
			AclBean aclBean = new AclBean();
			aclBean.setParentPath(node.getParent().getParent().getPath());
			aclBean.setAcl((JackrabbitAccessControlList) acl);
			aclBean.setJcrPath(node.getParent().getPath());
			aclSet.add(aclBean);
		}
		return aclSet;
	}

	private static StringBuilder getAuthorizablesQueryStringBuilder(final Iterator<String> authorizablesIdIterator, final int authorizbalesLimitPerQuery) {
		int authorizableCounter = 0;
		StringBuilder querySb = new StringBuilder();

		if(!authorizablesIdIterator.hasNext()){
			return querySb;
		}
		while (true) {
			querySb.append("@rep:principalName = '" + authorizablesIdIterator.next() + "'");
			authorizableCounter++;
			if (authorizableCounter < authorizbalesLimitPerQuery && authorizablesIdIterator.hasNext()) {
				querySb.append(" or ");
			}else{
				return querySb;
			}
		}
	}

	public static Set<AclBean> getAuthorizablesAcls(final Session session,
			final String authorizableId) throws InvalidQueryException,
			RepositoryException {
		Set<Node> nodeSet = new LinkedHashSet<Node>();
		String query = "/jcr:root//*[(@jcr:primaryType = 'rep:GrantACE' or @jcr:primaryType = 'rep:DenyACE' ) and (@rep:principalName = '"
				+ authorizableId + "')]";
		nodeSet.addAll(getNodes(session, query));

		Set<AclBean> aclSet = buildAclBeansFromNodeSet(session, nodeSet);
		return aclSet;
	}

}
