package taehyeon.brothers.matchreal.infrastructure.config

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class QuerydslRepositorySupport(
    domainClass: Class<*>,
) : QuerydslRepositorySupport(domainClass) {
    @PersistenceContext
    override fun setEntityManager(entityManager: EntityManager) {
        super.setEntityManager(entityManager)
    }
}
