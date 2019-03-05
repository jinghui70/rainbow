package rainbow.db.jdbc.object;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.util.ioc.InitializingBean;
import rainbow.db.dao.Dao;
import rainbow.db.jdbc.DataAccessException;
import rainbow.db.jdbc.SqlParameter;

/**
 * An "RDBMS operation" is a multi-threaded, reusable object representing a
 * query, update, or stored procedure call. An RDBMS operation is <b>not</b> a
 * command, as a command is not reusable. However, execute methods may take
 * commands as arguments. Subclasses should be JavaBeans, allowing easy
 * configuration.
 * 
 * <p>
 * Subclasses should set SQL and add parameters before invoking the
 * {@link #compile()} method. The order in which parameters are added is
 * significant. The appropriate <code>execute</code> or <code>update</code>
 * method can then be invoked.
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see StoredProcedure
 */
public abstract class RdbmsOperation implements InitializingBean {

    /** Logger available to subclasses */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Dao dao;

    private int resultSetType = ResultSet.TYPE_FORWARD_ONLY;

    private boolean updatableResults = false;

    private String sql;

    private final List<SqlParameter> declaredParameters = new LinkedList<SqlParameter>();

    /**
     * Has this operation been compiled? Compilation means at least checking
     * that a DataSource and sql have been provided, but subclasses may also
     * implement their own custom validation.
     */
    private boolean compiled;

