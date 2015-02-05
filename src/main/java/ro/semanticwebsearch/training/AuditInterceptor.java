package ro.semanticwebsearch.training;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Iterator;

public class AuditInterceptor extends EmptyInterceptor{

	private static final long serialVersionUID = 1L;

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		// TODO Auto-generated method stub
		System.out.println("Saving an entity!");
		return false;
	}

	@Override
	public void postFlush(Iterator entities) {
		System.out.println("After entity has been flushed!");
	}


}
