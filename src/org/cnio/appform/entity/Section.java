package org.cnio.appform.entity;

import java.util.List;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.OneToMany;
import static javax.persistence.CascadeType.ALL;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name="section")
public class Section implements Serializable, Cloneable {
	@Id
	@Column(name="idsection")
	@SequenceGenerator(name="SecGenerator", sequenceName = "section_idsection_seq")
	@GeneratedValue(strategy=SEQUENCE, generator = "SecGenerator")
	private Integer id;
	@Column(name="name")
	private String name;
	@Column(name="description")
	private String description;
	@Column(name="section_order")
	private Integer sectionOrder;
	
	@OneToMany(mappedBy="parentSec", cascade = ALL)
	private List<AbstractItem> items;
	
	@ManyToOne(targetEntity=Interview.class)
	@JoinColumn(name="codinterview")
	@ForeignKey(name="fk_section_is_formed_interview")
	private Interview parentInt;
	
	
	public Section () {
		items = new ArrayList<AbstractItem>();
	}

	public Section (String name, String description) {
		this ();
		
		sectionOrder = 1;
		this.name = name;
		this.description = description;
	}
/*	
	public Section (String name, String description, AbstractItem anItem) {
		this ();
		
		sectionOrder = 1;
		this.name = name;
		this.description = description;
		items.add(anItem);		
	}
	*/
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	
	public Integer getSectionOrder() {
		return sectionOrder;
	}
	public void setSectionOrder(Integer sectionOrder) {
		this.sectionOrder = sectionOrder;
	}

	public List<AbstractItem> getItems() {
		return items;
	}

	public void setItems(List<AbstractItem> items) {
		this.items = items;
	}

	
	public void setInterview (Interview intr) {
		this.parentInt = intr;
		
		intr.getSections().add(this);
	}
	
	
	public Interview getParentIntr () {
		return this.parentInt;
	}
	
	public String toString () {
		return "Section => id: "+id+"; "+name+". "+items.size()+" items";
	}
	
	
	public Object clone () throws CloneNotSupportedException {
		
		Section section = new Section (this.name, this.description);
		section.setId(null);
//		section.setItems(null);
System.out.println();
System.out.println("Section: "+this.name);
		for (Iterator<AbstractItem> iter = items.iterator(); iter.hasNext();) {
			AbstractItem item = iter.next();
			AbstractItem newOne;
			
// we dont recreate the containees because that is already done when creating
// the containers
			if (item.getContainer() == null) {
				if (item instanceof Question)
					newOne = (AbstractItem)((Question)item).clone();
				
				else
					newOne = (AbstractItem)((Text)item).clone();
				
// set parent section for newOne and its children if so				
				newOne.setParentSec(section);
				
				System.out.print("=");
				for (Iterator<AbstractItem> it=newOne.getContainees().iterator();
						 it.hasNext();) {
					AbstractItem child = it.next();
					child.setParentSec(section);
				}
			} // if no container
			
		}
		section.setSectionOrder(this.sectionOrder);
		return section;
	}
}