    /**
     * Set whether to use statements that return a specific type of ResultSet.
     * 
     * @param resultSetType
     *            the ResultSet type
     * @see java.sql.ResultSet#TYPE_FORWARD_ONLY
     * @see java.sql.ResultSet#TYPE_SCROLL_INSENSITIVE
     * @see java.sql.ResultSet#TYPE_SCROLL_SENSITIVE
     * @see java.sql.Connection#prepareStatement(String, int, int)
     */
    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }

    /**
     * Return whether statements will return a specific type of ResultSet.
     */
    public int getResultSetType() {
        return this.resultSetType;
    }

    /**
     * Set whether to use statements that are capable of returning updatable
     * ResultSets.
     * 
     * @see java.sql.Connection#prepareStatement(String, int, int)
     */
    public void setUpdatableResults(boolean updatableResults) {
        if (isCompiled()) {
            throw new DataAccessException("The updateableResults flag must be set before the operation is compiled");
        }
        this.updatableResults = updatableResults;
    }

    /**
     * Return whether statements will return updatable ResultSets.
     */
    public boolean isUpdatableResults() {
        return this.updatableResults;
    }

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    /**
     * Set the SQL executed by this operation.
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Subclasses can override this to supply dynamic SQL if they wish, but SQL
     * is normally set by calling the setSql() method or in a subclass
     * constructor.
     */
    public String getSql() {
        return this.sql;
    }

    /**
     * Add anonymous parameters, specifying only their SQL types as defined in
     * the <code>java.sql.Types</code> class.
     * <p>
     * Parameter ordering is significant. This method is an alternative to the
     * {@link #declareParameter} method, which should normally be preferred.
     * 
     * @param types
     *            array of SQL types as defined in the
     *            <code>java.sql.Types</code> class
     * @throws DataAccessException
     *             if the operation is already compiled
     */
    public void setTypes(int[] types) throws DataAccessException {
        if (isCompiled()) {
            throw new DataAccessException("Cannot add parameters once query is compiled");
        }
        if (types != null) {
            for (int type : types) {
                declareParameter(new SqlParameter(type));
            }
        }
    }

    /**
     * Declare a parameter for this operation.
     * <p>
     * The order in which this method is called is significant when using
     * positional parameters. It is not significant when using named parameters
     * with named SqlParameter objects here; it remains significant when using
     * named parameters in combination with unnamed SqlParameter objects here.
     * 
     * @param param
     *            the SqlParameter to add. This will specify SQL type and
     *            (optionally) the parameter's name. Note that you typically use
     *            the {@link SqlParameter} class itself here, not any of its
     *            subclasses.
     * @throws DataAccessException
     *             if the operation is already compiled, and hence cannot be
     *             configured further
     */
    public void declareParameter(SqlParameter param) throws DataAccessException {
        if (isCompiled()) {
            throw new DataAccessException("Cannot add parameters once the query is compiled");
        }
        this.declaredParameters.add(param);
    }

    /**
     * Add one or more declared parameters. Used for configuring this operation
     * when used in a bean factory. Each parameter will specify SQL type and
     * (optionally) the parameter's name.
     * 
     * @param parameters
     *            Array containing the declared {@link SqlParameter} objects
     * @see #declaredParameters
     */
    public void setParameters(SqlParameter[] parameters) {
        if (isCompiled()) {
            throw new DataAccessException("Cannot add parameters once the query is compiled");
        }
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] != null) {
                this.declaredParameters.add(parameters[i]);
            } else {
                throw new DataAccessException("Cannot add parameter at index " + i + " from "
                        + Arrays.asList(parameters) + " since it is 'null'");
            }
        }
    }

    /**
     * Return a list of the declared {@link SqlParameter} objects.
     */
    protected List<SqlParameter> getDeclaredParameters() {
        return this.declaredParameters;
    }

    /**
     * Ensures compilation if used in a bean factory.
     */
    public void afterPropertiesSet() {
        compile();
    }

    /**
     * Compile this query. Ignores subsequent attempts to compile.
     * 
     * @throws DataAccessException
     *             if the object hasn't been correctly initialized, for example
     *             if no DataSource has been provided
     */
    public final void compile() throws DataAccessException {
        if (!isCompiled()) {
            if (getSql() == null) {
                throw new DataAccessException("Property 'sql' is required");
            }

            compileInternal();
            this.compiled = true;

            if (logger.isDebugEnabled()) {
                logger.debug("RdbmsOperation with SQL [" + getSql() + "] compiled");
            }
        }
    }

    /**
     * Is this operation "compiled"? Compilation, as in JDO, means that the
     * operation is fully configured, and ready to use. The exact meaning of
     * compilation will vary between subclasses.
     * 
     * @return whether this operation is compiled, and ready to use.
     */
    public boolean isCompiled() {
        return this.compiled;
    }

    /**
     * Check whether this operation has been compiled already; lazily compile it
     * if not already compiled.
     * <p>
     * Automatically called by <code>validateParameters</code>.
     * 
     * @see #validateParameters
     */
    protected void checkCompiled() {
        if (!isCompiled()) {
            logger.debug("SQL operation not compiled before execution - invoking compile");
            compile();
        }
    }

    /**
     * Validate the parameters passed to an execute method based on declared
     * parameters. Subclasses should invoke this method before every
     * <code>executeQuery()</code> or <code>update()</code> method.
     * 
     * @param parameters
     *            parameters supplied (may be <code>null</code>)
     * @throws DataAccessException
     *             if the parameters are invalid
     */
    protected void validateParameters(Object[] parameters) throws DataAccessException {
        checkCompiled();
        int declaredInParameters = 0;
        for (SqlParameter param : this.declaredParameters) {
            if (param.isInputValueProvided()) {
                if (!supportsLobParameters() && (param.getSqlType() == Types.BLOB || param.getSqlType() == Types.CLOB)) {
                    throw new DataAccessException("BLOB or CLOB parameters are not allowed for this kind of operation");
                }
                declaredInParameters++;
            }
        }
        validateParameterCount((parameters != null ? parameters.length : 0), declaredInParameters);
    }

    /**
     * Validate the named parameters passed to an execute method based on
     * declared parameters. Subclasses should invoke this method before every
     * <code>executeQuery()</code> or <code>update()</code> method.
     * 
     * @param parameters
     *            parameter Map supplied. May be <code>null</code>.
     * @throws DataAccessException
     *             if the parameters are invalid
     */
    protected void validateNamedParameters(Map<String, ?> parameters) throws DataAccessException {
        checkCompiled();
        if (parameters == null)
            parameters = Collections.emptyMap();
        int declaredInParameters = 0;
        for (SqlParameter param : this.declaredParameters) {
            if (param.isInputValueProvided()) {
                if (!supportsLobParameters() && (param.getSqlType() == Types.BLOB || param.getSqlType() == Types.CLOB)) {
                    throw new DataAccessException("BLOB or CLOB parameters are not allowed for this kind of operation");
                }
                if (param.getName() != null && !parameters.containsKey(param.getName())) {
                    throw new DataAccessException("The parameter named '" + param.getName()
                            + "' was not among the parameters supplied: " + parameters.keySet());
                }
                declaredInParameters++;
            }
        }
        validateParameterCount(parameters.size(), declaredInParameters);
    }

    /**
     * Validate the given parameter count against the given declared parameters.
     * 
     * @param suppliedParamCount
     *            the number of actual parameters given
     * @param declaredInParamCount
     *            the number of input parameters declared
     */
    private void validateParameterCount(int suppliedParamCount, int declaredInParamCount) {
        if (suppliedParamCount < declaredInParamCount) {
            throw new DataAccessException(suppliedParamCount + " parameters were supplied, but " + declaredInParamCount
                    + " in parameters were declared in class [" + getClass().getName() + "]");
        }
        if (suppliedParamCount > this.declaredParameters.size() && !allowsUnusedParameters()) {
            throw new DataAccessException(suppliedParamCount + " parameters were supplied, but " + declaredInParamCount
                    + " parameters were declared in class [" + getClass().getName() + "]");
        }
    }

    /**
     * Subclasses must implement this template method to perform their own
     * compilation. Invoked after this base class's compilation is complete.
     * <p>
     * Subclasses can assume that SQL and a DataSource have been supplied.
     * 
     * @throws DataAccessException
     *             if the subclass hasn't been properly configured
     */
    protected abstract void compileInternal() throws DataAccessException;

    /**
     * Return whether BLOB/CLOB parameters are supported for this kind of
     * operation.
     * <p>
     * The default is <code>true</code>.
     */
    protected boolean supportsLobParameters() {
        return true;
    }

    /**
     * Return whether this operation accepts additional parameters that are
     * given but not actually used. Applies in particular to parameter Maps.
     * <p>
     * The default is <code>false</code>.
     * 
     * @see StoredProcedure
     */
    protected boolean allowsUnusedParameters() {
        return false;
    }

}
