package org.apereo.cas.web.flow;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.configurer.AbstractCasMultifactorWebflowConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * The {@link TrustedAuthenticationWebflowConfigurer} is responsible for
 * adjusting the CAS webflow context for trusted authn integration.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
@Slf4j
public class TrustedAuthenticationWebflowConfigurer extends AbstractCasMultifactorWebflowConfigurer {

    public TrustedAuthenticationWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
                                                  final FlowDefinitionRegistry loginFlowDefinitionRegistry,
                                                  final ApplicationContext applicationContext,
                                                  final CasConfigurationProperties casProperties) {
        super(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties);
    }

    @Override
    protected void doInitialize() {
        final Flow flow = getLoginFlow();
        if (flow != null) {
            final ActionState actionState = createActionState(flow, CasWebflowConstants.ACTION_ID_REMOTE_TRUSTED_AUTHENTICATION, createEvaluateAction("remoteUserAuthenticationAction"));
            createTransitionForState(actionState, CasWebflowConstants.TRANSITION_ID_SUCCESS, CasWebflowConstants.STATE_ID_SEND_TICKET_GRANTING_TICKET);
            createTransitionForState(actionState, CasWebflowConstants.TRANSITION_ID_ERROR, getStartState(flow).getId());
            createTransitionForState(actionState, CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE, CasWebflowConstants.STATE_ID_HANDLE_AUTHN_FAILURE);
            actionState.getExitActionList().add(createEvaluateAction("clearWebflowCredentialsAction"));

            setStartState(flow, actionState);
        }
    }
}
