package name.abhijitsarkar.javaee.userpref.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import lombok.extern.slf4j.Slf4j;
import name.abhijitsarkar.javaee.userpref.domain.UserPreference;
import oracle.jdbc.OracleTypes;

@Slf4j
public class GetUserPreferenceSproc extends StoredProcedure {
    static final String NAME = "TE_GET_USER_PREFERENCES_SP";

    static final String USERNAME = "userloginid_i";
    static final String PASSWORD = "userpassword_i";
    static final String SERVICE_ID = "service_id_i";
    static final String PREFERENCE_NAME = "list_name_i";
    static final String PARTNER_ID = "partnerid_i";
    static final String USER_PREFERENCE = "recordset_o";

    public GetUserPreferenceSproc(JdbcTemplate jdbcTemplate) {
	super(jdbcTemplate, NAME);
	this.setFunction(false);

	declareParameter(
		new SqlParameter(new SqlParameter(USERNAME, Types.VARCHAR)));
	declareParameter(
		new SqlParameter(new SqlParameter(PASSWORD, Types.VARCHAR)));
	declareParameter(
		new SqlParameter(new SqlParameter(SERVICE_ID, Types.VARCHAR)));
	declareParameter(new SqlParameter(
		new SqlParameter(PREFERENCE_NAME, Types.VARCHAR)));
	declareParameter(
		new SqlParameter(new SqlParameter(PARTNER_ID, Types.VARCHAR)));
	declareParameter(new SqlOutParameter(USER_PREFERENCE,
		OracleTypes.CURSOR, new UserPreferenceExtractor()));

	compile();
    }

    @SuppressWarnings("unchecked")
    public Object execute(String username, String password, int serviceId,
	    String partnerId, String name) {
	log.warn(
		"Username: {}, Password: {}, Service ID: {}, Partner ID: {}, Name: {}.",
		username, password, serviceId, partnerId, name);

	Map<String, Object> results = super.execute(username, password,
		serviceId, name, partnerId);

	List<UserPreference> userPreferences = (List<UserPreference>) results
		.get(USER_PREFERENCE);
	UserPreference userPreference = userPreferences.get(0);

	if (userPreference != null) {
	    userPreference.setName(name);
	    userPreference.setServiceId(serviceId);
	}

	return userPreference;
    }

    final class UserPreferenceExtractor implements RowMapper<UserPreference> {
	final static int SUCCESS = 0;

	@Override
	public UserPreference mapRow(ResultSet rs, int arg1)
		throws SQLException {
	    int status = rs.getInt(1);

	    if (status != SUCCESS) {
		log.warn(
			"Failed to retrieve user preferences. Database returned: {}.",
			status);

		return null;
	    }

	    UserPreference userPreference = new UserPreference();
	    userPreference.setValue(rs.getString(2));

	    return userPreference;
	}
    }
}