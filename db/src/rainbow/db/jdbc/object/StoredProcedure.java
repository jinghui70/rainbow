package rainbow.db.jdbc.object;

import java.util.HashMap;
import java.util.Map;

import rainbow.db.dao.Dao;
import rainbow.db.jdbc.DataAccessException;
import rainbow.db.jdbc.SqlParameter;

/**
 * Superclass for object abstractions of RDBMS stored procedures. This class is
 * abstract and it is intended that subclasses will provide a typed method for
 * invocation that delegates to the supplied {@link #execute} method.
 * 
 * <p>
 * The inherited <code>sql</code> property is the name of the stored procedure
 * in the RDBMS. Note that JDBC 3.0 introduces named parameters, although the
 * other features provided by this class are still necessary in JDBC 3.0.
 * 
 * @author Rod Johnson
 * @author Thomas Risberg
 * @see #setSql
 */
public abstract class StoredProcedure extends SqlCall {

    /**
     * Allow use as a bean.
     */
    protected StoredProcedure() {
    }

    /**
     * Create a new object wrapper for a stored procedure.
     * 
     * @param dao
     *            Dao to use throughout the lifetime of this object to
     *            obtain connections
     * @param name
     *            name of the stored procedure in the database
     */
    protected StoredProcedure(Dao dao, String name) {
        setDao(dao);
        setSql(name);
    }

    /**
     * Declare a parameter. Overridden method. Parameters declared as
     * <code>SqlParameter</code> and <code>SqlInOutParameter</code> will always
     * be used to provide input values. In addition to this any parameter
     * declared as <code>SqlOutParameter</code> where an non-null input value is
     * provided will also be used as an input paraneter. <b>Note: Calls to
     * declareParameter must be made in the same order as they appear in the
     * database's stored procedure parameter list.</b> Names are purely used to
     * help mapping.
     * 
     * @param param
     *            parameter object
     */
    @Override
    public void declareParameter(SqlParameter param) throws DataAccessException {
        if (param.getName() == null) {
            throw new DataAccessException("Parameters to stored procedures must have names as well as types");
        }
        super.declareParameter(param);
    }

    /**
     * Execute the stored procedure with the provided parameter values. This is
     * a convenience method where the order of the passed in parameter values
     * must match the order that the parameters where declared in.
     * 
     * @param inParams
     *            variable number of input parameters. Output parameters should
     *            not be included in this map. It is legal for values to be
     *            <code>null</code>, and this will produce the correct behavior
     *            using a NULL argument to the stored procedure.
     * @return map of output params, keyed by name as in parameter declarations.
     *         Output parameters will appear here, with their values after the
     *         stored procedure has been called.
     */
    public Map<String, Object> execute(Object... inParams) {
        Map<String, Object> paramsToUse = new HashMap<String, Object>();
        validateParameters(inParams);
        int i = 0;
        for (SqlParameter sqlParameter : getDeclaredParameters()) {
            if (sqlParameter.isInputValueProvided()) {
                if (i < inParams.length) {
                    paramsToUse.put(sqlParameter.getName(), inParams[i++]);
                }
            }
        }
        return dao.getJdbcTemplate().call(newCallableStatementCreator(paramsToUse), getDeclaredParameters());
    }

    /**
     * Execute the stored procedure. Subclasses should define a strongly typed
     * execute method (with a meaningful name) that invokes this method,
     * populating the input map and extracting typed values from the output map.
     * Subclass execute methods will often take domain objects as arguments and
     * return values. Alternatively, they can return void.
     * 
     * @param inParams
     *            map of input parameters, keyed by name as in parameter
     *            declarations. Output parameters need not (but can) be included
     *            in this map. It is legal for map entries to be
     *            <code>null</code>, and this will produce the correct behavior
     *            using a NULL argument to the stored procedure.
     * @return map of output params, keyed by name as in parameter declarations.
     *         Output parameters will appear here, with their values after the
     *         stored procedure has been called.
     */
    public Map<String, Object> execute(Map<String, ?> inParams) throws DataAccessException {
        validateParameters(inParams.values().toArray());
        return dao.getJdbcTemplate().call(newCallableStatementCreator(inParams), getDeclaredParameters());
    }

}
