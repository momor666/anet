package mil.dds.anet.beans.lists;

import java.util.List;

import mil.dds.anet.beans.Organization;
import mil.dds.anet.beans.Person;
import mil.dds.anet.beans.Position;
import mil.dds.anet.beans.Report;
import mil.dds.anet.graphql.IGraphQLBean;

public abstract class AbstractAnetBeanList<T extends IGraphQLBean> implements IGraphQLBean {

	List<T> list;
	Integer pageNum;
	Integer pageSize;
	
	public AbstractAnetBeanList() { /*Serialization Constructor */ } 
	
	public AbstractAnetBeanList(List<T> list) { 
		this(null, null, list);
	}
	
	public AbstractAnetBeanList(Integer pageNum, Integer pageSize, List<T> list) { 
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.list = list;
	}
	
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	public Integer getPageNum() {
		return pageNum;
	}
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	/* Because of Java Type Erasure we actually have to have the 
	 * getList() method live on every implementation of the List
	 */

	public static class ReportList extends AbstractAnetBeanList<Report> {
		public ReportList() { /*Serialization Constructor */ } 
		public ReportList(Integer pageNum, Integer pageSize, List<Report> list) {
			super(pageNum, pageSize, list);
		}
		public ReportList(List<Report> list) { 
			super(list);
		}
		public List<Report> getList() { return list; } 
	}
	
	public static class PersonList extends AbstractAnetBeanList<Person> {
		public PersonList() { /*Serialization Constructor */ } 
		public PersonList(Integer pageNum, Integer pageSize, List<Person> list) {
			super(pageNum, pageSize, list);
		}
		public PersonList(List<Person> list) { 
			super(list);
		}
		public List<Person> getList() { return list; } 
	};
	
	public static class OrganizationList extends AbstractAnetBeanList<Organization> {
		public OrganizationList() { /*Serialization Constructor */ } 
		public OrganizationList(Integer pageNum, Integer pageSize, List<Organization> list) {
			super(pageNum, pageSize, list);
		}
		public OrganizationList(List<Organization> list) { 
			super(list);
		}
		public List<Organization> getList() { return list; } 
	};
	
	public static class PositionList extends AbstractAnetBeanList<Position> {
		public PositionList() { /*Serialization Constructor */ } 
		public PositionList(Integer pageNum, Integer pageSize, List<Position> list) {
			super(pageNum, pageSize, list);
		}
		public PositionList(List<Position> list) { 
			super(list);
		}
		public List<Position> getList() { return list; } 
	};
	
}
