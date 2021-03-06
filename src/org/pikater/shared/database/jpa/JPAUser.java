package org.pikater.shared.database.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.pikater.shared.database.jpa.daos.DAOs;
import org.pikater.shared.database.jpa.status.JPAUserStatus;

/**
 * Class {@link JPAUser} represents record about user of system.
 */
@Entity
@Table(name = "_User", indexes = { @Index(columnList = "login"), @Index(columnList = "email"), @Index(columnList = "priorityMax"), @Index(columnList = "status"), @Index(columnList = "created") })
@NamedQueries({ @NamedQuery(name = "User.getAll", query = "select u from JPAUser u"), @NamedQuery(name = "User.getAll.count", query = "select count(u) from JPAUser u"),
		@NamedQuery(name = "User.getByStatus", query = "select u from JPAUser u where u.status=:status"),
		@NamedQuery(name = "User.getByLogin", query = "select u from JPAUser u where u.login=:login"), @NamedQuery(name = "User.getByRole", query = "select u from JPAUser u where u.role=:role") })
public class JPAUser extends JPAAbstractEntity {

	@Column(unique = true)
	private String login;
	@Column(nullable = false)
	private String password;
	private int priorityMax;
	private String email;
	@ManyToOne
	private JPARole role;
	@Enumerated(EnumType.STRING)
	private JPAUserStatus status;
	@OneToMany
	private List<JPABatch> batches;
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;

	/** Constructor for JPA Compatibility. */
	protected JPAUser() {
	}

	/**
	 * Complete constructor - for internal use only.
	 * @param login The login of the user.
	 * @param password The password of the user.
	 * @param role The role of the user.
	 * @param email The e-mail address of the user.
	 * @param maxPriority The maximal priority of user's tasks.
	 * @param status The user's account status.
	 */
	protected JPAUser(String login, String password, String email, JPARole role, int maxPriority, JPAUserStatus status) {
		setLogin(login);
		setPassword(password);
		setPriorityMax(maxPriority);
		setEmail(email);
		setRole(role);
		setStatus(status);
		this.created = new Date();
	}

	/**
	 * Creates a new user account. Notes:
	 * - not an admin,
	 * - lowest priority possible (administrator should raise),
	 * - passive status (administrator acceptance is needed).  
	 */
	public static JPAUser createAccountForGUI(String login, String password, String email) {
		return new JPAUser(login, password, email, DAOs.roleDAO.getByPikaterRole(PikaterRole.USER), 0, JPAUserStatus.SUSPENDED);
	}

	/**
	 * Creates a new user account. Notes:
	 * - admin account,
	 * - maximum priority,
	 * - active status (no administrator acceptance is needed). 
	 */
	public static JPAUser createAccountForDBInit(String login, String password, String email, JPARole role) {
		return new JPAUser(login, password, email, DAOs.roleDAO.getByPikaterRole(PikaterRole.ADMIN), 9, JPAUserStatus.ACTIVE);
	}

	/**
	 * Used in web for offline development when the database is not reachable or usable for some reason.
	 */
	public static JPAUser getDummy() {
		return new JPAUser("dummy_user", "dummy_password", "dummy_user@mail.com", null, 9, JPAUserStatus.ACTIVE);
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getPriorityMax() { // changed return type to allow comparisons without boxing
		return priorityMax;
	}

	public void setPriorityMax(int priorityMax) {
		if ((priorityMax >= 0) && priorityMax < 10) {
			this.priorityMax = priorityMax;
		} else {
			throw new IllegalArgumentException("Only values from 0 to 9 are allowed. Received: " + priorityMax);
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public JPARole getRole() {
		return role;
	}

	public void setRole(JPARole role) {
		this.role = role;
	}

	public JPAUserStatus getStatus() {
		return status;
	}

	public void setStatus(JPAUserStatus status) {
		this.status = status;
	}

	public List<JPABatch> getBatches() {
		return batches;
	}

	public void setBatches(List<JPABatch> batches) {
		this.batches = batches;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public boolean isAdmin() {
		return role != null ? role.isAdmin() : false;
	}

	public boolean isUser() {
		return role != null ? role.isUser() : true;
	}

	public void setAdmin(boolean admin) {
		if (admin) // promote
		{
			if (!isAdmin()) {
				setRole(DAOs.roleDAO.getByPikaterRole(PikaterRole.ADMIN));
			}
		} else // downgrade
		{
			if (isAdmin()) {
				setRole(DAOs.roleDAO.getByPikaterRole(PikaterRole.USER));
			}
		}
	}

	public boolean hasPrivilege(PikaterPriviledge priviledge) {
		return role != null ? role.hasPriviledge(priviledge) : false;
	}

	@Transient
	public static final String EntityName = "User";

	@Override
	public void updateValues(JPAAbstractEntity newValues) throws Exception {
		JPAUser updateValues = (JPAUser) newValues;
		this.batches = updateValues.getBatches();
		this.email = updateValues.getEmail();
		this.login = updateValues.getLogin();
		this.password = updateValues.getPassword();
		this.priorityMax = updateValues.getPriorityMax();
		this.role = updateValues.getRole();
		this.status = updateValues.getStatus();
		this.created = updateValues.getCreated();
		this.lastLogin = updateValues.getLastLogin();
	}
}
