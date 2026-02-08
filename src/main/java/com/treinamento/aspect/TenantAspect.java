package com.treinamento.aspect;

import com.treinamento.config.multitenant.TenantContext;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TenantAspect {

    @Autowired
    private EntityManager entityManager;

    @Before("execution(* com.treinamento.repository.*.*(..)) " +
            "&& !within(com.treinamento.TreinamentoApplication) " +
            "&& !within(com.treinamento.config.CustomUserDetailsService) " +
            "&& !within(com.treinamento.controller.AuthController) " +
            "&& !within(com.treinamento.controller.PeriodoController) " +
            "&& !within(com.treinamento.controller.EmpresaController)")
    public void beforeExecution() {
        String tenantId = TenantContext.getCurrentTenant();
        
        // Se o tenant for "000" (ou algum marcador de admin global), podemos ignorar o filtro
        // Mas por padrão, habilitamos o filtro se houver um tenant ID
        if (tenantId != null && !tenantId.isEmpty()) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
        }
    }
}
