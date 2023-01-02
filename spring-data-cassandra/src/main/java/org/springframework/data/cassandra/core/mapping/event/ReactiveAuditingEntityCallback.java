/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.cassandra.core.mapping.event;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.core.Ordered;
import org.springframework.data.auditing.ReactiveIsNewAwareAuditingHandler;
import org.springframework.data.mapping.callback.EntityCallback;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.util.Assert;

import com.datastax.oss.driver.api.core.CqlIdentifier;

/**
 * Reactive {@link EntityCallback} to populate auditing related fields on an entity about to be saved.
 *
 * @author Mark Paluch
 * @since 2.2
 */
public class ReactiveAuditingEntityCallback implements ReactiveBeforeConvertCallback<Object>, Ordered {

	private final ObjectFactory<ReactiveIsNewAwareAuditingHandler> auditingHandlerFactory;

	/**
	 * Creates a new {@link ReactiveAuditingEntityCallback} using the given {@link MappingContext} and
	 * {@link ReactiveIsNewAwareAuditingHandler} provided by the given {@link ObjectFactory}.
	 *
	 * @param auditingHandlerFactory must not be {@literal null}.
	 */
	public ReactiveAuditingEntityCallback(ObjectFactory<ReactiveIsNewAwareAuditingHandler> auditingHandlerFactory) {

		Assert.notNull(auditingHandlerFactory, "IsNewAwareAuditingHandler must not be null!");
		this.auditingHandlerFactory = auditingHandlerFactory;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.cassandra.core.mapping.event.ReactiveBeforeConvertCallback#onBeforeConvert(java.lang.Object, com.datastax.oss.driver.api.core.CqlIdentifier)
	 */
	@Override
	public Mono<Object> onBeforeConvert(Object entity, CqlIdentifier tableName) {
		return auditingHandlerFactory.getObject().markAudited(entity);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	@Override
	public int getOrder() {
		return 100;
	}
}
