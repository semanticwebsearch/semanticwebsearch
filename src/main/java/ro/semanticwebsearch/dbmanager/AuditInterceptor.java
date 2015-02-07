package ro.semanticwebsearch.dbmanager;

import org.apache.log4j.Logger;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;

class AuditInterceptor extends EmptyInterceptor{

	private static final long serialVersionUID = 1L;
    public static Logger log = Logger.getLogger(AuditInterceptor.class.getCanonicalName());

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		if(log.isInfoEnabled()) {
            log.info("Entity saved! " + entity.toString());
        }

		return false;
	}

}
