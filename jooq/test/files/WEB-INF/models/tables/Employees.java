/**
 * This class is generated by jOOQ
 */
package models.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.3.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Employees extends org.jooq.impl.TableImpl<models.tables.records.EmployeesRecord> {

	private static final long serialVersionUID = -1584860745;

	/**
	 * The singleton instance of <code>PUBLIC.EMPLOYEES</code>
	 */
	public static final models.tables.Employees EMPLOYEES = new models.tables.Employees();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<models.tables.records.EmployeesRecord> getRecordType() {
		return models.tables.records.EmployeesRecord.class;
	}

	/**
	 * The column <code>PUBLIC.EMPLOYEES.ID</code>.
	 */
	public final org.jooq.TableField<models.tables.records.EmployeesRecord, java.lang.Integer> ID = createField("ID", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>PUBLIC.EMPLOYEES.FIRST_NAME</code>.
	 */
	public final org.jooq.TableField<models.tables.records.EmployeesRecord, java.lang.String> FIRST_NAME = createField("FIRST_NAME", org.jooq.impl.SQLDataType.VARCHAR.length(56), this, "");

	/**
	 * The column <code>PUBLIC.EMPLOYEES.LAST_NAME</code>.
	 */
	public final org.jooq.TableField<models.tables.records.EmployeesRecord, java.lang.String> LAST_NAME = createField("LAST_NAME", org.jooq.impl.SQLDataType.VARCHAR.length(56), this, "");

	/**
	 * Create a <code>PUBLIC.EMPLOYEES</code> table reference
	 */
	public Employees() {
		this("EMPLOYEES", null);
	}

	/**
	 * Create an aliased <code>PUBLIC.EMPLOYEES</code> table reference
	 */
	public Employees(java.lang.String alias) {
		this(alias, models.tables.Employees.EMPLOYEES);
	}

	private Employees(java.lang.String alias, org.jooq.Table<models.tables.records.EmployeesRecord> aliased) {
		this(alias, aliased, null);
	}

	private Employees(java.lang.String alias, org.jooq.Table<models.tables.records.EmployeesRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, models.Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<models.tables.records.EmployeesRecord, java.lang.Integer> getIdentity() {
		return models.Keys.IDENTITY_EMPLOYEES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<models.tables.records.EmployeesRecord> getPrimaryKey() {
		return models.Keys.CONSTRAINT_4;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<models.tables.records.EmployeesRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<models.tables.records.EmployeesRecord>>asList(models.Keys.CONSTRAINT_4);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public models.tables.Employees as(java.lang.String alias) {
		return new models.tables.Employees(alias, this);
	}

	/**
	 * Rename this table
	 */
	public models.tables.Employees rename(java.lang.String name) {
		return new models.tables.Employees(name, null);
	}
}
