package name.abhijitsarkar.javaee.salon.appointment.service;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;

public class PageAwareMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object filter(Object filterTarget, Expression filterExpression, EvaluationContext ctx) {
		if (filterTarget instanceof Page) {
			Page page = (Page) filterTarget;
			List elements = newArrayList(page.getContent());

			// DefaultMethodSecurityExpressionHandler tries to clear the
			// collection
			elements = (List) super.filter(elements, filterExpression, ctx);

			PageRequest pageRequest = new PageRequest(page.getNumber(), page.getSize(), page.getSort());

			return new PageImpl(elements, pageRequest, elements.size());
		}

		return super.filter(filterTarget, filterExpression, ctx);
	}
}
